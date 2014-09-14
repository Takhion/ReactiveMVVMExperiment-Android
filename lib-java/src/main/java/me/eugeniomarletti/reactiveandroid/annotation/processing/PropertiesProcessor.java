package me.eugeniomarletti.reactiveandroid.annotation.processing;

import com.squareup.javawriter.JavaWriter;
import me.eugeniomarletti.reactiveandroid.annotation.GenerateProperties;
import me.eugeniomarletti.reactiveandroid.annotation.GenerateProperty;
import me.eugeniomarletti.reactiveandroid.annotation.PropertiesProvider;
import me.eugeniomarletti.reactiveandroid.property.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.LocalDate;
import rx.Observable;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.EnumSet.of;
import static javax.lang.model.element.Modifier.*;

//TODO use Velocity Templates?
//TODO restrict to class only
//TODO proxy class extension through annotation (@Extends?) (eg. Person > _Person > BasePerson)
public class PropertiesProcessor extends AbstractProcessor
{
    //TODO more descriptive error!
    protected static final String NULL_POINTER_ERROR = "The order of the generated properties matters!";

    //TODO configure prefix/name through annotation
    protected String classPrefix = "_";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        final Messager messager = processingEnv.getMessager();
        final Filer filer = processingEnv.getFiler();

        for (Element elem : roundEnv.getElementsAnnotatedWith(GenerateProperties.class))
        {
            final Element klass = elem;

            String fullName = klass.toString();
            String[] package_class = splitPackage(fullName);
            String packageName = package_class[0];
            String className = package_class[1];
            className = classPrefix + className;
            fullName = packageName + "." + className;

            try
            {
                JavaFileObject jfo = filer.createSourceFile(fullName, klass);
                Writer writer = jfo.openWriter();
                writer.write(process(packageName, className, elem));
                writer.flush();
                writer.close();
            }
            catch (IOException e)
            {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Can't generate Properties:\n" + printStackTrace(e),
                        elem);
            }
        }
        return true; // no further processing of this annotation type
    }

    public String process(String packageName, String className, Element klass) throws IOException
    {
        final String fullName = packageName + "." + className;
        final GenerateProperty[] properties = klass.getAnnotation(GenerateProperties.class).value();

        final Set<String> imports = new HashSet<>();
        imports.add(Property.class.getCanonicalName());
        imports.add(NotNull.class.getCanonicalName());
        imports.add(LocalDate.class.getCanonicalName());
        imports.add(Observable.class.getCanonicalName());
        imports.add(Collections.class.getCanonicalName());
        imports.add(HashSet.class.getCanonicalName());
        imports.add(Set.class.getCanonicalName());
        for (GenerateProperty property : properties)
        {
            imports.add(getType(property));
        }

        final StringWriter sw = new StringWriter();
        final JavaWriter w = new JavaWriter(sw);

        w.emitPackage(packageName)
         .emitImports(imports)
         .emitEmptyLine()
         .beginType(fullName, "class", of(ABSTRACT), null, PropertiesProvider.class.getCanonicalName())
         .emitEmptyLine()
        ;

        w.beginConstructor(of(PROTECTED), "@NotNull Object...", "args")
        ;
        if (properties.length > 0)
        {
            w.beginControlFlow("try");
        }
        for (GenerateProperty property : properties)
        {
            w.emitStatement("%s = build%sProperty(args)", property.name(), capitalize(property.name()));
        }
        if (properties.length > 0)
        {
            w.nextControlFlow("catch (NullPointerException e)")
             .emitStatement("final RuntimeException e2 = new NullPointerException(\"%s\")", NULL_POINTER_ERROR)
             .emitStatement("e2.initCause(e)")
             .emitStatement("throw e2")
             .endControlFlow()
             .emitEmptyLine()
            ;
        }
        w.emitStatement("final Set<Property> properties = buildPropertiesSet(args)")
         .emitStatement("this.properties = properties != null " +
                                "? Collections.unmodifiableSet(properties) : Collections.emptySet()")
         .endConstructor()
         .emitEmptyLine()
        ;

        w.emitField("Set<Property>", "properties", of(PROTECTED, FINAL))
         .emitEmptyLine()
         .beginMethod("Set<Property>", "buildPropertiesSet", of(PROTECTED), "@NotNull Object...", "args")
         .emitStatement("final Set<Property> properties = new HashSet<>(%d)", properties.length)
        ;
        for (GenerateProperty property : properties)
        {
            w.emitStatement("properties.add(%s)", property.name());
        }
        w.emitStatement("return properties")
         .endMethod()
         .emitEmptyLine()
         .emitAnnotation("Override")
         .beginMethod("Set<Property>", "getProperties", of(PUBLIC))
         .emitStatement("return properties")
         .endMethod()
         .emitEmptyLine()
        ;

        for (GenerateProperty property : properties)
        {
            final String typeFull = getType(property);
            final String typeClass = splitPackage(typeFull)[1];
            final String name = property.name();
            final String nameCapitalized = capitalize(name);
            w.emitField("Property<" + typeClass + ">", name, of(PROTECTED, FINAL))
             .emitEmptyLine()
             .emitAnnotation("NotNull")
             .beginMethod("Property<" + typeClass + ">", "build" + nameCapitalized + "Property",
                          of(ABSTRACT, PROTECTED), "@NotNull Object...", "args")
             .endMethod()
             .emitEmptyLine()
             .emitAnnotation("NotNull")
             .beginMethod("Property<" + typeClass + ">", name, of(PUBLIC))
             .emitStatement("return %s", name)
             .endMethod()
             .emitEmptyLine()
            ;

            if (property.get())
            {
                w.beginMethod(typeClass, "get" + nameCapitalized, of(PUBLIC))
                 .emitStatement("return %s.get()", name)
                 .endMethod()
                 .emitEmptyLine()
                ;
            }

            if (property.set())
            {
                w.beginMethod("void", "set" + nameCapitalized, of(PUBLIC), typeClass, name)
                 .emitStatement("this.%s.set(%s)", name, name)
                 .endMethod()
                 .emitEmptyLine()
                ;
            }

            if (property.observe())
            {
                w.emitAnnotation("NotNull")
                 .beginMethod("Observable<" + typeClass + ">", "observe" + nameCapitalized, of(PUBLIC))
                 .emitStatement("return %s.observe()", name)
                 .endMethod()
                 .emitEmptyLine()
                ;
            }
        }

        // workaround for a stupid lint warning
        w.emitAnnotation("Override")
         .beginMethod("int", "hashCode", of(PUBLIC))
         .emitStatement("return super.hashCode()")
         .endMethod()
        ;

        w.endType();

        return sw.toString();
    }

    private String capitalize(@Nullable String s)
    {
        if (s == null || s.length() == 0) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String[] splitPackage(String fullName)
    {
        final String packageName, className;
        final int p = fullName.lastIndexOf(".");
        if (p == -1)
        {
            packageName = "";
            className = fullName;
        }
        else
        {
            packageName = fullName.substring(0, p);
            className = fullName.substring(Math.min(p + 1, fullName.length()));
        }
        return new String[] { packageName, className };
    }

    private String getType(GenerateProperty property)
    {
        try
        {
            return property.type().getCanonicalName();
        }
        catch (MirroredTypeException mte)
        {
            return mte.getTypeMirror().toString();
        }
    }

    private String printStackTrace(Throwable e)
    {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> supportTypes = new HashSet<>();
        supportTypes.add(GenerateProperties.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }
}

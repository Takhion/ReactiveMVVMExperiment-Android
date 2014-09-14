package me.eugeniomarletti.reactiveandroid.annotation.processing;

import me.eugeniomarletti.reactiveandroid.annotation.PublicProperty;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("me.eugeniomarletti.reactiveandroid.annotation.PublicProperty")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PropertiesProcessor extends AbstractProcessor
{
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for (Element elem : roundEnv.getElementsAnnotatedWith(PublicProperty.class))
        {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format
                    ("Property found in %s: %s",
                     elem.getEnclosingElement().getSimpleName(),
                     elem.getSimpleName()));
        }
        return true; // no further processing of this annotation type
    }
}

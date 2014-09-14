package me.eugeniomarletti.reactiveandroid.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Repeatable(GenerateProperties.class)
public @interface GenerateProperty
{
    //TODO add "dependsOn" for compile-time checking of correct initialisation order!
    String name();
    Class type();
    boolean get() default true;
    boolean set() default true;
    boolean observe() default true;
}

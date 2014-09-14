package me.eugeniomarletti.reactiveandroid.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface PublicProperty
{
    boolean readOnly() default false;
}

package me.eugeniomarletti.reactiveandroid.annotation;

import me.eugeniomarletti.reactiveandroid.property.Property;

import java.util.Set;

public interface PropertiesProvider
{
    public Set<Property> getProperties();
}

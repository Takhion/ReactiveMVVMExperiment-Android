package me.eugeniomarletti.reactivemvvmexperiment.model;

import me.eugeniomarletti.reactiveandroid.collection.ObservableList;
import org.jetbrains.annotations.NotNull;

public class People
{
    protected static final ObservableList<Person> people = new ObservableList<>();

    protected People()
    {
    }

    @NotNull
    public static ObservableList<Person> people()
    {
        return people;
    }
}

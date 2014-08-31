package me.eugeniomarletti.reactivemvvmexperiment.viewmodel;

import me.eugeniomarletti.reactivemvvmexperiment.model.People;
import me.eugeniomarletti.reactivemvvmexperiment.model.Person;
import me.eugeniomarletti.reactiveandroid.collection.ObservableList;

public class ListViewModel
{
    protected final ObservableList<Person> people = People.people();

    public ObservableList<Person> people()
    {
        return people;
    }
}

package me.eugeniomarletti.reactivemvvmexperiment;

import me.eugeniomarletti.reactiveandroid.collection.ObservableList;
import me.eugeniomarletti.reactivemvvmexperiment.model.People;
import me.eugeniomarletti.reactivemvvmexperiment.model.Person;
import org.joda.time.LocalDate;

public class Application extends android.app.Application
{
    protected final Person[] PEOPLE = new Person[]
            {
                    new Person("Joshua May", new LocalDate(1984, 3, 29)),
                    new Person("Eugenio Marletti", new LocalDate(1985, 4, 18)),
                    new Person("Harry Houdini", new LocalDate(1947, 2, 1))
            };

    protected void initialize()
    {
        final ObservableList<Person> peopleList = People.people();
        for (Person person : PEOPLE) peopleList.add(person);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        initialize();
    }
}

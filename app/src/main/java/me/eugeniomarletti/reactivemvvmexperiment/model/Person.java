package me.eugeniomarletti.reactivemvvmexperiment.model;

import me.eugeniomarletti.reactiveandroid.property.ImmutableProperty;
import me.eugeniomarletti.reactiveandroid.property.ObservableProperty;
import me.eugeniomarletti.reactiveandroid.property.Property;
import me.eugeniomarletti.reactiveandroid.utils.Today;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import rx.Observable;

public class Person
{
    protected final Property<String>    name;
    protected final Property<LocalDate> dateOfBirth;
    protected final Property<Integer>   age;

    public Person(String name, LocalDate dateOfBirth)
    {
        this.name = new ImmutableProperty<>(name);
        this.dateOfBirth = new ImmutableProperty<>(dateOfBirth);

        final ObservableProperty<Integer> age = new ObservableProperty<>();
        Observable.combineLatest(this.dateOfBirth.observe(), Today.observe(),
                                 (_dateOfBirth, today) -> computeAge(_dateOfBirth, today))
                  .subscribe(_age -> age.set(_age));
        this.age = age.asReadOnly();
    }

    public static int computeAge(@NotNull LocalDate dateOfBirth, @NotNull LocalDate now)
    {
        return Years.yearsBetween(dateOfBirth, now).getYears();
    }

    @NotNull
    public Property<String> name()
    {
        return name;
    }

    @NotNull
    public Property<LocalDate> dateOfBirth()
    {
        return dateOfBirth;
    }

    @NotNull
    public Property<Integer> age()
    {
        return age.asReadOnly();
    }

    @Override
    public boolean equals(@Nullable Object o)
    {
        return o != null && name.get() != null && o instanceof Person && name.get().equals(((Person)o).name.get());
    }

    @Override
    public int hashCode()
    {
        final String name = this.name.get();
        return name == null ? super.hashCode() : name.hashCode();
    }
}

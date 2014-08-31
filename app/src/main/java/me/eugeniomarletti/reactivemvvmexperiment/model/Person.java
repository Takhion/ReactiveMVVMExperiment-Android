package me.eugeniomarletti.reactivemvvmexperiment.model;

import me.eugeniomarletti.reactiveandroid.property.ComputedProperty;
import me.eugeniomarletti.reactiveandroid.property.ImmutableProperty;
import me.eugeniomarletti.reactiveandroid.property.Property;
import me.eugeniomarletti.reactiveandroid.utils.Today;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.LocalDate;
import org.joda.time.Years;

public class Person
{
    protected final Property<String>    name;
    protected final Property<LocalDate> dateOfBirth;
    protected final Property<Integer>   age;

    public Person(String name, LocalDate dateOfBirth)
    {
        this.name = new ImmutableProperty<>(name);
        this.dateOfBirth = new ImmutableProperty<>(dateOfBirth);
        age = ComputedProperty.make(
                this.dateOfBirth.observe(),Today.observe(),
                (_dateOfBirth, today) -> computeAge(_dateOfBirth, today));
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

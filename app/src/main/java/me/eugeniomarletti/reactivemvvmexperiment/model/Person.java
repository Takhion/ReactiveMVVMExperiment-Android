package me.eugeniomarletti.reactivemvvmexperiment.model;

import me.eugeniomarletti.reactiveandroid.annotation.GenerateProperty;
import me.eugeniomarletti.reactiveandroid.property.ComputedProperty;
import me.eugeniomarletti.reactiveandroid.property.ImmutableProperty;
import me.eugeniomarletti.reactiveandroid.property.Property;
import me.eugeniomarletti.reactiveandroid.utils.Today;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.LocalDate;
import org.joda.time.Years;

@GenerateProperty(
        name = "name",
        type = String.class,
        set = false
)
@GenerateProperty(
        name = "dateOfBirth",
        type = LocalDate.class,
        set = false
)
@GenerateProperty(
        name = "age",
        type = Integer.class,
        set = false
)
public class Person extends _Person
{
    public Person(@NotNull String name, @NotNull LocalDate dateOfBirth)
    {
        super(name, dateOfBirth);
    }

    @NotNull
    @Override
    protected Property<String> buildNameProperty(@NotNull Object... args)
    {
        return new ImmutableProperty<>((String)args[0]);
    }

    @NotNull
    @Override
    protected Property<LocalDate> buildDateOfBirthProperty(@NotNull Object... args)
    {
        return new ImmutableProperty<>((LocalDate)args[1]);
    }

    @NotNull
    @Override
    protected Property<Integer> buildAgeProperty(@NotNull Object... args)
    {
        return ComputedProperty.make(
                dateOfBirth.observe(), Today.observe(),
                (_dateOfBirth, today) -> Years.yearsBetween(_dateOfBirth, today).getYears());
    }

    @Override
    public boolean equals(@Nullable Object o)
    {
        return o instanceof Person && name.get() != null && name.get().equals(((Person)o).name.get());
    }

    @Override
    public int hashCode()
    {
        final String name = this.name.get();
        return name == null ? super.hashCode() : name.hashCode();
    }
}

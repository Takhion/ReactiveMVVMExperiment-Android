package me.eugeniomarletti.reactiveandroid.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;

public class ImmutableProperty<T> implements Property<T>
{
    protected final T value;

    public ImmutableProperty(@Nullable T value)
    {
        this.value = value;
    }

    @Override
    public T get()
    {
        return value;
    }

    @Override
    public void set(@Nullable T value)
    {
        throw new UnsupportedOperationException("This Property is read-only.");
    }

    @NotNull
    @Override
    public Observable<T> observe()
    {
        return Observable.just(value);
    }

    @Override
    public boolean isReadOnly()
    {
        return true;
    }

    @NotNull
    @Override
    public Property<T> asReadOnly()
    {
        return this;
    }
}

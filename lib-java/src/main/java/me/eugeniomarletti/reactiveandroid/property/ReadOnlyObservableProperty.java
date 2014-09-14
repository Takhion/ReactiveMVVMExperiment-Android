package me.eugeniomarletti.reactiveandroid.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;

public class ReadOnlyObservableProperty<T> implements Property<T>
{
    protected final ObservableProperty<T> p;

    public static <T> ReadOnlyObservableProperty<T> create(@NotNull ObservableProperty<T> p)
    {
        //noinspection ConstantConditions
        return p == null ? null : new ReadOnlyObservableProperty<>(p);
    }

    public ReadOnlyObservableProperty(@NotNull ObservableProperty<T> p)
    {
        //noinspection ConstantConditions
        if (p == null) throw new IllegalStateException("Can't wrap a null ObservableProperty.");
        this.p = p;
    }

    @Override
    public T get()
    {
        return p.get();
    }

    @Override
    public void set(@Nullable T value)
    {
        throw new UnsupportedOperationException("This ObservableProperty is read-only.");
    }

    @NotNull
    @Override
    public Observable<T> observe()
    {
        return p.observe();
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

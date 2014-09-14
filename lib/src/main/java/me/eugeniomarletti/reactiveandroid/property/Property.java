package me.eugeniomarletti.reactiveandroid.property;

import org.jetbrains.annotations.NotNull;
import rx.Observable;

public interface Property<T>
{
    public T get();
    public void set(T value);
    @NotNull
    public Observable<T> observe();

    public boolean isReadOnly();
    @NotNull
    public Property<T> asReadOnly();
}

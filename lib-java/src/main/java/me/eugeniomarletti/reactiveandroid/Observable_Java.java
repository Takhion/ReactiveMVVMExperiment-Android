package me.eugeniomarletti.reactiveandroid;

import me.eugeniomarletti.reactiveandroid.operators.WeakReferenceOperator;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public class Observable_Java
{
    protected Observable_Java() { }

    @NotNull
    public static <T> Observable<T> weakReference(@NotNull Observable<T> observable)
    {
        return observable.lift(new WeakReferenceOperator<>());
    }
}

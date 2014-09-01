package me.eugeniomarletti.reactiveandroid.property;

import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.functions.Func2;

public class ComputedProperty
{
    protected ComputedProperty() { }

    @NotNull
    public static <T1, T2, R> Property<R> make(
            @NotNull Observable<? extends T1> o1,
            @NotNull Observable<? extends T2> o2,
            @NotNull Func2<? super T1, ? super T2, ? extends R> computeFunction)
    {
        //TODO copy all overloaded versions of 'combineLatest'
        final ObservableProperty<R> p = new ObservableProperty<>();
        Observable.combineLatest(o1, o2, computeFunction)
                  .subscribe(value -> p.set(value));
        return p.asReadOnly();
    }
}

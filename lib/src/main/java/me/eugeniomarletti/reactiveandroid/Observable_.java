package me.eugeniomarletti.reactiveandroid;

import android.app.Activity;
import me.eugeniomarletti.reactiveandroid.operators.UnsubscribeOnActivityDestroyOperator;
import me.eugeniomarletti.reactiveandroid.operators.WeakReferenceOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class Observable_
{
    protected Observable_() { }

    @NotNull
    public static <T> Observable<T> weakReference(@NotNull Observable<T> observable)
    {
        return observable.lift(new WeakReferenceOperator<>());
    }

    @NotNull
    public static <T> Observable<T> unsubscribeOnActivityDestroy(@NotNull Observable<T> observable,
                                                                 @Nullable Activity activity)
    {
        return activity == null ? observable
                                : observable.lift(new UnsubscribeOnActivityDestroyOperator<>(activity));
    }

    @NotNull
    public static <T> Observable<T> androidSafe(@NotNull Observable<T> observable, @Nullable Activity activity)
    {
        return unsubscribeOnActivityDestroy(observable.observeOn(AndroidSchedulers.mainThread()), activity);
    }
}

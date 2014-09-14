package me.eugeniomarletti.reactiveandroid.operators;

import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

import java.lang.ref.WeakReference;

public class WeakReferenceOperator<T> implements Observable.Operator<T, T>
{
    @Override
    public Subscriber<? super T> call(Subscriber<? super T> subscriber)
    {
        return new WeakSubscriber<>(subscriber);
    }

    public static class WeakSubscriber<T> extends Subscriber<T>
    {
        protected final WeakReference<Subscriber<T>> original;

        public WeakSubscriber(Subscriber<T> original)
        {
            this.original = new WeakReference<>(original);
        }

        @Override
        public void onCompleted()
        {
            check(subscriber -> subscriber.onCompleted());
        }

        @Override
        public void onError(Throwable e)
        {
            check(subscriber -> subscriber.onError(e));
        }

        @Override
        public void onNext(T t)
        {
            check(subscriber -> subscriber.onNext(t));
        }

        private void check(@NotNull Action1<Subscriber<T>> action)
        {
            final Subscriber<T> s = original.get();
            if (s != null) action.call(s);
            else unsubscribe();
        }
    }
}

package me.eugeniomarletti.reactiveandroid.operators;

import rx.Observable;
import rx.Subscriber;

import java.lang.ref.WeakReference;

public class WeakReferenceOperator<T> implements Observable.Operator<T, T>
{
    @Override
    public Subscriber<? super T> call(Subscriber<? super T> subscriber)
    {
        return subscriber == null ? null : new WeakSubscriber<>(subscriber);
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
            final Subscriber<T> s = original.get();
            if (s != null) s.onCompleted();
            else unsubscribe();
        }

        @Override
        public void onError(Throwable e)
        {
            final Subscriber<T> s = original.get();
            if (s != null) s.onError(e);
            else unsubscribe();
        }

        @Override
        public void onNext(T t)
        {
            final Subscriber<T> s = original.get();
            if (s != null) s.onNext(t);
            else unsubscribe();
        }
    }
}

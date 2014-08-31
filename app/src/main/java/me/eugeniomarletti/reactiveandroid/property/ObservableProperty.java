package me.eugeniomarletti.reactiveandroid.property;

import android.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class ObservableProperty<T> implements Property<T>
{
    protected T value;
    protected BehaviorSubject<T> subject = BehaviorSubject.create();
    protected Observable<T> observable = subject.asObservable();

    public ObservableProperty()
    {
    }

    public ObservableProperty(@Nullable T value)
    {
        set(value);
    }

    @Override
    public T get()
    {
        return value;
    }

    @Override
    public synchronized void set(@Nullable T value)
    {
        if (this.value != value)
        {
            Log.w("ObservableProperty", String.format("old value: %s | new value: %s",
                                                      this.value == null ? "null" : this.value,
                                                      value == null ? "null" : value));
            this.value = value;
            subject.onNext(value);
        }
    }

    @NotNull
    @Override
    public Observable<T> observe()
    {
        return observable;
    }

    @Override
    public boolean isReadOnly()
    {
        return false;
    }

    @NotNull
    @Override
    public Property<T> asReadOnly()
    {
        return ReadOnlyObservableProperty.create(this);
    }
}

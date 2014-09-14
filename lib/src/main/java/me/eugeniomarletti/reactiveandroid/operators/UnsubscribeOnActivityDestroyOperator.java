package me.eugeniomarletti.reactiveandroid.operators;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

public class UnsubscribeOnActivityDestroyOperator<T> implements Observable.Operator<T, T>
{
    protected final WeakReference<Activity> weakActivity;

    public UnsubscribeOnActivityDestroyOperator(@NotNull Activity activity)
    {
        weakActivity = new WeakReference<>(activity);
    }

    @Override
    public Subscriber<? super T> call(Subscriber<? super T> subscriber)
    {
        if (subscriber != null)
        {
            final Activity activity = weakActivity.get();
            if (activity != null)
            {
                subscriber.add(Subscriptions.create(() -> Callbacks.onUnsubscribe(weakActivity, subscriber)));
                Callbacks.add(activity.getApplication(), activity, subscriber);
            }
        }
        return subscriber;
    }

    protected static class Callbacks implements Application.ActivityLifecycleCallbacks
    {
        protected static Callbacks   instance;
        protected static Application application;

        protected final WeakHashMap<Object, Set<Subscriber<?>>> map = new WeakHashMap<>();

        protected Callbacks() { }

        public static void add(@NotNull Application application,
                               @Nullable Object ref, @NotNull Subscriber<?> subscriber)
        {
            if (instance == null)
            {
                Callbacks.application = application;
                instance = new Callbacks();
                application.registerActivityLifecycleCallbacks(instance);
            }
            instance._add(ref, subscriber);
        }

        protected void _add(@Nullable Object ref, @NotNull Subscriber<?> subscriber)
        {
            if (ref != null)
            {
                Set<Subscriber<?>> set = map.get(ref);
                if (set == null) set = new HashSet<>();
                set.add(subscriber);
                map.put(ref, set);
            }
        }

        public static void onUnsubscribe(@Nullable WeakReference<?> weakRef, @Nullable Subscriber<?> subscriber)
        {
            if (instance != null) instance._onUnsubscribe(weakRef, subscriber);
        }

        protected void _onUnsubscribe(@Nullable WeakReference<?> weakRef, @Nullable Subscriber<?> subscriber)
        {
            if (weakRef != null)
            {
                final Object ref = weakRef.get();
                if (ref != null)
                {
                    final Set<Subscriber<?>> set = map.get(ref);
                    if (set != null)
                    {
                        if (subscriber != null) set.remove(subscriber);
                        if (set.size() == 0) map.remove(ref);
                    }
                    cleanup();
                }
            }
        }

        protected void onActivityDestroyed(@NotNull Activity activity, boolean checkIsFinishing)
        {
            //noinspection ConstantConditions
            if (activity != null && (!checkIsFinishing || activity.isFinishing()))
            {
                final Set<Subscriber<?>> set = map.remove(activity);
                if (set != null)
                {
                    for (Subscriber<?> s : set)
                    {
                        s.unsubscribe();
                    }
                }
                cleanup();
            }
        }

        protected static void cleanup()
        {
            if (instance != null && instance.map.size() == 0)
            {
                application.unregisterActivityLifecycleCallbacks(instance);
                instance = null;
                application = null;
            }
        }

        // Application.ActivityLifecycleCallbacks

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState)
        {
            onActivityDestroyed(activity, true);
        }

        @Override
        public void onActivityStarted(Activity activity)
        {
            onActivityDestroyed(activity, true);
        }

        @Override
        public void onActivityResumed(Activity activity)
        {
            onActivityDestroyed(activity, true);
        }

        @Override
        public void onActivityPaused(Activity activity)
        {
            onActivityDestroyed(activity, true);
        }

        @Override
        public void onActivityStopped(Activity activity)
        {
            onActivityDestroyed(activity, true);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState)
        {
            onActivityDestroyed(activity, true);
        }

        @Override
        public void onActivityDestroyed(Activity activity)
        {
            onActivityDestroyed(activity, false);
        }
    }
}

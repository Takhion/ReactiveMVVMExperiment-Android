package me.eugeniomarletti.reactiveandroid.collection;

import android.app.Activity;
import android.widget.BaseAdapter;
import me.eugeniomarletti.reactiveandroid.Observable_Android;
import org.jetbrains.annotations.NotNull;

public abstract class ReactiveAdapter<T> extends BaseAdapter
{
    protected final ObservableList<T> observableList;

    public ReactiveAdapter(@NotNull Activity activity, @NotNull ObservableList<T> observableList)
    {
        this.observableList = observableList;
        Observable_Android.androidSafe(observableList.observe(), activity)
                    .subscribe($ -> notifyDataSetChanged());
    }

    public T _getItem(int position)
    {
        return observableList.get().get(position);
    }

    @Override
    public int getCount()
    {
        return observableList.get().size();
    }

    @Override
    public Object getItem(int position)
    {
        return _getItem(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }
}

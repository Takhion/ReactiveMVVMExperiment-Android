package me.eugeniomarletti.reactiveandroid.collection;

import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

//TODO this must become a *real* List!
public class ObservableList<T>
{
    protected final List<T> list;
    protected PublishSubject<ListChange<T>> subject = PublishSubject.create();

    public ObservableList()
    {
        list = new ArrayList<>();
    }

    public ObservableList(int capacity)
    {
        list = new ArrayList<>(capacity);
    }

    public ObservableList(Collection<? extends T> collection)
    {
        list = new ArrayList<>(collection);
    }

    public void add(T item)
    {
        list.add(item);
        subject.onNext(new ListChange<>(true, item, list.size() - 1));
    }

    public void remove(T item)
    {
        final int position = list.indexOf(item);
        if (position != -1)
        {
            list.remove(item);
            subject.onNext(new ListChange<>(false, item, position));
        }
    }

    public List<T> get()
    {
        return Collections.unmodifiableList(list);
    }

    @NotNull
    public Observable<ListChange<T>> observe()
    {
        return subject.asObservable();
    }

    public static class ListChange<T>
    {
        public final boolean addOrRemove; // true:add false:remove
        public final T       item;
        public final int     position;

        public ListChange(boolean addOrRemove, T item, int position)
        {
            this.addOrRemove = addOrRemove;
            this.item = item;
            this.position = position;
        }
    }
}

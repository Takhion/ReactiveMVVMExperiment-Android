package me.eugeniomarletti.reactiveandroid.collection;

import java.util.ArrayList;
import java.util.Collection;

public class ObservableList2<T> extends ArrayList<T>
{
    public ObservableList2(int capacity)
    {
        super(capacity);
    }

    public ObservableList2()
    {
    }

    public ObservableList2(Collection<? extends T> collection)
    {
        super(collection);
    }

    @Override
    public boolean add(T object)
    {
        return super.add(object);
    }

    @Override
    public void add(int index, T object)
    {
        super.add(index, object);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        return super.addAll(collection);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> collection)
    {
        return super.addAll(index, collection);
    }

    @Override
    public void clear()
    {
        super.clear();
    }

    @Override
    public T remove(int index)
    {
        return super.remove(index);
    }

    @Override
    public boolean remove(Object object)
    {
        return super.remove(object);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex)
    {
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public T set(int index, T object)
    {
        return super.set(index, object);
    }

    @Override
    public boolean removeAll(Collection<?> collection)
    {
        return super.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection)
    {
        return super.retainAll(collection);
    }
}

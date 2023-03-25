package br.net.rankup.spawners.drops;

import java.util.*;
import java.util.function.*;
import com.google.common.collect.*;

public abstract class Cache<T>
{
    private final List<T> elements;
    
    public Cache() {
        this.elements = new LinkedList<T>();
    }
    
    public <E> List<E> map(final Function<T, E> function) {
        final List<E> copy = new LinkedList<E>();
        for (final T element : this.elements) {
            copy.add(function.apply(element));
        }
        return copy;
    }
    
    public boolean contains(final T element) {
        return this.elements.contains(element);
    }
    
    public void addElement(final T toAdd) {
        this.elements.add(toAdd);
    }
    
    @SafeVarargs
    public final void addElements(final T... toAdd) {
        this.elements.addAll((Collection<? extends T>)Arrays.asList(toAdd));
    }
    
    public boolean removeElement(final T toRemove) {
        return this.elements.remove(toRemove);
    }
    
    @SafeVarargs
    public final boolean removeElements(final T... toRemove) {
        return this.elements.removeAll(Arrays.asList(toRemove));
    }
    
    public T getByIndex(final int index) {
        return this.elements.get(index);
    }
    
    public T get(final Predicate<T> predicate) {
        for (final T element : this.elements) {
            if (predicate.test(element)) {
                return element;
            }
        }
        return null;
    }
    
    public T getAndRemove(final Predicate<T> predicate) {
        final T element = this.get(predicate);
        if (element != null) {
            this.removeElement(element);
        }
        return element;
    }
    
    public T[] getAll(final Predicate<T> predicate) {
        final List<T> array = new LinkedList<T>();
        for (final T element : this.elements) {
            if (predicate.test(element)) {
                array.add(element);
            }
        }
        return (T[])array.toArray();
    }
    
    public Optional<T> find(final Predicate<T> predicate) {
        return Optional.ofNullable((T)this.get((Predicate<T>)predicate));
    }
    
    public Optional<T> findAndRemove(final Predicate<T> predicate) {
        final Optional<T> optional = this.find(predicate);
        optional.ifPresent(this::removeElement);
        return optional;
    }
    
    public ImmutableList<T> toImmutable() {
        return (ImmutableList<T>)ImmutableList.copyOf((Collection)this.elements);
    }
    
    public Iterator<T> iterator() {
        return this.elements.iterator();
    }
    
    public int size() {
        return this.elements.size();
    }
    
    public void removeIf(final Predicate<T> predicate) {
        for (final T element : this.elements) {
            if (predicate.test(element)) {
                this.removeElement(element);
            }
        }
    }
    
    public List<T> getElements() {
        return this.elements;
    }
}

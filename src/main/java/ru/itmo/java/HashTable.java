package ru.itmo.java;

public class HashTable {


    static final double DefaultLoadFactor = 0.5;

    static final int DefaultCapacity = 1031;

    static final int[] PrimaryNumbers = { 0, 127, 1031, 2089, 5209, 7321, 9173, 20789, 38431, 49037, 89261, 1507591 };

    static final double DefaultThreshold = 0.4;

    private int innerSize = 0;

    private Entry[] Collection;

    private double threshold = DefaultThreshold;

    private double ExtendsCap = 2;

    private double LoadFactor = DefaultLoadFactor;

    HashTable() {
        this(DefaultCapacity, DefaultLoadFactor);
    }

    HashTable(int capacity) {
        this(capacity, DefaultLoadFactor);
    }

    HashTable(int capacity, double loadFactor) {
        LoadFactor = loadFactor;
        Collection = new Entry[CalculateArrayCapacity(capacity, loadFactor)];
    }

    private int CalculateArrayCapacity(int clientCapacity, double loadFactor) {
        int needArraySize = (int)((double)clientCapacity / loadFactor);
        if (needArraySize > PrimaryNumbers[PrimaryNumbers.length - 1]) {
            return needArraySize;
        }
        int yk = PrimaryNumbers.length - 1;
        while (needArraySize <= PrimaryNumbers[yk] && yk > 0) {
            --yk;
        }
        return PrimaryNumbers[yk + 1];
    }

    public void SetThreshold(double value) {
        threshold = value;
    }

    private int TransformHashToArrayIndex(int hash, Entry[] Items) {
        int result = hash % Items.length;
        if (result < 0) {
            result += Items.length;
        }
        return result;
    }

    Object put(Object key, Object value) {
        ExtendIfNeeded();
        return internalPut(key, value, Collection);
    }

    Object internalPut(Object key, Object value, Entry[] Items) {
        int hash = key.hashCode();
        int index = TransformHashToArrayIndex(hash, Items);
        Object returnResult = internalGet(key, Items);
        if (returnResult != null) {
            internalRemove(key, Items);
        }
        while (Items[index] != null && !Items[index].IsMarkedAsRemoved()) {
            ++index;
            if (index >= Items.length) {
                index = 0;
            }
        }
        if (Items[index] == null || Items[index].IsMarkedAsRemoved()) {
            ++innerSize;
        }
        Items[index] = new Entry(key, value);
        return returnResult;
    }

    Object get(Object key) {
        return internalGet(key, Collection);
    }

    Object internalGet(Object key, Entry[] Items) {
        int hash = key.hashCode();
        int index = TransformHashToArrayIndex(hash, Items);
        boolean exists = false;
        //System.out.println("get hash " + hash + " from index = " + index);
        int iterations = 0;
        while (Items[index] != null) {
            ++iterations;
            if (iterations > Items.length) {
                break;
            }
            if (Items[index].key.equals(key) && !Items[index].IsMarkedAsRemoved()) {
                exists = true;
                break;
            }
            ++index;
            if (index >= Items.length) {
                index = 0;
            }
        }
        if (exists) {
            return Items[index].value;
        }
        return null;
    }

    Object remove(Object key) {
        return internalRemove(key, Collection);
    }

    Object internalRemove(Object key, Entry[] Items) {
        int hash = key.hashCode();
        int index = TransformHashToArrayIndex(hash, Items);
        boolean exists = false;
        int iterations = 0;
        while (Items[index] != null) {
            ++iterations;
            if (iterations > Items.length) {
                break;
            }
            if (Items[index].key.equals(key)) {
                exists = !Items[index].IsMarkedAsRemoved();
                break;
            }
            ++index;
            if (index >= Items.length) {
                index = 0;
            }
        }
        if (exists) {
            Items[index].MarkAsRemoved();
            --innerSize;
            Object result = Items[index].value;
            if (Items[(index + 1) % Items.length] == null) {
                Items[index] = null;
            }
            return result;
        }
        return null;
    }

    private void ExtendIfNeeded() {
        if (size() <= (int)(threshold * Collection.length)) {
            return;
        }
        int newCapacity = (int)((double)size() * ExtendsCap);
        Entry[] newCollection = new Entry[CalculateArrayCapacity(newCapacity, LoadFactor)];
        innerSize = 0;
        for (int i = 0; i < Collection.length; ++i) {
            if (Collection[i] != null && !Collection[i].IsMarkedAsRemoved()) {
                internalPut(Collection[i].key, Collection[i].value, newCollection);
            }
        }
        Collection = newCollection;
    }

    int size() {
        return innerSize;
    }


    private class Entry {

        public final Object key;

        public Object value;

        private boolean markedAsRemoved = false;

        Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public boolean IsMarkedAsRemoved() {
            return markedAsRemoved;
        }

        public void MarkAsRemoved() {
            markedAsRemoved = true;
        }

    }

}

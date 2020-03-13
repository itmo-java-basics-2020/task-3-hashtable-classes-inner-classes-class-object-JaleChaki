package ru.itmo.java;

import java.util.ArrayList;
import java.util.Map;

public class HashTable {

    private class Entry {

        public Object Key;

        public Object Value;

        public boolean MarkedAsRemoved = false;

        Entry(Object key, Object value) {
            Key = key;
            Value = value;
        }

    }

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
        Collection = new Entry[CalculateArrayCapacity(DefaultCapacity, DefaultLoadFactor)];
    }

    HashTable(int capacity) {
        Collection = new Entry[CalculateArrayCapacity(capacity, DefaultLoadFactor)];
    }

    HashTable(int capacity, double loadFactor) {
        LoadFactor = loadFactor;
        Collection = new Entry[CalculateArrayCapacity(capacity, loadFactor)];
    }

    private int CalculateArrayCapacity(int clientCapacity, double loadFactor) {
        //return 1507591;
        int needArraySize = (int)((double)clientCapacity / loadFactor);
        if (needArraySize > PrimaryNumbers[PrimaryNumbers.length - 1]) {
            return needArraySize;
        }
        int yk = PrimaryNumbers.length - 1;
        while (needArraySize <= PrimaryNumbers[yk] && yk > 0) {
            --yk;
        }
        //System.out.println("assigned array size = " + PrimaryNumbers[yk + 1]);
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
        //while (Items[result] != null && Items[result])
    }

    Object put(Object key, Object value) {
        if (size() > (int)(threshold * Collection.length)) {
            Extend();
        }
        return internalPut(key, value, Collection);
    }

    Object internalPut(Object key, Object value, Entry[] Items) {
        int hash = key.hashCode();
        int index = TransformHashToArrayIndex(hash, Items);
        //System.out.println("add hash " + key.hashCode() + " key = " + key + " into " + index + " val = " + value);
        Object returnResult = internalGet(key, Items);
        //System.out.println("current size = " + size());
        if (returnResult != null) {
            internalRemove(key, Items);
        }
        while (Items[index] != null && !Items[index].MarkedAsRemoved) {
            /*System.out.println("current index = " + index + " key = " + Items[index].Key);
            if (Items[index].Key.equals(key)) {
                System.out.println("break");
                break;
            }*/
            ++index;
            if (index >= Items.length) {
                index = 0;
            }
        }
        if (Items[index] == null || Items[index].MarkedAsRemoved) {
            /*if (Items[index] != null && Items[index].MarkedAsRemoved) {
                System.out.println("replace into " + Items[index].Key + " " + Items[index].Value);
            }*/
            ++innerSize;
        }
        //System.out.println("target index = " + index);
        //System.out.println("added result = " + value);
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
        while (Items[index] != null) {
            if (Items[index].Key.equals(key) && !Items[index].MarkedAsRemoved) {
                exists = true;
                break;
            }
            ++index;
            if (index >= Items.length) {
                index = 0;
            }
        }
        if (exists) {
            //System.out.println("result value = " + Items[index].Value);
            return Items[index].Value;
        } else {
            //System.out.println("not found!");
            return null;
        }
    }

    Object remove(Object key) {
        return internalRemove(key, Collection);
    }

    Object internalRemove(Object key, Entry[] Items) {
        int hash = key.hashCode();
        int index = TransformHashToArrayIndex(hash, Items);
        boolean exists = false;
        //System.out.println("remove hash " + hash + " key = " + key + " from index = " + index);
        if (Items[index] == null) {
           // System.out.println("NULLLLL");
        }
        while (Items[index] != null) {
            //System.out.println("index key = " + Items[index].Key);
            if (Items[index].Key.equals(key)) {
                //System.out.println("detected key = " + key + " val = " + Items[index].Value + " deleted = " + Items[index].MarkedAsRemoved);
                exists = !Items[index].MarkedAsRemoved;
                break;
            }
            ++index;
            if (index >= Items.length) {
                index = 0;
            }
        }
        if (exists) {
            Items[index].MarkedAsRemoved = true;
            --innerSize;
            //System.out.println("removed result " + Items[index].Value);
            return Items[index].Value;
        } else {
            //System.out.println("key not found!");
            /*if (Items[index] == null) {
                return null;
            } else {
                return Items[index].Value;
            }*/
            return null;
        }
    }

    private void Extend() {
        int newCapacity = (int)((double)size() * ExtendsCap);
        Entry[] newCollection = new Entry[CalculateArrayCapacity(newCapacity, LoadFactor)];
        innerSize = 0;
        for (int i = 0; i < Collection.length; ++i) {
            if (Collection[i] != null && !Collection[i].MarkedAsRemoved) {
                internalPut(Collection[i].Key, Collection[i].Value, newCollection);
            }
        }
        Collection = newCollection;
    }

    int size() {
        return innerSize;
    }

}

package hust.cs.javacourse.search.util;


import java.util.Map;

public class Pair<K, V> implements Map.Entry<K,V> {
    K key = null;
    V value = null;

    public Pair(K key, V value){
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    @Override
    public V setValue(V value) {
        this.value = value;
        return this.value;
    }
}

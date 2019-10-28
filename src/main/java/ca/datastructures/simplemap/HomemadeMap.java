package ca.datastructures.simplemap;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class HomemadeMap<K, V> implements SimpleMap<K, V> {
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private static final int DEFAULT_CAPACITY = 16;
    private static final int GROW_COEFFICIENT = 2;

    private Node<K, V>[] table;
    private double loadFactor;
    private int numberOfElements;

    private static class Node<K, V> implements Map.Entry<K, V> {
        final K key;
        final int hash;
        V value;
        Node<K, V> next;

        Node(K key, int hash, V value, Node<K, V> next) {
            this.key = key;
            this.hash = hash;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            this.value = value;
            return value;
        }

        /**
         * return node with particular key or return last node in chain
         *
         * @param searchKey key to search
         * @return Node
         */
        Node<K, V> findByKeyOrLastInChain(K searchKey) {
            Node<K, V> node = this;
            while (! node.getKey().equals(searchKey) && node.next != null) {
                node = node.next;
            }
            return node;
        }
    }

    public HomemadeMap(int capacity, double loadFactor) {
        table = new Node[capacity];//(Node<K,V>[]) new Object[capacity];//new K[capacity];
        this.loadFactor = loadFactor;
    }

    public HomemadeMap(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    public HomemadeMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    @Override
    public V put(@NonNull K key, V value) {
        int hash = hash(key);
        int index = indexForHash(hash);
        if (table[index] == null) {
            table[index] = new Node<>(key, hash, value, null);
            numberOfElements++;
        } else {
            Node<K, V> node = table[index].findByKeyOrLastInChain(key);
            if (node.hash == hash) {
                node.setValue(value);
            } else {
                node.next = new Node<>(key, hash, value, null);
                numberOfElements++;
            }
        }
        if (size() >= threshold()) {
            grow();
        }
        return value;
    }

    @Override
    public V get(@NonNull K key) {
        int index = indexForHash(hash(key));
        Node<K, V> node = table[index];
        if (node == null) {
            return null;
        }
        node = node.findByKeyOrLastInChain(key);
        V value;
        if (node.getKey().equals(key)) {
            value = node.getValue();
        } else {
            value = null;
        }
        return value;
    }

    @Override
    public V remove(@NonNull K key) {
        int hash = hash(key);
        int index = indexForHash(hash);
        if (table[index] == null) {
            return null;
        }

        Node<K, V> parentNode = null;
        Node<K, V> node = table[index];

        while (! node.getKey().equals(key) && node.next != null) {
            parentNode = node;
            node = node.next;
        }

        if (! node.getKey().equals(key)) {
            return null;
        }

        if (parentNode == null) {
            table[index] = null;
            numberOfElements--;
            return node.getValue();
        }

        parentNode.next = node.next;
        numberOfElements--;
        return node.getValue();
    }

    @Override
    public boolean contains(@NonNull K key) {
        int hash = hash(key);
        int index = indexForHash(hash);
        if (table[index] == null) {
            return false;
        }
        Node<K, V> node = table[index].findByKeyOrLastInChain(key);
        return node.getKey() == key;
    }

    @Override
    public int size() {
        return numberOfElements;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Node<K, V> node : table) {
            while (node != null) {
                keys.add(node.getKey());
                node = node.next;
            }
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = new ArrayList<>();
        for (Node<K, V> node : table) {
            while (node != null) {
                values.add(node.getValue());
                node = node.next;
            }
        }
        return values;
    }

    private void grow() {
        Node<K, V>[] oldTable = table;
        table = new Node[GROW_COEFFICIENT * capacity()];
        numberOfElements = 0;
        for (Node<K, V> node : oldTable) {
            if (node == null) {
                continue;
            }
            put(node);
            while (node.next != null) {
                node = node.next;
                put(node);
            }
        }
    }

    private int threshold() {
        return (int) (loadFactor * capacity());
    }

    private void put(Node<K, V> node) {
        put(node.getKey(), node.getValue());
    }

    private int indexForHash(int hash) {
        return hash % capacity();
    }

    private int hash(K key) {
        return key == null ? 0 : Math.abs(key.hashCode());
    }

    private int capacity() {
        return table.length;
    }
}

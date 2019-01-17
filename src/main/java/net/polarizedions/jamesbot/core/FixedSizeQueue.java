package net.polarizedions.jamesbot.core;

import java.util.ArrayList;

public class FixedSizeQueue<K> extends ArrayList<K> {
    private int maxSize;

    public FixedSizeQueue(int size) {
        this.maxSize = size;
    }

    public boolean add(K k) {
        boolean r = super.add(k);
        if (size() > maxSize) {
            removeRange(0, size() - maxSize);
        }
        return r;
    }

    public K getYoungest() {
        return get(size() - 1);
    }

    public K getOldest() {
        return get(0);
    }
}

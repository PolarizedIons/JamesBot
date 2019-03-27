package net.polarizedions.jamesbot.utils;

import java.util.Objects;

public class Pair<One, Two> {
    One one;
    Two two;

    public Pair(One one, Two two) {
        this.one = one;
        this.two = two;
    }

    public One getOne() {
        return one;
    }

    public Two getTwo() {
        return two;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Pair<?, ?> pair = (Pair<?, ?>)o;
        return Objects.equals(one, pair.one) &&
                Objects.equals(two, pair.two);
    }

    @Override
    public int hashCode() {
        return Objects.hash(one, two);
    }
}

package net.polarizedions.jamesbot.utils;

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
}

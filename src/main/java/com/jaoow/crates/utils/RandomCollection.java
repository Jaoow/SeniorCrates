package com.jaoow.crates.utils;

import lombok.RequiredArgsConstructor;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class RandomCollection<E> {

    private final NavigableMap<Double, E> map = new TreeMap<>();

    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(ThreadLocalRandom.current());
    }

    public void add(double weight, E result) {
        if (weight <= 0) return;

        total += weight;
        map.put(total, result);
    }

    public E random() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }
}


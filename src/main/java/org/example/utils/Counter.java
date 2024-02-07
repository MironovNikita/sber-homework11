package org.example.utils;

public class Counter {
    public Double count(double a) {
        for (int i = 0; i < 2; i++) {
            a += a;
        }

        return a;
    }
}

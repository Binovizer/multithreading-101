package com.the_learning_perk.practice;

import java.util.concurrent.atomic.AtomicInteger;

public class Foo {

    private AtomicInteger counter = new AtomicInteger(1);

    public Foo() {}

    public void first(Runnable printFirst) throws InterruptedException {
        while (counter.get() != 1) {
            wait();
        }
        // printFirst.run() outputs "first". Do not change or remove this line.
        printFirst.run();
        counter.incrementAndGet();
    }

    public void second(Runnable printSecond) throws InterruptedException {
        while (counter.get() != 2) {
            wait();
        }
        // printSecond.run() outputs "second". Do not change or remove this line.
        printSecond.run();
        counter.incrementAndGet();
    }

    public void third(Runnable printThird) throws InterruptedException {
        while (counter.get() != 3) {
            wait();
        }
        // printThird.run() outputs "third". Do not change or remove this line.
        printThird.run();
        counter.incrementAndGet();
    }
}

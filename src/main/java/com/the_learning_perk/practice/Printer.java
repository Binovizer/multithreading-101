package com.the_learning_perk.practice;

public class Printer {
    private int counter = 1;
    private int threadToPrint = 1;

    public synchronized void print(int threadId) {
        while (counter <= 10) {
            while (threadToPrint != threadId) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            if (counter <= 10) {
                System.out.println("Thread " + threadId + " printed: " + counter);
                counter++;
                threadToPrint = (threadToPrint % 3) + 1;
                notifyAll();
            }
        }
    }
}

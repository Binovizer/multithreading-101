package com.the_learning_perk.practice;

/**
 * The type PrintUsingThreads
 *
 * @author nadeem Date : 10/10/24
 */
public class PrintUsingThreads {

    public static void main(String[] args) {
        Printer printer = new Printer();

        Thread t1 = new Thread(() -> printer.print(1));
        Thread t2 = new Thread(() -> printer.print(2));
        Thread t3 = new Thread(() -> printer.print(3));

        t1.start();
        t2.start();
        t3.start();
    }
}

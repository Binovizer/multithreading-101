package com.the_learning_perk.practice;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerV1 {

    private final Lock lock = new ReentrantLock(true); // Enabling fairness
    private final Condition canProduce = lock.newCondition();
    private final Condition canConsume = lock.newCondition();

    private final Queue<String> queue = new LinkedList<>();
    private final Random random = new Random();
    private final int maxQueueSize = 5; // Maximum size of the queue
    private volatile boolean shutdown = false; // Flag to indicate shutdown

    public void produce() {
        lock.lock();
        try {
            while (!shutdown) { // Keep producing until shutdown is signaled
                while (queue.size() >= maxQueueSize && !shutdown) {
                    canProduce.await(); // Wait if the queue is full
                }
                if (shutdown) {
                    System.out.println("Producer shutdown requested. Exiting...");
                    return;
                }
                String generated = "String: " + random.nextInt();
                queue.add(generated);
                System.out.println("Produced -> " + generated + " | Queue Size: " + queue.size());
                canConsume.signal(); // Signal the consumer to consume
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
        } finally {
            lock.unlock();
        }
    }

    public void consume() {
        lock.lock();
        try {
            while (!shutdown
                    || !queue
                            .isEmpty()) { // Keep consuming until shutdown is signaled and queue is
                                          // empty
                while (queue.isEmpty() && !shutdown) {
                    canConsume.await(); // Wait if the queue is empty
                }
                if (shutdown && queue.isEmpty()) {
                    System.out.println("Consumer shutdown requested. Exiting...");
                    return;
                }
                String polled = queue.poll();
                System.out.println("Consumed -> " + polled + " | Queue Size: " + queue.size());
                canProduce.signal(); // Signal the producer to produce
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
        } finally {
            lock.unlock();
        }
    }

    // Method to initiate shutdown
    public void shutdown() {
        lock.lock();
        try {
            shutdown = true; // Set the shutdown flag
            canProduce.signalAll(); // Wake up any waiting producer thread
            canConsume.signalAll(); // Wake up any waiting consumer thread
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ProducerConsumerV1 pc = new ProducerConsumerV1();

        // Create Producer Thread
        Thread producerThread =
                new Thread(
                        () -> {
                            while (true) {
                                pc.produce();
                                try {
                                    Thread.sleep(500); // Simulate production time
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        });

        // Create Consumer Thread
        Thread consumerThread =
                new Thread(
                        () -> {
                            while (true) {
                                pc.consume();
                                try {
                                    Thread.sleep(1000); // Simulate consumption time
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        });

        // Start threads
        producerThread.start();
        consumerThread.start();

        // Let the producer and consumer run for a while
        Thread.sleep(5000);

        // Initiate a graceful shutdown
        System.out.println("Initiating shutdown...");
        pc.shutdown();

        // Wait for producer and consumer threads to finish gracefully
        producerThread.join();
        consumerThread.join();

        System.out.println("Shutdown complete.");
    }
}

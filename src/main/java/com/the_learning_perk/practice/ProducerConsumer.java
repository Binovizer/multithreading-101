package com.the_learning_perk.practice;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumer {
    
    private final Lock lock = new ReentrantLock(true); // Fair lock
    private final Condition canProduce = lock.newCondition();
    private final Condition canConsume = lock.newCondition();
    private final Queue<String> queue = new LinkedList<>();
    private final Random random = new Random();
    private final int maxQueueSize = 5;
    private volatile boolean shutdown = false;

    public void produce() {
        lock.lock();
        try {
            while (!shutdown) {
                while (isQueueFull() && !shutdown) {
                    canProduce.await();
                }
                if (shutdown) {
                    logAndExit("Producer");
                    return;
                }
                String generated = "String: " + random.nextInt();
                queue.add(generated);
                logWithQueueSize("Produced", generated);
                canConsume.signal();
            }
        } catch (InterruptedException e) {
            handleInterruptedException("Producer");
        } finally {
            lock.unlock();
        }
    }

    public void consume() {
        lock.lock();
        try {
            while (!shutdown || !queue.isEmpty()) {
                while (isQueueEmpty() && !shutdown) {
                    canConsume.await();
                }
                if (shutdown && queue.isEmpty()) {
                    logAndExit("Consumer");
                    return;
                }
                String polled = queue.poll();
                logWithQueueSize("Consumed", polled);
                canProduce.signal();
            }
        } catch (InterruptedException e) {
            handleInterruptedException("Consumer");
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        lock.lock();
        try {
            shutdown = true;
            canProduce.signalAll();
            canConsume.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // Utility Methods
    private boolean isQueueFull() {
        return queue.size() >= maxQueueSize;
    }

    private boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    private void logWithQueueSize(String action, String message) {
        System.out.println(action + " -> " + message + " | Queue Size: " + queue.size());
    }

    private void logAndExit(String actor) {
        System.out.println(actor + " shutdown requested. Exiting...");
    }

    private void handleInterruptedException(String actor) {
        System.out.println(actor + " was interrupted.");
        Thread.currentThread().interrupt(); // Restore the interrupt status
    }

    public static void main(String[] args) throws InterruptedException {
        ProducerConsumer pc = new ProducerConsumer();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> {
            while (true) {
                pc.produce();
                sleep(500);  // Simulating production time
            }
        });

        executorService.submit(() -> {
            while (true) {
                pc.consume();
                sleep(1000); // Simulating consumption time
            }
        });

        // Let the producer and consumer run for a while
        Thread.sleep(5000);

        // Initiate graceful shutdown
        System.out.println("Initiating shutdown...");
        pc.shutdown();
        executorService.shutdown();

        while (!executorService.isTerminated()) {
            Thread.sleep(100); // Wait for graceful termination
        }

        System.out.println("Shutdown complete.");
    }

    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

package com.the_learning_perk.basics;

import java.time.Instant;
import java.util.concurrent.*;

/**
 * The type ParallelSum
 *
 * @author nadeem Date : 31/08/24
 */
public class ParallelSum {
    /**
     * Returns the sum for given range [from, to] both inclusive
     *
     * @param from the start of the range
     * @param to the end of the range
     * @return the sum
     */
    public long sum(long from, long to) {
        long sum = 0;
        for (long i = from; i <= to; i++) {
            sum += i;
        }
        return sum;
    }

    /**
     * Runs program in single threaded environment
     *
     * @return the total time taken to sum from 1 to Integer.MAX_VALUE
     */
    public long singleThreadSum() {
        int start = 1;
        int end = Integer.MAX_VALUE;
        Instant startTime = Instant.now();
        long sum = sum(start, end);
        Instant endTime = Instant.now();
        System.out.println("Total Sum From Single Thread : " + sum);
        return endTime.toEpochMilli() - startTime.toEpochMilli();
    }

    /**
     * Runs the program in a multithreaded environment
     *
     * @return the total time taken to sum from 1 to Integer.MAX_VALUE
     */
    public long multiThreadSum() throws InterruptedException, ExecutionException {
        long start = 1;
        long end = Integer.MAX_VALUE;
        long mid = start + (end - start) / 2;

        Instant startTime = Instant.now();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<Long> sumCallable1 = () -> sum(1, mid);
        Callable<Long> sumCallable2 = () -> sum(1 + mid, end);

        Future<Long> future1 = executorService.submit(sumCallable1);
        Future<Long> future2 = executorService.submit(sumCallable2);

        // Retrieve results from the futures
        long sum1 = future1.get();
        long sum2 = future2.get();

        long totalSum = sum1 + sum2;
        System.out.println("Total Sum From Multiple Threads: " + totalSum);

        Instant endTime = Instant.now();
        return endTime.toEpochMilli() - startTime.toEpochMilli();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ParallelSum sum = new ParallelSum();
        long singleThreadSum = sum.singleThreadSum();
        System.out.println("Total Time Taken For Single Thread : " + singleThreadSum);
        long multiThreadSum = sum.multiThreadSum();
        System.out.println("Total Time Taken For Multiple Thread : " + multiThreadSum);
    }
}

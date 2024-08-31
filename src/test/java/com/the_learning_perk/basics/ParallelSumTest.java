package com.the_learning_perk.basics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

class ParallelSumTest {

    @Test
    void testSingleThreadSum() {
        ParallelSum parallelSum = new ParallelSum();

        // Capture the time taken by single-threaded execution
        long timeTaken = parallelSum.singleThreadSum();

        // Validate the time taken to ensure it's not too quick (indicating work was done)
        assertTrue(timeTaken > 0, "Single-threaded sum should take some time.");

        // Since Integer.MAX_VALUE is too large to actually calculate in a test, consider the time
        // validation only
        // or test with a smaller range using a different method for accurate sum calculation.
    }

    @Test
    void testMultiThreadSum() throws ExecutionException, InterruptedException {
        ParallelSum parallelSum = new ParallelSum();

        // Capture the time taken by multithreaded execution
        long timeTaken = parallelSum.multiThreadSum();

        // Validate the time taken to ensure it's not too quick (indicating work was done)
        assertTrue(timeTaken > 0, "Multi-threaded sum should take some time.");

        // Since Integer.MAX_VALUE is too large to actually calculate in a test, consider the time
        // validation only
        // or test with a smaller range using a different method for accurate sum calculation.
    }

    @Test
    void testParallelExecution() throws ExecutionException, InterruptedException {
        ParallelSum parallelSum = new ParallelSum();

        // Validate that the multithreaded execution runs faster or at least the same as
        // single-threaded
        long singleThreadTime = parallelSum.singleThreadSum();
        long multiThreadTime = parallelSum.multiThreadSum();

        // The multithreaded execution should be faster or equal in time
        assertTrue(
                multiThreadTime <= singleThreadTime,
                "Multi-threaded sum should be faster or equal to single-threaded sum.");
    }

    @Test
    void testCorrectnessOfSum() throws ExecutionException, InterruptedException {
        ParallelSum parallelSum = new ParallelSum();

        // Define a smaller range for which we know the correct sum
        long from = 1;
        long to = 10;

        // Expected sum for the range 1 to 10
        long expectedSum = 55; // 1 + 2 + ... + 10

        // Test sum method directly
        assertEquals(expectedSum, parallelSum.sum(from, to), "Sum calculation should be correct.");

        // Test single-threaded execution with a known range
        long actualSingleThreadSum = parallelSum.sum(from, to);
        assertEquals(expectedSum, actualSingleThreadSum, "Single-threaded sum should be correct.");

        // Test multithreaded execution with a known range
        long mid = from + (to - from) / 2;

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<Long> sumCallable1 = () -> parallelSum.sum(from, mid);
        Callable<Long> sumCallable2 = () -> parallelSum.sum(1 + mid, to);

        Future<Long> future1 = executorService.submit(sumCallable1);
        Future<Long> future2 = executorService.submit(sumCallable2);

        long sum1 = future1.get();
        long sum2 = future2.get();

        long actualMultiThreadSum = sum1 + sum2;
        assertEquals(expectedSum, actualMultiThreadSum, "Multi-threaded sum should be correct.");
    }
}

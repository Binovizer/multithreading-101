package com.the_learning_perk.basics;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class ParallelSumDynamicTest {

    @TestFactory
    Stream<DynamicTest> dynamicSumTests() {
        ParallelSum parallelSum = new ParallelSum();

        return LongStream.of(1, 10, 100, 1000, 10000)
                .mapToObj(
                        range -> {
                            long expectedSum = calculateExpectedSum(range);
                            return dynamicTest(
                                    "Test sum for range 1 to " + range,
                                    () -> {
                                        long actualSum = parallelSum.sum(1, range);
                                        assertEquals(
                                                expectedSum,
                                                actualSum,
                                                "Sum for range 1 to "
                                                        + range
                                                        + " should be "
                                                        + expectedSum);
                                    });
                        });
    }

    private long calculateExpectedSum(long range) {
        return range * (range + 1) / 2; // Formula for sum of first N natural numbers
    }
}

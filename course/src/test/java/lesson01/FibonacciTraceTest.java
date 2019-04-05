package lesson01;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FibonacciTraceTest {
    @Test
    public void test() {
        assertEquals(FibonacciTrace.fib(6).trace.size(), 6);
    }
}
package lesson01;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FibonacciTest {
    @Test
    public void test() {
        assertEquals(Fibonacci.fib(6), 8);
    }
}
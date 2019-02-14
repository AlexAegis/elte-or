package lesson01;

import java.util.stream.IntStream;

public class Fibonacci3 {
    public static void main(String[] args) {
        System.out.println(fib(4));
    }

    private static int fib(int n) {
        return IntStream.range(0, n).collect(FibAcc::new, FibAcc::shift, (fibAcc, fin) -> {}).sum;
    }

    static class FibAcc {
        private int prev = 1;
        private int prevBef;
        int sum;

        void shift(Integer i) {
            sum += prev + prevBef;
            prevBef = prev;
            prev = i;
        }
    }
}


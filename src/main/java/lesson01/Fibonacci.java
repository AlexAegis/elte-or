package lesson01;

import java.util.stream.IntStream;

public class Fibonacci {
	public static void main(String... args) {
		System.out.println(fib(6));
	}

	public static int fib(int n) {
		return IntStream.range(0, n).collect(Fib::new, Fib::shift, (fibAcc, fin) -> {
		}).t2;
	}

	static class Fib {
		private int t1 = 1;
		private int t2;

		void shift(Integer i) {
			var sum = t1 + t2;
			t1 = t2;
			t2 = sum;
		}
	}
}

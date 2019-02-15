package lesson02;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class FibonacciTest {
	@Test
	public void test() {
		assertArrayEquals(new Read().read("input.txt").toArray(), new String[] { "first", "line", "second", "line" });
	}
}

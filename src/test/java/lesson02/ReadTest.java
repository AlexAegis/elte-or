package lesson02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import model.Coord;

class FibonacciTest {
	@Test
	public void test() {
		assertEquals(new Read().read("input.txt").orElse(null), new Coord(1, 1));
	}

}

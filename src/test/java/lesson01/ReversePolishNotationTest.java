package lesson01;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ReversePolishNotationTest {

	@Test
	public void testExample() {
		assertEquals(ReversePolishNotation.RPN.eval("1 2 * 3 +"), 5);
	}

	@Test
	public void testWikiExample() {
		assertEquals(ReversePolishNotation.RPN.eval("15 7 1 1 + - / 3 * 2 1 1 + + -"), 5);
	}

	@Test
	public void testAnotherWikiExample() {
		assertEquals(ReversePolishNotation.RPN.eval("1 2 + 4 × 5 + 3 −"), 14);
	}

}

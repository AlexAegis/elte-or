package lesson01;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lesson01.Arguments;

public class ArgumentsTest {

	@Test
	public void test() {
		assertEquals(new Arguments().task("alma", "3", "körte", "6"), new Arguments.ResultPair(4, 9));
	}
}

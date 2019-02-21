package lesson02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import model.Table;

class PrintTest {
	String state = "..........\n" + ".X........\n" + "..X.......\n" + "...X......\n" + "..........\n" + "..........\n"
			+ "..........\n" + "..........\n" + "..........\n" + "..........";

	@Test
	public void test() {
		assertEquals(new Print().read("input.txt").orElse(new Table()).toString(), state);
	}

}

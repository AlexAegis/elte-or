package lesson02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import model.Table;

class ShipsTest {
	String finalFieldState = "~~~~~.....\n" + "~--#~O....\n" + "~~~~~.....\n" + "..~#~.....\n" + "..~#~.....\n"
			+ "..~#~.....\n" + "..~#~.....\n" + "..~~~.....\n" + "..........\n" + "..........";

	@Test
	public void test() {
		assertEquals(new Ships().simulate("player.defend.txt", "player.attack.txt").orElse(new Table()).getAdmiral(0)
				.field(), finalFieldState);
	}

}

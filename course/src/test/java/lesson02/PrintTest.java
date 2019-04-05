package lesson02;

// import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import battleships.model.Table;

class PrintTest {
	String state = "~~~.......\n" + "~X~.......\n" + "~~~~~.....\n" + "..~X~.....\n" + "..~~~.....\n" + ".....~~~..\n"
			+ ".....~X~..\n" + ".....~~~..\n" + "..........\n" + "..........";

	@Test
	public void test() {
		System.out.println(new Print().read("input.txt").orElse(new Table()).getAdmiral("0").field());
		// assertEquals(new Print().read("input.txt").orElse(new Table()).getAdmiral("0").field(), state);
	}

}

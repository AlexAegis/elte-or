package musicbox.model;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;

public class SongTest {
	@Test
	void songTest() {
		var song = new Song("test1", Arrays.asList(
				"C 4 E 4 C 4 E 4 G 8 G 8 REP 6;1 C/1 4 B 4 A 4 G 4 F 8 A 8 G 4 F 4 E 4 D 4 C 8 C 8".split(" ")));
		var resultList = new ArrayList<>();
		var shouldList = Arrays.asList("C", "-", "-", "-", "E", "-", "-", "-", "C", "-", "-", "-", "E", "-", "-", "-",
				"G", "-", "-", "-", "-", "-", "-", "-", "G", "-", "-", "-", "-", "-", "-", "-", "C", "-", "-", "-", "E",
				"-", "-", "-", "C", "-", "-", "-", "E", "-", "-", "-", "G", "-", "-", "-", "-", "-", "-", "-", "G", "-",
				"-", "-", "-", "-", "-", "-", "C/1", "-", "-", "-", "B", "-", "-", "-", "A", "-", "-", "-", "G", "-",
				"-", "-", "F", "-", "-", "-", "-", "-", "-", "-", "A", "-", "-", "-", "-", "-", "-", "-", "G", "-", "-",
				"-", "F", "-", "-", "-", "E", "-", "-", "-", "D", "-", "-", "-", "C", "-", "-", "-", "-", "-", "-", "-",
				"C", "-", "-", "-", "-", "-", "-", "-", "FIN");
		song.blockingSubscribe(note -> resultList.add(note.toString(false)));
		Assert.assertArrayEquals(shouldList.toArray(), resultList.toArray());
	}


	@Test
	void songTest2() {
		var song = new Song("test2", Arrays.asList(
				"D 1 D 3 D/1 1 D/1 3 C/1 1 C/1 3 C/1 2 C/1 2 D/1 1 D/1 3 C/1 1 Bb 3 A 4 A 2 R 2 REP 15;1 Bb 4 A 2 G 2 F 1 F 3 E 2 D 2 G 2 G 2 C/1 2 Bb 2 A 4 D/1 2 R 2 C/1 1 Bb 3 A 2 G 2 G 1 A 3 G 2 F 2 A 1 G 3 F# 2 Eb 2 D 4 D 2 R 2"
						.split(" ")));
		var resultList = new ArrayList<>();
		var shouldList = Arrays.asList("D", "D", "-", "-", "D/1", "D/1", "-", "-", "C/1", "C/1", "-", "-", "C/1", "-",
				"C/1", "-", "D/1", "D/1", "-", "-", "C/1", "Bb", "-", "-", "A", "-", "-", "-", "A", "-", "R", "-", "D",
				"D", "-", "-", "D/1", "D/1", "-", "-", "C/1", "C/1", "-", "-", "C/1", "-", "C/1", "-", "D/1", "D/1",
				"-", "-", "C/1", "Bb", "-", "-", "A", "-", "-", "-", "A", "-", "R", "-", "Bb", "-", "-", "-", "A", "-",
				"G", "-", "F", "F", "-", "-", "E", "-", "D", "-", "G", "-", "G", "-", "C/1", "-", "Bb", "-", "A", "-",
				"-", "-", "D/1", "-", "R", "-", "C/1", "Bb", "-", "-", "A", "-", "G", "-", "G", "A", "-", "-", "G", "-",
				"F", "-", "A", "G", "-", "-", "F#", "-", "Eb", "-", "D", "-", "-", "-", "D", "-", "R", "-", "FIN");
		song.doOnEach(e -> System.out.println(e)).blockingSubscribe(note -> resultList.add(note.toString(false)));
		resultList.forEach(System.out::println);
		Assert.assertArrayEquals(shouldList.toArray(), resultList.toArray());
	}
}

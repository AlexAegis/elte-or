package lesson07;

import java.io.*;

public class TreeWrite {

	public static void main(String[] args) {
		var root = new Node<>(5);
		root.insert(3);
		root.insert(4);
		root.insert(6);

		System.out.println(root);

		try (ObjectOutputStream out =
				new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("test.out")))) {
			out.writeObject(root);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("test.out"))) {
			var ino = in.readObject();
			System.out.println(ino.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package lesson07;

import java.io.*;

public class Sum {
	public static void main(String[] args) {
		try(ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("test.out")))) {
			for (int i = 0; i < 100; i++) {
				if (Math.random() <= 0.5) {
					out.writeObject(Double.valueOf((Math.round(Math.random() * 10))).intValue());
				} else {
					out.writeObject(Math.random() * 10);
				}
			}
			out.flush();
		} catch(Exception e ) {
			e.printStackTrace();
		}

		try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("test.out"))) {
			Double sum = 0.D;
			boolean eof = false;
			while(!eof) {
				try {
					var ino = in.readObject();
					System.out.println(ino);
					if(ino instanceof Double) {
						sum += (Double)ino;
					} else if(ino instanceof Integer) {
						sum += (Integer) ino;
					}
				} catch(EOFException eofE) {
					eof = true;
				}
			}
			System.out.println("Sum: " + sum);
		} catch(Exception e ) {
			e.printStackTrace();
		}
	}
}

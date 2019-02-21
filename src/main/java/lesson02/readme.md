# Lesson 2

Java file handling practice using Scanner

## 1) [Read text file with the scanner](./Read.java) - [Test](../../../test/java/lesson02/ReadTest.java)

Read [coordinate](../model/Coord.java) from the input file and print out the one **closest** to the center [`(0, 0)`](../model/Coord.java#L9)

## 2) [Read and Print](./Print.java)

Read [coordinates](../model/Coord.java) and print out a **10 by 10** plane, put `X` where is a coordinate and `.` where's not.

## 3) [Ships](./Ships.java)

Read [ships](../model/Ship.java) from a file, then attacks from another. Simulate a one-sided torpedo game. Print out the [table](../model/Table.java) after each attack and the statuses of the ships (Total health and health remaining).
Mark empty spots on the **10 by 10** plane with a `.`, Ships with a `-` or `|` depending on where it's facing. (`X` if it's a single piece) and damaged parts of the ships with a `#`. Also, you can display missed shots with an `O`.

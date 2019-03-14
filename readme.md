# Osztott Rendszerek

[![Build Status](https://travis-ci.com/AlexAegis/elte-or.svg?branch=master)](https://travis-ci.com/AlexAegis/elte-or) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/d66bb3f374ce459dad9985499eb32cc1)](https://www.codacy.com/app/AlexAegis/elte-or?utm_source=github.com&utm_medium=referral&utm_content=AlexAegis/elte-or&utm_campaign=Badge_Grade) [![Maintainability](https://api.codeclimate.com/v1/badges/c6f44ecb5a3920b431dc/maintainability)](https://codeclimate.com/github/AlexAegis/elte-or/maintainability) [![Test Coverage](https://api.codeclimate.com/v1/badges/c6f44ecb5a3920b431dc/test_coverage)](https://codeclimate.com/github/AlexAegis/elte-or/test_coverage) [![librariesio: dependencies](https://img.shields.io/librariesio/github/AlexAegis/elte-or.svg?style=popout)](https://libraries.io/github/AlexAegis/elte-or) [![snyk: vulnerabilities](https://img.shields.io/snyk/vulnerabilities/github/AlexAegis/elte-or.svg?style=popout)](https://app.snyk.io/org/alexaegis/project/2c007095-748f-4281-9ce0-655598d97fc2) [![code style: prettier](https://img.shields.io/badge/code_style-prettier-ff69b4.svg)](https://github.com/prettier/prettier)

ELTE-IK 2018-19/2

# BattleShips!

_Now With Exclusive Battle Royale Mode!~_

```bash
java -jar battleships-1.0.0.jar
```

## If you want to run the client on Windows use `javaw` instead of `java`! (Or use [WSL](https://docs.microsoft.com/en-us/windows/wsl/install-win10))

(Lanterna can't use CMD or PS to display)

```cmd
javaw -jar battleships-1.0.0.jar client
```

For everything else like reading the help prompts or running the server using `java` is fine

```cmd
java -jar battleships-1.0.0.jar client -h
```

[Download!](https://github.com/AlexAegis/elte-or/releases)

![BattleShips screenshot](https://i.imgur.com/r3QgjAc.png)

<sub>The screenshot was made with the [cool-retro-term](https://github.com/Swordfish90/cool-retro-term)</sub>

---

## [Lesson 1 - resuscitation](./src/main/java/lesson01/)

| #   | Task                                                                           | [Test](./src/test/java/lesson01/)                               |
| --- | ------------------------------------------------------------------------------ | --------------------------------------------------------------- |
| 1   | [Hello World](./src/main/java/lesson01/HelloWorld.java)                        | [Test](https://www.youtube.com/watch?v=dQw4w9WgXcQ)             |
| 2   | [Arguments](./src/main/java/lesson01/Arguments.java)                           | [Test](./src/test/java/lesson01/ArgumentsTest.java)             |
| 3   | [Fibonacci](./src/main/java/lesson01/Fibonacci.java)                           | [Test](./src/test/java/lesson01/FibonacciTest.java)             |
| 4   | [Fibonacci Trace](./src/main/java/lesson01/FibonacciTrace.java)                | [Test](./src/test/java/lesson01/FibonacciTraceTest.java)        |
| 5   | [Reverse Polish Notation](./src/main/java/lesson01/ReversePolishNotation.java) | [Test](./src/test/java/lesson01/ReversePolishNotationTest.java) |

## [Lesson 2 - file handling, scanner, battleships](./src/main/java/lesson02/)

| #   | Task                                         | [Test](./src/test/java/lesson02/)               |
| --- | -------------------------------------------- | ----------------------------------------------- |
| 1   | [Read](./src/main/java/lesson02/Read.java)   | [Test](./src/test/java/lesson02/ReadTest.java)  |
| 2   | [Print](./src/main/java/lesson02/Print.java) | [Test](./src/test/java/lesson02/PrintTest.java) |
| 3   | [Ships](./src/main/java/lesson02/Ships.java) | [Test](./src/test/java/lesson02/ShipsTest.java) |

| Shared model                                                  |
| ------------------------------------------------------------- |
| [Table](./src/main/java/battleships/Table.java)               |
| [Ship](./src/main/java/battleships/Ship.java)                 |
| [Coord](./src/main/java/battleships/model/Coord.java)         |
| [Direction](./src/main/java/battleships/model/Direction.java) |

## [Lesson 3 - Basic server-client connection](./src/main/java/lesson03/)

| #   | Task                                                                                                                                                |
| --- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | [Continuous file write from standard input](./src/main/java/lesson03/ContinuousFileWrite.java)                                                      |
| 2   | [BasicBattleShips from standard input](./src/main/java/lesson03/BattleShipsFromStdIn.java)                                                          |
| 3   | [Basic Server](./src/main/java/lesson03/Server.java)/[Client example](./src/main/java/lesson03/Client.java)                                         |
| 4   | [Basic BattleShips Server](./src/main/java/lesson03/BasicBattleShipsServer.java) [and Client](./src/main/java/lesson03/BasicBattleShipsClient.java) |

| Shared model                                                  |
| ------------------------------------------------------------- |
| [Admiral](./src/main/java/battleships/Admiral.java)           |
| [Table](./src/main/java/battleships/Table.java)               |
| [Ship](./src/main/java/battleships/Ship.java)                 |
| [Coord](./src/main/java/battleships/model/Coord.java)         |
| [Shot](./src/main/java/battleships/model/Shot.java)           |
| [Direction](./src/main/java/battleships/model/Direction.java) |

## [Lesson 4 - BattleShips over network](./src/main/java/lesson04/)

---

## Build

```bash
./gradlew clean build
```

## Test

```bash
./gradlew clean test
```

## Run as application

```bash
./gradlew run
```

If for some reason VS Code throws an error upon running a debug session that it can't build the workspace, but the project can be built by hand, try cleaning it with Ctrl+Shift+P `>Java: Clean the Java language server workspace`

## Throubleshoot

If the application looks blurry and you're using different scaling settings on windows other than 100%, try setting the Right-Click->Properties->Compatibility Tab's "Override the high DPI scaling behavior" to "System" on the java.exe/javaw.exe you're using. (Or use a WSL Shell)

## Technologies

### [Java 11](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)

> **Oracle** JDK

### [RXJava](https://github.com/ReactiveX/RxJava)

> **Reactive** components for concurrent solutions

### [Lanterna](https://github.com/mabe02/lanterna)

> **Text GUI** library

### [Picocli](https://picocli.info/)

> **Command line** interface

### [Gradle](https://gradle.org/)

> **Build** tool

### [JUnit 5](https://junit.org/junit5/)

> **Unit testing** framework

### [JaCoCo](https://www.eclemma.org/jacoco/)

> **Code coverage** tool

## Recommendations

### [Visual Studio Code](https://code.visualstudio.com/)

> **IDE** for everything. [Settings](./.vscode/)

### [IntelliJ IDEA](https://www.jetbrains.com/idea/)

> **IDE** for java.

### [Fira Code](https://github.com/tonsky/FiraCode)

> **Font** with ligatures

## Services

### [Travis](https://travis-ci.com/)

> **Continuous Integration** solution

### [Codacy](https://codacy.com/)

> **Code Quality** monitoring

### [Code Climate](https://codeclimate.com/)

> **Maintainability and Coverage** reports

### [Snyk](https://snyk.io/)

> **Vulnerability** detection

### [Libraries.io](https://libraries.io/)

> **Dependency** watcher

### [Shields.io](https://shields.io/#/)

> **Badges** to look cool

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

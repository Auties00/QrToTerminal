# Qr code to terminal

### What is qr to terminal

Qr to terminal is a simple library to print qr codes generate using zxing to the terminal.
I wrote this library because there are equivalents for other programming languages, but I couldn't find any good one for Java.
Requires at least Java 11.
Inspired by [this thread](https://superuser.com/a/1420015).

### How to install

#### Maven

```xml
<dependency>
    <groupId>com.github.auties00</groupId>
    <artifactId>qr-terminal</artifactId>
    <version>2.1</version>
</dependency>
```

#### Gradle

1. Groovy DSL
   ```groovy
   implementation 'com.github.auties00:qr-terminal:2.1'
   ```

2. Kotlin DSL
   ```kotlin
   implementation("com.github.auties00:qr-terminal:2.1")
   ```

### How to use

1. Full size ANSI blocks
   ```java
   QrTerminal.print(matrix, false);
   ```

   ![](https://user-images.githubusercontent.com/28218457/180877477-c8360128-62b7-48fe-96c9-bdc17e8fb2b3.png)

2. UTF-8 small blocks (recommended)
   ```java
   QrTerminal.print(matrix, true);
   ```

   ![](https://user-images.githubusercontent.com/28218457/180877136-bd54c647-1507-4743-8111-74d9e41c4e9f.png)

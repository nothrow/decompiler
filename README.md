# Decompiler
Decompiler for Java Spring. Is intended to run _after_ all the Spring magic takes place, so you can see what the code actually looks like.

## Usage

### Build

Built by maven. Includes all needed inside (I hope).

```sh
    git clone git@github.com:nothrow/decompiler.git
    cd decompiler
    mvn package
```

### Use

This package is `javaagent`, and accepts, as parameter, prefix for all classes to be decompiled.

```sh
    java -javaagent:$(PATH_TO_DECOMPILER_TARGET)/decompiler-1.0-SNAPSHOT.jar=cz.nothrow -jar my-spring-application.jar
```

It creates (and fills) two directories:

- `dumped_bytecode/` for all the `*.class` files
- `dumped_java/` for `*.java` files - generated through [JetBrains' Fernflower decompiler](https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler/engine). 

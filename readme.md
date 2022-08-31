# JSQL, a simple java mysql framework 1.4.1
This is just a very simple framework, that enables you to easily save and load your class instances from and to your mysql server
Please note that I jsut started working on this project hence there isn't much here yet

By default JSQL can only save the following types, [save custom types](https://github.com/Bethibande/JSQL/tree/master/examples/src/com/bethibande/jsql/examples/adapters)
 - short
 - byte
 - int
 - long
 - float
 - double
 - boolean
 - char
 - String
 - UUID
 - and any enum types

## Examples
A simple example can be found [here](https://github.com/Bethibande/JSQL/tree/master/examples/src/com/bethibande/jsql/examples) <br>
An example showing how to save custom types is [here](https://github.com/Bethibande/JSQL/tree/master/examples/src/com/bethibande/jsql/examples/adapters) <br>
A caching example can be found here [here](https://github.com/Bethibande/JSQL/tree/master/examples/src/com/bethibande/jsql/examples/cache) <br>
An example for using basic 'or' and 'and' queries is [here](https://github.com/Bethibande/JSQL/tree/master/examples/src/com/bethibande/jsql/examples/query/Main.java) <br>
An example for saving and loading abstract classes is [here](https://github.com/Bethibande/JSQL/tree/master/examples/src/com/bethibande/jsql/examples/abstractclasses/Main.java)

## Import
Download latest build [here](https://github.com/Bethibande/maven-repos/blob/main/de/bethibande/jsql/1.4.1/jsql-1.4.1.jar)
### Gradle
```gradle
repositories {
    mavenCentral()

    maven { url "https://github.com/Bethibande/maven-repos/raw/main" }
}

dependencies {
    implementation 'de.bethibande:jsql:1.4.1'
}
```
### Maven
```xml
<repository>
    <id>de.bethibande</id>
    <url>https://github.com/Bethibande/maven-repos/raw/main</url>
</repository>

<dependency>
    <groupId>de.bethibande</groupId>
    <artifactId>jsql</artifactId>
    <version>1.4.1</version>
</dependency>
```

## Dependencies
 - JConnector 8.0.28

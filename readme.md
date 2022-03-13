# JSQL, a simple java mysql framework
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
 - and enums

## Examples
A simple example can be found [here](https://github.com/Bethibande/JSQL/tree/master/examples/src/com/bethibande/jsql/examples)
An example showing how to save custom types is [here](https://github.com/Bethibande/JSQL/tree/master/examples/src/com/bethibande/jsql/examples/adapters)

## Import
Download jar [here](https://github.com/Bethibande/JSQL/blob/repository/de/bethibande/jsql/1.1.0/jsql-1.1.0.jar)
### Gradle
```gradle
repositories {
    mavenCentral()

    maven { url "https://github.com/Bethibande/JSQL/raw/repository" }
}

dependencies {
    implementation 'de.bethibande:jsql:1.1.0'
}
```
### Maven
```xml
<repository>
    <id>de.bethibande</id>
    <url>https://github.com/Bethibande/JSQL/raw/repository</url>
</repository>

<dependency>
    <groupId>de.bethibande</groupId>
    <artifactId>jsql</artifactId>
    <version>1.1.0</version>
</dependency>
```

## Dependencies
 - JConnector 8.0.28
 - Gson (although only used in the examples classes -> Main.java)

## TODO
 - Add a caching option to SQLTable.java

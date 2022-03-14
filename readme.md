# JSQL, a simple java mysql framework 1.2.0
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
A caching example can be found here [here](https://github.com/Bethibande/JSQL/tree/master/examples/src/com/bethibande/jsql/examples/cache)

## Import
Download jar [here](https://github.com/Bethibande/JSQL/blob/repository/de/bethibande/jsql/1.2.0/jsql-1.2.0.jar)
### Gradle
```gradle
repositories {
    mavenCentral()

    maven { url "https://github.com/Bethibande/JSQL/raw/repository" }
}

dependencies {
    implementation 'de.bethibande:jsql:1.2.0'
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
    <version>1.2.0</version>
</dependency>
```

## Dependencies
 - JConnector 8.0.28

## TODO
 - Add default values

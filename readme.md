# JSQL, a simple java mysql framework
This is just a very simple framework, that enables you to easily save and load your class instances from and to your mysql server
Please note that I jsut started working on this project hence there isn't much here yet

At the moment JSQL can only save the following java types
 - short
 - byte
 - int
 - long
 - float
 - double
 - boolean
 - char
 - String
 - and enums

## Examples
A simple example can be found [here](https://github.com/Bethibande/JSQL/tree/master/src/com/bethibande/jsql/examples)

## Dependencies
 - JConnector 8.0.28
 - Gson (although only used in the examples classes -> Main.java)

## TODO
 - Add a caching option to SQLTable.java
 - Add type adapters inorder to save lists, maps or other custom types and in order to get rid of all the if statements in the SQLTable and SQLFields classes

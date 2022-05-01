package com.bethibande.jsql.examples.abstractclasses;

public class AbstractClassImpl extends AbstractClass<String> {

    public AbstractClassImpl(String key, String value) {
        super(ClassType.STRING, key, value);
    }

    @Override
    public String getKeyAndValue() {
        return super.getKey() + ":" + super.getValue();
    }
}

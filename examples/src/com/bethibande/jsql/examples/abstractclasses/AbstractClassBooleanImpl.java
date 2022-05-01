package com.bethibande.jsql.examples.abstractclasses;

public class AbstractClassBooleanImpl extends AbstractClass<Boolean> {

    public AbstractClassBooleanImpl(String key, Boolean value) {
        super(ClassType.BOOLEAN, key, value);
    }

    @Override
    public String getKeyAndValue() {
        return super.getKey() + ":" + super.getValue();
    }
}

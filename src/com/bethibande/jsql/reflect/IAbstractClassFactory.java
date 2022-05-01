package com.bethibande.jsql.reflect;

import com.bethibande.jsql.SQLDataObject;
import com.bethibande.jsql.SQLObject;

public interface IAbstractClassFactory<T extends SQLObject> {

    /**
     * Turn a SQLDataObject representing an abstract class into an object instance of a class extending/implementing the abstract class
     * @param obj the sql data object holding all the field values of the object
     * @return an instance of the same type as the SQLDataObject object
     */
    T toClassInstance(SQLDataObject<T> obj);

}

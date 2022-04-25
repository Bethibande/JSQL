package com.bethibande.jsql;

import com.bethibande.jsql.types.SQLType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: default values
// TODO: update table schema when SQLObject fields are changed?
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLField {

    SQLType type() default SQLType.AUTO_DETECT;
    long size() default 0;
    boolean isKey() default false;

}

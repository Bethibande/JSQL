package com.bethibande.jsql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLField {

    public SQLType type() default SQLType.AUTO_DETECT;
    public int size() default 0;
    public boolean isKey() default false;

}

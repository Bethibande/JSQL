package com.bethibande.jsql.fields;

import com.bethibande.jsql.JSQL;
import com.bethibande.jsql.SQLField;
import com.bethibande.jsql.SQLObject;
import com.bethibande.jsql.types.FinalType;
import com.bethibande.jsql.types.SQLType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;

public class SQLFields {

    public class SimpleField {

        private transient Field field;
        private transient SQLField sqlField;

        private SQLType type;
        private long typeSize;

        private boolean isKey;

        public SimpleField(Field field, SQLField sqlField, SQLType type, long typeSize, boolean isKey) {
            this.field = field;
            this.sqlField = sqlField;
            this.type = type;
            this.typeSize = typeSize;
            this.isKey = isKey;
        }

        public Field getField() {
            return field;
        }

        public SQLField getSqlField() {
            return sqlField;
        }

        public SQLType getType() {
            return type;
        }

        public long getTypeSize() {
            return typeSize;
        }

        public boolean isKey() {
            return isKey;
        }
    }

    private final HashMap<String, SimpleField> fields = new HashMap<>();

    private String keyValue = null;

    public void generate(Class<? extends SQLObject> objClass, JSQL owner) {
        Field[] fields = objClass.getDeclaredFields();
        LinkedList<SQLTypeAdapter> adapters = owner.getTypeAdapters();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);

            if(Modifier.isStatic(field.getModifiers())) continue;

            if(field.isAnnotationPresent(SQLField.class)) {
                SQLField sqlField = field.getAnnotation(SQLField.class);
                SQLType type = sqlField.type();
                long size = sqlField.size();


                if(sqlField.isKey()) this.keyValue = field.getName();

                boolean isEnum = false;
                if(field.getType().isEnum()) {
                    type = SQLType.VARCHAR;
                    size = SQLType.VARCHAR.getDefaultSize();
                    isEnum = true;
                }

                FinalType finalType = null;
                for (int i1 = 0; i1 < adapters.size(); i1++) {
                    SQLTypeAdapter a = adapters.get(i1);
                    FinalType temp = a.translateType(field.getType(), type, size);
                    if(temp != null) finalType = temp;
                }

                if(finalType != null) {
                    type = finalType.getSqlType();
                    size = finalType.getSize();
                }

                // in case the type is still AUTO_DETECT, which usually shouldn't be the case
                if(type.equals(SQLType.AUTO_DETECT)) {
                    type = SQLType.VARCHAR;
                    size = SQLType.VARCHAR.getMaxSize();
                }

                this.fields.put(field.getName(), new SimpleField(field, sqlField, type, size, sqlField.isKey()));
            }
        }
    }

    public HashMap<String, SimpleField> getFields() {
        return fields;
    }

    public String getKeyValue() {
        return keyValue;
    }
}

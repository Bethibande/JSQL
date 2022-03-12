package com.bethibande.jsql.fields;

import com.bethibande.jsql.SQLField;
import com.bethibande.jsql.SQLObject;
import com.bethibande.jsql.SQLType;

import java.lang.reflect.Field;
import java.util.HashMap;

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

    public void generate(Class<? extends SQLObject> objClass) {
        Field[] fields = objClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
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

                if(type.equals(SQLType.AUTO_DETECT) && !isEnum) {
                    Class<?> t = field.getType();
                    // TODO: replace if statements with type adapters
                    if(t == String.class) {
                        type = SQLType.VARCHAR;
                        size = SQLType.VARCHAR.getDefaultSize();
                    }
                    if(t == byte.class) {
                        type = SQLType.TINYINT;
                        size = SQLType.TINYINT.getDefaultSize();
                    }
                    if(t == short.class) {
                        type = SQLType.SMALLINT;
                        size = SQLType.SMALLINT.getDefaultSize();
                    }
                    if(t == int.class) {
                        type = SQLType.VARCHAR;
                        size = SQLType.VARCHAR.getDefaultSize();
                    }
                    if(t == float.class) {
                        type = SQLType.VARCHAR;
                        size = SQLType.VARCHAR.getDefaultSize();
                    }
                    if(t == double.class) {
                        type = SQLType.VARCHAR;
                        size = SQLType.VARCHAR.getDefaultSize();
                    }
                    if(t == long.class) {
                        type = SQLType.VARCHAR;
                        size = SQLType.VARCHAR.getDefaultSize();
                    }
                    if(t == char.class) {
                        type = SQLType.VARCHAR;
                        size = SQLType.VARCHAR.getDefaultSize();
                    }
                    if(t == boolean.class) {
                        type = SQLType.VARCHAR;
                        size = SQLType.VARCHAR.getDefaultSize();
                    }
                }

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

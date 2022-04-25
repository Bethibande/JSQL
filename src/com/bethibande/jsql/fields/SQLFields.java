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

    public static class SimpleField {

        private final transient Field field;
        private final transient SQLField sqlField;

        private final SQLType type;
        private final long typeSize;

        private final boolean isKey;

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

        for(Field field : fields) {
            field.setAccessible(true);

            if (Modifier.isStatic(field.getModifiers())) continue;

            if (field.isAnnotationPresent(SQLField.class)) {
                SQLField sqlField = field.getAnnotation(SQLField.class);
                SQLType type = sqlField.type();
                long size = sqlField.size();


                if (sqlField.isKey()) this.keyValue = field.getName();

                if (field.getType().isEnum()) {
                    type = SQLType.VARCHAR;
                    size = SQLType.VARCHAR.getDefaultSize();
                }

                FinalType finalType = null;
                for (SQLTypeAdapter a : adapters) {
                    FinalType temp = a.translateType(new TypeTranslationParameters(field.getDeclaringClass(), field, field.getType(), type, size));
                    if (temp != null) finalType = temp;
                }

                if (finalType != null) {
                    type = finalType.getSqlType();
                    size = finalType.getSize();
                }

                // in case the type is still AUTO_DETECT, which usually shouldn't be the case
                if (type.equals(SQLType.AUTO_DETECT)) {
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

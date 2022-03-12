package com.bethibande.jsql;

import com.bethibande.jsql.commands.SQLCommands;
import com.bethibande.jsql.fields.SQLFields;
import com.bethibande.jsql.reflect.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class SQLTable<T extends SQLObject> {

    private JSQL owner;
    private String table;

    private Class<T> clazz;

    private SQLFields fields;
    private SQLCommands commands;

    private boolean init;

    public SQLTable(JSQL owner, Class<T> clazz, String table) {
        this.owner = owner;
        this.table = table;
        this.clazz = clazz;
    }

    public void init() {
        if(this.init) {
            if(this.owner.isDebug()) System.out.println("[JSQL ERROR] called SQLTable.init() twice or more!");
            return;
        }

        generateFields();
        generateCommands();

        this.owner.update(this.commands.getCreateTableCommand());

        this.init = true;
    }

    public boolean hasItem(Object key) {
        try {
            ResultSet rs = this.owner.query(this.commands.getHasItemQuery(), key);
            return rs.next();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Object[] getValues(T item) {
        Object[] values = new Object[this.fields.getFields().size()];

        Iterator<SQLFields.SimpleField> it = this.fields.getFields().values().iterator();
        for (int i = 0; i < this.fields.getFields().keySet().size(); i++) {
            SQLFields.SimpleField f = it.next();
            try {
                Object obj = f.getField().get(item);

                if(obj.getClass().isEnum()) obj = obj.toString();

                values[i] = obj;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return values;
    }

    public void addItem(T item) {
        item.setOwner(this);
        this.owner.update(this.getCommands().getInsertCommand(), getValues(item));
    }

    public void saveItem(T item) {
        item.setOwner(this);
        this.owner.update(this.getCommands().getUpdateCommand(), getValues(item));
    }

    public T getItem(Object key) {
        if(key.getClass().isEnum()) key = key.toString();
        ResultSet rs = this.owner.query(this.commands.getQueryCommand(), key);
        try {
            if(rs.next()) {
                T instance = ClassUtils.createClassInstance(this.clazz);

                Iterator<String> it = this.fields.getFields().keySet().iterator();
                while(it.hasNext()) {
                    String name = it.next();
                    SQLFields.SimpleField f = this.fields.getFields().get(name);
                    Class<?> type = f.getField().getType();
                    Object obj = null;

                    if(type == byte.class) {
                        obj = rs.getByte(name);
                    }
                    if(type == short.class) {
                        obj = rs.getShort(name);
                    }
                    if(type == int.class) {
                        obj = rs.getInt(name);
                    }
                    if(type == long.class) {
                        obj = rs.getLong(name);
                    }
                    if(type == float.class) {
                        obj = rs.getFloat(name);
                    }
                    if(type == double.class) {
                        obj = rs.getDouble(name);
                    }
                    if(type == char.class) {
                        obj = rs.getString(name).charAt(0);
                    }
                    if(type == boolean.class) {
                        obj = rs.getBoolean(name);
                    }
                    if(type == String.class) {
                        obj = rs.getString(name);
                    }

                    if(type.isEnum()) {
                        try {
                            String str = rs.getString(name);

                            Method m = type.getDeclaredMethod("valueOf", String.class);
                            obj = m.invoke(null, str);
                        } catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        f.getField().set(instance, obj);
                    } catch(IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                instance.setOwner(this);
                return instance;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSQL getOwner() {
        return owner;
    }

    public String getTable() {
        return table;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public SQLFields getFields() {
        return fields;
    }

    public SQLCommands getCommands() {
        return commands;
    }

    public boolean isInit() {
        return init;
    }

    private void generateFields() {
        this.fields = new SQLFields();
        this.fields.generate(this.clazz);

        if(this.fields.getKeyValue() == null) {
            System.err.println("[JSQL ERROR] Initialized SQLTable without key value!");
        }
    }

    private void generateCommands() {
        this.commands = new SQLCommands(this.fields, this.table);
        this.commands.generate();
    }

}

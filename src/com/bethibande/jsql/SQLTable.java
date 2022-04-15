package com.bethibande.jsql;

import com.bethibande.jsql.cache.CacheConfig;
import com.bethibande.jsql.cache.SimpleCache;
import com.bethibande.jsql.commands.SQLCommands;
import com.bethibande.jsql.fields.SQLFields;
import com.bethibande.jsql.fields.SQLTypeAdapter;
import com.bethibande.jsql.reflect.ClassUtils;
import com.bethibande.jsql.types.FinalType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Class representing a mysql table
 * @param <T> the type of the Objects being stored in this mysql table
 */
public class SQLTable<T extends SQLObject> {

    private JSQL owner;
    private String table;

    private Class<T> clazz;

    private SQLFields fields;
    private SQLCommands commands;

    private boolean useCache = false;
    private SimpleCache<Object, T> cache = null;

    private boolean init;

    public SQLTable(JSQL owner, Class<T> clazz, String table) {
        this.owner = owner;
        this.table = table;
        this.clazz = clazz;
    }

    /**
     * executes useCache(new CacheConfig<>());
     */
    public void useCache() {
        this.useCache(new CacheConfig<>());
    }

    /**
     * Initialize cache
     * @param config config for cache instance
     */
    public void useCache(CacheConfig<T> config) {
        this.useCache = true;
        this.cache = new SimpleCache<>(config.getSize(), config.getTimeout());
        this.cache.setRemoveItemHook((item) -> {
            this.saveItem(item);
            config.getRemoveItemHook().accept(item);
        });
    }

    /**
     * @return true if useCache(config) was called and cache instance not equals null
     */
    public boolean isUseCache() {
        return this.useCache && this.cache != null;
    }

    /**
     * @return returns cache instance
     */
    public SimpleCache<?, T> getCache() {
        return cache;
    }

    /**
     * Do not call this method, called by JSQL class when the table is registered,
     * can't be called twice any ways, that's what the isInit() and the init field are for
     */
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

    /**
     * @return true if your mysql table is empty / row count <= 0
     */
    public boolean isEmpty() {
        return countRows() <= 0;
    }

    /**
     * @return true if your mysql table is not empty / row count > 0
     */
    public boolean isNotEmpty() {
        return countRows() > 0;
    }

    /**
     * The same as SQLTable.countRows()
     * @return the number of items in your mysql table
     */
    public int size() {
        return countRows();
    }

    /**
     * Executes count query, counts all rows/items stored in your mysql table
     * @return the number of rows your table has, if an error occurred -1
     */
    public int countRows() {
        try {
            ResultSet rs = this.owner.query(this.commands.getCountCommand());
            if(rs.next()) {
                return rs.getInt("COUNT(*)");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Same as SQLTable.countColumn()
     * @see #countColumn(String)
     * @param fieldName the field name whose entries are to be counted
     * @return the counted entries, no duplicates or null values counted
     */
    public int countUniqueFieldEntries(String fieldName) {
        return countColumn(fieldName);
    }

    /**
     * Count unique column entries, without counting duplicate or null entries.<br>
     * For example calling SQLTable.coutColumn("Value"); using the table below, would return 2
     * <table>
     *   <col width="25%"/>
     *   <col width="75%"/>
     *   <thead>
     *     <tr><th>Key</th><th>Value</th></tr>
     *   <thead>
     *   <tbody>
     *      <tr><td>a</td><td>f</td></tr>
     *      <tr><td>b</td><td>f</td></tr>
     *      <tr><td>c</td><td>h</td></tr>
     *   </tbody>
     * </table>
     * @param columnName the column/field name to be counted
     * @return the number of entries counted, no duplicates or null values included
     */
    public int countColumn(String columnName) {
        try {
            ResultSet rs = this.owner.query(this.commands.generateCountColumnCommand(columnName));
            if(rs.next()) {
                return rs.getInt("count");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get the value of the specified field within the object instance
     * @param obj the object instance containing the field/value you want to get
     * @param field the java field name of the value you want to get
     * @return the value, if there is none or an exception is thrown, returns null
     */
    public Object getFieldValue(T obj, String field) {
        SQLFields.SimpleField sf = this.fields.getFields().get(field);

        try {
            return sf.getField().get(obj);
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks whether the objects within this table have a field with the specified field name
     * @param field the java field name
     * @return true if there is a field with the specified name
     */
    public boolean hasField(String field) {
        return this.fields.getFields().containsKey(field);
    }

    /**
     * Save/update a single field of an object instead of saving/updating the entire object
     * @param obj the object instance with the value you want to save
     * @param field the java field name of the field you want to save
     */
    public void saveField(T obj, String field) {
        Object key = obj.getKey();
        Object save = javaValueToSQL(field, getFieldValue(obj, field));

        this.owner.update(this.commands.generateFieldUpdateCommand(field), save, key);
    }

    /**
     * This method will clear the cache if used, and save all the cached items in your mysql table
     */
    public void dumpCache() {
        if(this.useCache) {
            this.cache.getAll().forEach(this::saveItem);
            this.cache.clear();
        }
    }

    /**
     * Check if the mysql table already contains a key
     * @param key the value of the key field
     * @return true if mysql table contains key value
     */
    public boolean hasItem(Object key) {
        if(this.useCache && this.cache.hasKey(key)) return true;
        try {
            if(key.getClass().isEnum()) key = key.toString();
            ResultSet rs = this.owner.query(this.commands.getHasItemQuery(), key);
            return rs.next();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete an object/item from your mysql table
     * @param key the key of the item to remove
     */
    public void deleteItem(Object key) {
        this.owner.update(this.commands.getDeleteCommand(), key);
        if(this.useCache) this.cache.remove(key);
    }

    private Object[] getValues(T item) {
        Object[] values = new Object[this.fields.getFields().size()];
        List<SQLTypeAdapter> adapters = this.owner.getTypeAdapters();

        Iterator<SQLFields.SimpleField> it = this.fields.getFields().values().iterator();
        for (int i = 0; i < this.fields.getFields().keySet().size(); i++) {
            SQLFields.SimpleField f = it.next();
            try {
                Object obj = f.getField().get(item);

                for (int i1 = 0; i1 < adapters.size(); i1++) {
                    SQLTypeAdapter a = adapters.get(i1);
                    FinalType ft = FinalType.of(f.getType(), f.getTypeSize());
                    Object temp = a.toSQL(obj, ft);
                    if(temp != null) obj = temp;
                }

                if(obj.getClass().isEnum()) obj = obj.toString();

                values[i] = obj;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return values;
    }

    /**
     * Does the same as SQLTable.addItem
     * @param item the item to add to your mysql table
     */
    public void add(T item) {
        this.putItem(item);
    }

    /**
     * Does the same as SQLTable.addItem(T)
     * @param item the item to add to your mysql table
     */
    public void putItem(T item) {
        item.setOwner(this);
        if(this.useCache) this.cache.put(item.getKey(), item);
        this.owner.update(this.getCommands().getInsertCommand(), getValues(item));
    }

    /**
     * Add an object to your mysql table
     * @param item the item to add to your mysql table
     */
    public void addItem(T item) {
        item.setOwner(this);
        if(this.useCache) this.cache.put(item.getKey(), item);
        this.owner.update(this.getCommands().getInsertCommand(), getValues(item));
    }

    /**
     * Update an object / save it to your mysql table
     * @param item the item to save to your mysql table
     */
    public void saveItem(T item) {
        item.setOwner(this);
        //if(this.useCache) this.cache.put(item.getKey(), item);
        this.owner.update(this.getCommands().getUpdateCommand(), getValues(item));
    }

    /**
     * This will execute a simple 'or' query and return a list of keys
     * like "select `key` from `table` where `field1`='value1' or `field2`='value2';"
     * @param fields the sql fields to query, for example 'username' and 'password'
     * @param values the values to check, for example 'max' and 'password'
     * @return a list of keys, retrieve items via SQLTable.get(key);
     */
    public List<Object> queryKeysOr(String[] fields, Object... values) {
        List<SQLTypeAdapter> adapters = this.owner.getTypeAdapters();
        Object[] objs = new Object[fields.length];
        for(int i = 0; i < fields.length; i++) {
            String field = fields[i];
            SQLFields.SimpleField f = this.fields.getFields().get(field);
            Object obj = values[i];

            for (int i1 = 0; i1 < adapters.size(); i1++) {
                SQLTypeAdapter a = adapters.get(i1);
                FinalType ft = FinalType.of(f.getType(), f.getTypeSize());
                Object temp = a.toSQL(obj, ft);
                if (temp != null) obj = temp;
            }

            if (obj.getClass().isEnum()) obj = obj.toString();
            objs[i] = obj;
        }

        ResultSet rs = this.owner.query(this.commands.generateCustomOrQuery(fields), objs);
        List<Object> keys = new ArrayList<>();
        try {
            while(rs.next()){
                SQLFields.SimpleField f = this.fields.getFields().get(this.getFields().getKeyValue());
                Class<?> type = f.getField().getType();
                Object obj = null;

                for (int i = 0; i < adapters.size(); i++) {
                    SQLTypeAdapter a = adapters.get(i);
                    Object temp = a.fromSQL(type, this.getFields().getKeyValue(), rs);
                    if(temp != null) obj = temp;
                }

                if(type.isEnum()) {
                    try {
                        String str = rs.getString(this.getFields().getKeyValue());

                        Method m = type.getDeclaredMethod("valueOf", String.class);
                        obj = m.invoke(null, str);
                    } catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }

                if(obj != null) keys.add(obj);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return keys;
    }

    /**
     * This will execute a simple 'and' query and return a list of keys
     * like "select `key` from `table` where `field1`='value1' and `field2`='value2';"
     * @param fields the sql fields to query, for example 'username' and 'password'
     * @param values the values to check, for example 'max' and 'password'
     * @return a list of keys, retrieve items via SQLTable.get(key);
     */
    public List<Object> queryKeysAnd(String[] fields, Object... values) {
        List<SQLTypeAdapter> adapters = this.owner.getTypeAdapters();
        Object[] objs = new Object[fields.length];
        for(int i = 0; i < fields.length; i++) {
            String field = fields[i];
            SQLFields.SimpleField f = this.fields.getFields().get(field);
            Object obj = values[i];

            for (int i1 = 0; i1 < adapters.size(); i1++) {
                SQLTypeAdapter a = adapters.get(i1);
                FinalType ft = FinalType.of(f.getType(), f.getTypeSize());
                Object temp = a.toSQL(obj, ft);
                if (temp != null) obj = temp;
            }

            if (obj.getClass().isEnum()) obj = obj.toString();
            objs[i] = obj;
        }

        ResultSet rs = this.owner.query(this.commands.generateCustomAndQuery(fields), objs);
        List<Object> keys = new ArrayList<>();
        try {
            while(rs.next()){
                SQLFields.SimpleField f = this.fields.getFields().get(this.getFields().getKeyValue());
                Class<?> type = f.getField().getType();
                Object obj = null;

                for (int i = 0; i < adapters.size(); i++) {
                    SQLTypeAdapter a = adapters.get(i);
                    Object temp = a.fromSQL(type, this.getFields().getKeyValue(), rs);
                    if(temp != null) obj = temp;
                }

                if(type.isEnum()) {
                    try {
                        String str = rs.getString(this.getFields().getKeyValue());

                        Method m = type.getDeclaredMethod("valueOf", String.class);
                        obj = m.invoke(null, str);
                    } catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }

                if(obj != null) keys.add(obj);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return keys;
    }

    /**
     * Get java objects from a mysql ResultSet, ResultSet.next() must be called before invoking method
     * @param field name of the java field within the class/type of this SQLTable instance
     * @param rs the mysql ResultSet you want to load an object from
     * @return the java Object, may be null
     */
    public Object sqlValueToJava(String field, ResultSet rs) {
        List<SQLTypeAdapter> adapters = this.owner.getTypeAdapters();
        SQLFields.SimpleField f = this.fields.getFields().get(this.getFields().getKeyValue());
        Class<?> type = f.getField().getType();
        Object obj = null;

        try {
            for (SQLTypeAdapter a : adapters) {
                Object temp = a.fromSQL(type, this.getFields().getKeyValue(), rs);
                if (temp != null) obj = temp;
            }

            if (type.isEnum()) {
                try {
                    String str = rs.getString(this.getFields().getKeyValue());

                    Method m = type.getDeclaredMethod("valueOf", String.class);
                    obj = m.invoke(null, str);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * Turn a java object/field into the final object that will be saved in your mysql table,
     * for example Person(name, age, job) -> String(name)
     * @param field name of the java field within the class/type of the table instance
     * @param value the java value, type must be the same as the type of the field
     * @return the type which will be saved in your mysql table, string, int, boolean [...], may be null
     */
    public Object javaValueToSQL(String field, Object value) {
        List<SQLTypeAdapter> adapters = this.owner.getTypeAdapters();
        SQLFields.SimpleField f = this.fields.getFields().get(field);
        Object obj = value;

        for (int i1 = 0; i1 < adapters.size(); i1++) {
            SQLTypeAdapter a = adapters.get(i1);
            FinalType ft = FinalType.of(f.getType(), f.getTypeSize());
            Object temp = a.toSQL(obj, ft);
            if(temp != null) obj = temp;
        }

        if(obj.getClass().isEnum()) obj = obj.toString();

        return obj;
    }

    /**
     * This executes a simple sql query and returns a list of keys
     * query: "select `key` from `table` where `field`='value';"
     * @param field the sql field to query, for example 'pet'
     * @param value the value to check, for example 'CAT'
     * @return a list of keys, retrieve items via SQLTable.get(key);
     */
    public List<Object> query(String field, Object value) {
        List<SQLTypeAdapter> adapters = this.owner.getTypeAdapters();
        SQLFields.SimpleField f = this.fields.getFields().get(field);
        Object obj = javaValueToSQL(field, value);

        ResultSet rs = this.owner.query(this.commands.generateSingleFieldQuery(field), obj);
        List<Object> keys = new ArrayList<>();
        try {
            while(rs.next()){
                f = this.fields.getFields().get(this.getFields().getKeyValue());
                Class<?> type = f.getField().getType();
                obj = null;

                for (int i = 0; i < adapters.size(); i++) {
                    SQLTypeAdapter a = adapters.get(i);
                    Object temp = a.fromSQL(type, this.getFields().getKeyValue(), rs);
                    if(temp != null) obj = temp;
                }

                if(type.isEnum()) {
                    try {
                        String str = rs.getString(this.getFields().getKeyValue());

                        Method m = type.getDeclaredMethod("valueOf", String.class);
                        obj = m.invoke(null, str);
                    } catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }

                if(obj != null) keys.add(obj);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return keys;
    }

    /**
     * Does the same as SQLTable.getItem();
     */
    public T get(Object key) {
        return this.getItem(key);
    }

    /**
     * Load an item from your mysql table
     * @param key the key marked as isKey using the @SQLField annotation
     * @return returns the item
     */
    public T getItem(Object key) {
        if(key.getClass().isEnum()) key = key.toString();
        if(this.useCache && this.cache.hasKey(key)) return this.cache.get(key);
        ResultSet rs = this.owner.query(this.commands.getQueryCommand(), key);
        try {
            if(rs.next()) {
                T instance = ClassUtils.createClassInstance(this.clazz);
                List<SQLTypeAdapter> adapters = this.owner.getTypeAdapters();

                Iterator<String> it = this.fields.getFields().keySet().iterator();
                while(it.hasNext()) {
                    String name = it.next();
                    SQLFields.SimpleField f = this.fields.getFields().get(name);
                    Class<?> type = f.getField().getType();
                    Object obj = null;

                    for (int i = 0; i < adapters.size(); i++) {
                        SQLTypeAdapter a = adapters.get(i);
                        Object temp = a.fromSQL(type, name, rs);
                        if(temp != null) obj = temp;
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
                if(this.useCache) this.cache.put(instance.getKey(), instance);
                return instance;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the jsql instance this table belongs to
     * @return the jsql instance
     */
    public JSQL getOwner() {
        return owner;
    }

    /**
     * Get the name of the mysql table if SQLTable instance manages
     * @return the mysql table name
     */
    public String getTable() {
        return table;
    }

    /**
     * Get the type/class this SQLTable instance stores
     * @return class type stored in this table
     */
    public Class<T> getType() {
        return clazz;
    }

    /**
     * Get the mysql fields of the class this SQLTable instance is storing (all the fields annotated with @SQLField)
     * @return an object containing all the field names and types
     */
    public SQLFields getFields() {
        return fields;
    }

    /**
     * Get the mysql commands used by this SQLTable instance
     * @return the mysql commands
     */
    public SQLCommands getCommands() {
        return commands;
    }

    /**
     * @returns Nothing useful
     */
    public boolean isInit() {
        return init;
    }

    private void generateFields() {
        this.fields = new SQLFields();
        this.fields.generate(this.clazz, this.owner);

        if(this.fields.getKeyValue() == null) {
            System.err.println("[JSQL ERROR] Initialized SQLTable without key value!");
        }
    }

    private void generateCommands() {
        this.commands = new SQLCommands(this.fields, this.table);
        this.commands.generate();
    }

}

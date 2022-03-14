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
import java.util.Iterator;
import java.util.List;

/**
 * A Class that represents a mysql table
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
     * Does the same as SQLTable.addItem(T)
     * @param item the item to add to the mysql table
     */
    public void putItem(T item) {
        item.setOwner(this);
        System.out.println(item.getKey());
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

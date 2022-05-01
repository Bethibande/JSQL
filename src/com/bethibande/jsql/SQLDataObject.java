package com.bethibande.jsql;

import java.util.HashMap;
import java.util.UUID;

public class SQLDataObject<T extends SQLObject> {

    private final HashMap<String, SQLFieldInstance<?>> fields;
    private final Class<T> clazz;
    private final SQLTable<T> owner;

    public SQLDataObject(HashMap<String, SQLFieldInstance<?>> fields, SQLTable<T> owner) {
        this.fields = fields;
        this.clazz = owner.getType();
        this.owner = owner;
    }

    /**
     * Get the SQLTable this Object was loaded from
     * @return the sql table instance
     */
    public SQLTable<T> getOwner() {
        return owner;
    }

    /**
     * Get the class the current object instance represents
     * @return the java class type
     */
    public Class<T> getClassType() {
        return clazz;
    }

    /**
     * Get the value of a field, the field names are the same as in the class the current object represents
     * @param fieldName the java field name
     * @throws NullPointerException if this class has no field with the specified fieldName
     * @return the value of the specified field, may be null
     */
    public Object getFieldValue(String fieldName) {
        return fields.get(fieldName).getValue();
    }

    /**
     * @see #getFieldValue(String)
     */
    public <E extends Enum<E>> E getEnum(Class<E> type, String fieldName) {
        Object obj = getFieldValue(fieldName);
        return (E) getFieldValue(fieldName);
    }

    /**
     * @see #getFieldValue(String)
     */
    public String getString(String fieldName) {
        Object obj = getFieldValue(fieldName);
        if(obj instanceof String) return (String) obj;

        System.out.println(obj);
        throw new ClassCastException("Cannot cast " + obj.getClass() + " to " + String.class);
    }

    /**
     * @see #getFieldValue(String)
     */
    public UUID getUUID(String fieldName) {
        Object obj = getFieldValue(fieldName);
        if(obj instanceof UUID) return (UUID) obj;

        throw new ClassCastException("Cannot cast " + obj.getClass() + " to " + UUID.class);
    }

    /**
     * @see #getFieldValue(String)
     */
    public byte getByte(String fieldName) {
        Object obj = getFieldValue(fieldName);
        if(obj instanceof Byte) return (byte) obj;

        throw new ClassCastException("Cannot cast " + obj.getClass() + " to " + byte.class);
    }

    /**
     * @see #getFieldValue(String)
     */
    public short getShort(String fieldName) {
        Object obj = getFieldValue(fieldName);
        if(obj instanceof Short) return (short) obj;

        throw new ClassCastException("Cannot cast " + obj.getClass() + " to " + short.class);
    }

    /**
     * @see #getFieldValue(String)
     */
    public Integer getInt(String fieldName) {
        Object obj = getFieldValue(fieldName);
        if(obj instanceof Integer) return (int) obj;

        throw new ClassCastException("Cannot cast " + obj.getClass() + " to " + int.class);
    }

    /**
     * @see #getFieldValue(String)
     */
    public Long getLong(String fieldName) {
        Object obj = getFieldValue(fieldName);
        if(obj instanceof Long) return (long) obj;

        throw new ClassCastException("Cannot cast " + obj.getClass() + " to " + Long.class);
    }

    /**
     * @see #getFieldValue(String)
     */
    public Boolean getBoolean(String fieldName) {
        Object obj = getFieldValue(fieldName);
        if(obj instanceof Boolean) return (boolean) obj;

        throw new ClassCastException("Cannot cast " + obj.getClass() + " to " + boolean.class);
    }

    /**
     * @see #getFieldValue(String)
     */
    public Float getFloat(String fieldName) {
        Object obj = getFieldValue(fieldName);
        if(obj instanceof Float) return (float) obj;

        throw new ClassCastException("Cannot cast " + obj.getClass() + " to " + float.class);
    }

    /**
     * @see #getFieldValue(String)
     */
    public Double getDouble(String fieldName) {
        Object obj = getFieldValue(fieldName);
        if(obj instanceof Double) return (double) obj;

        throw new ClassCastException("Cannot cast " + obj.getClass() + " to " + double.class);
    }

    /**
     * Check if there is a field with a certain name
     * @param fieldName the field name to check
     * @return true if there is a java field with the specified name in the class the current object represents
     */
    public boolean hasField(String fieldName) {
        return fields.containsKey(fieldName);
    }

    public HashMap<String, SQLFieldInstance<?>> getFields() {
        return fields;
    }
}

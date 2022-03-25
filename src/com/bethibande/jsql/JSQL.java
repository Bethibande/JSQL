package com.bethibande.jsql;

import com.bethibande.jsql.cache.CacheUpdateThread;
import com.bethibande.jsql.fields.SQLTypeAdapter;
import com.bethibande.jsql.fields.adapters.*;

import java.sql.*;
import java.util.*;

/**
 * A Class which represents a mysql connection to a certain database,
 * database may be changed whenever you fell like it, but keep in mind, this class
 * will not automatically switch between databases, if it's tables are in different databases
 *
 * !! important, the keys of SQLObjects may only have types compatible with mysql like String, int, boolean [...]
 */
public class JSQL {

    private boolean debug = false;

    private String hostAddress;
    private int hostPort = 3306;

    private String username;
    private String password;

    private String database;

    private Connection con;

    private HashMap<Class<? extends SQLObject>, SQLTable<? extends SQLObject>> tables = new HashMap<>();

    private LinkedList<SQLTypeAdapter> adapters = new LinkedList<>();

    private CacheUpdateThread updateThread;


    public void startCacheUpdateThread() {
        this.updateThread = new CacheUpdateThread(this);
        this.updateThread.start();
    }

    private void registerDefaultAdapters() {
        adapters.add(new BooleanAdapter());
        adapters.add(new ByteAdapter());
        adapters.add(new CharAdapter());
        adapters.add(new DoubleAdapter());
        adapters.add(new FloatAdapter());
        adapters.add(new IntAdapter());
        adapters.add(new LongAdapter());
        adapters.add(new ShortAdapter());
        adapters.add(new StringAdapter());
        adapters.add(new UUIDTypeAdapter());
    }

    /**
     * Call to update cache, this will remove all objects exceeding the timeout value
     */
    public void updateCache() {
        for(SQLTable<?> table : tables.values()) {
            if(table.isUseCache()) {
                table.getCache().update();
            }
        }
    }

    /**
     * @return all currently registered SQLTables
     */
    public Collection<SQLTable<? extends SQLObject>> getTables() {
        return this.tables.values();
    }

    /**
     * Get the table of a certain class
     * @param clazz the class stored in the table you want to get
     * @return an SQLTable instance storing the specified class
     */
    public <T extends SQLObject> SQLTable<T> getTable(Class<T> clazz) {
        return (SQLTable<T>) this.tables.get(clazz);
    }

    /**
     * @return all the currently registered type adapters
     */
    public LinkedList<SQLTypeAdapter> getTypeAdapters() {
        return this.adapters;
    }

    /**
     * Register a new type adapter, these adapters will translate java types/values to mysql types/values and
     * the other way around
     * @param adapter the adapter you want to register
     * @return the current object instance
     */
    public JSQL registerTypeAdapter(SQLTypeAdapter adapter) {
        this.adapters.add(adapter);
        return this;
    }

    /**
     * Unregister a type adapter if you don't need it anymore
     * @param adapter the adapter you want to remove
     * @return the current object instance
     */
    public JSQL unregisterTypeAdapter(SQLTypeAdapter adapter) {
        this.adapters.remove(adapter);
        return this;
    }

    /**
     * Register and initialize a new sql object/table,
     * mysql table will be created if it doesn't exist.
     * Please keep in mind, this will only create tables not update them if you changed/added new fields to you classes
     * @param clazz the sql object to be stored in the mysql table
     * @param table the name of your mysql table (will be created if it doesn't exist)
     * @return the created SQLTable object
     */
    public <T extends SQLObject> SQLTable<T> registerTable(Class<T> clazz, String table) {
        SQLTable<T> sqlTable = new SQLTable<>(this, clazz, table);
        sqlTable.init();

        this.tables.put(clazz, sqlTable);

        return sqlTable;
    }

    /**
     * Test method, only prints some simple debug messages
     */
    public void debug() {
        this.debug = true;
    }

    /**
     * @return true if JSQL.debug() has been called
     */
    public boolean isDebug() {
        return this.debug;
    }

    /**
     * @return the mysql connection object
     */
    public Connection getConnection() {
        return this.con;
    }

    /**
     * Set this value using JSQL.database(...);
     * @return the current database
     */
    public String getDatabase() {
        return this.database;
    }

    /**
     * Set this value using JSQL.host(..., ...);
     * @return the host address
     */
    public String getHostAddress() {
        return this.hostAddress;
    }

    /**
     * Set this value using JSQL.host(..., ...);
     * @return the host port
     */
    public int getHostPort() {
        return this.hostPort;
    }

    /**
     * Set the host address and port of the mysql server
     * @param hostAddress the ipv4 address of your mysql server
     * @param hostPort the port of your mysql server
     * @return the current object instance
     */
    public JSQL host(String hostAddress, int hostPort) {
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
        return this;
    }

    /**
     * Set the host address of the mysql server, default port if not set is 3306
     * @param hostAddress the ipv4 address of your mysql server
     * @return the current object instance
     */
    public JSQL host(String hostAddress) {
        this.hostAddress = hostAddress;
        return this;
    }

    /**
     * Set the username and password used for logging into the mysql server when calling JSQL.connect();
     * @param username username of your mysql user
     * @param password password of your mysql user
     * @return the current object instance
     */
    public JSQL user(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    /**
     * Select the active database, will connect to this database when set bore calling JSQL.connect();
     * and will execute 'use `$database`;' when isConnected() equals true
     * @param database the name of the database
     * @return the current object instance
     */
    public JSQL database(String database) {
        this.database = database;
        if(isConnected()) {
            update("USE `" + database + "`;");
        }
        return this;
    }

    public void updateBatch(String... command) {
        try {
            Statement st = this.con.createStatement();
            for (int i = 0; i < command.length; i++) {
                st.addBatch(command[i]);
            }
            st.executeBatch();
            st.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String command) {
        try {
            Statement st = this.con.createStatement();
            st.execute(command);
            st.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String command, Object... objects) {
        try {
            PreparedStatement st = this.con.prepareStatement(command);
            for (int i = 0; i < objects.length; i++) {
                st.setObject(i+1, objects[i]);
            }
            st.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet query(String command) {
        try {
            PreparedStatement st = this.con.prepareStatement(command);
            return st.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet query(String command, Object... objects) {
        try {
            PreparedStatement st = this.con.prepareStatement(command);
            for (int i = 0; i < objects.length; i++) {
                st.setObject(i+1, objects[i]);
            }

            return st.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Close the connection if isConnected() equals true
     */
    public void disconnect() {
        if(!isConnected()) return;
        if(this.updateThread != null) this.updateThread.stopThread();

        try {
            this.con.close();
            if(debug) System.out.println("MySQL Disconnected");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return true if connection object not equals null and connection isn't closed
     */
    public boolean isConnected() {
        try {
            return this.con != null && !this.con.isClosed();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Connect to the mysql server, will connect to the datbase if database has been set using JSQL.database(...);
     * @return true if connected successfully, false if not
     */
    public boolean connect() {
        if(this.username == null || this.password == null || this.hostAddress == null) {
            System.err.println("[JSQL Error] Couldn't connect to mysql server, username/password or hostAddress not set!");
        }

        if(this.adapters.isEmpty()) this.registerDefaultAdapters();

        Properties connectionProps = new Properties();
        connectionProps.put("user", this.username);
        connectionProps.put("password", this.password);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(
                    "jdbc:" + "mysql" + "://" +
                            this.hostAddress +
                            ":" + this.hostPort +
                            (this.database != null ? "/" + this.database: ""),
                    connectionProps);

            if(debug) System.out.println(isConnected() ? "[JSQL] Connected!": "[JSQL] Couldn't connect to mysql server!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return isConnected();
    }

}

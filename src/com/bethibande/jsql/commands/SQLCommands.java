package com.bethibande.jsql.commands;

import com.bethibande.jsql.fields.SQLFields;

public class SQLCommands {

    private String createTableCommand;
    private String updateCommand;
    private String insertCommand;
    private String deleteCommand;
    private String queryCommand;
    private String hasItemQuery;
    private String countCommand;

    private final SQLFields fields;
    private final String table;

    private String keyField;

    public SQLCommands(SQLFields fields, String table) {
        this.fields = fields;
        this.table = table;

        for(String s : this.fields.getFields().keySet()) {
            if(this.fields.getFields().get(s).isKey()) this.keyField = s;
        }
    }

    public void generate() {
        if(this.fields == null || this.table == null) return;

        generateCountCommand();
        createTableCommand();
        updateCommand();
        insertCommand();
        deleteCommand();
        queryCommand();
        hasItemQuery();
    }

    private void generateCountCommand() {
        this.countCommand = "select COUNT(*) from `" + this.table + "`;";
    }

    public String generateCountColumnCommand(String col) {
        return "SELECT COUNT(distinct(`" + col + "`)) as `count` from `" + this.table + "`;";
    }

    public String generateFieldUpdateCommand(String field) {
        return "UPDATE `" + this.table + "` SET `" + field + "`=? WHERE `" + this.keyField + "`=?;";
    }

    public String generateCustomOrQuery(String... fields) {
        StringBuilder sb = new StringBuilder("select `" + this.keyField + "` from `" + this.table + "` where");
        for(String f : fields) {
            sb.append(" `" + f + "`=? or");
        }
        sb.delete(sb.length()-3, sb.length());
        sb.append(";");

        return sb.toString();
    }

    public String generateCustomAndQuery(String... fields) {
        StringBuilder sb = new StringBuilder("select `" + this.keyField + "` from `" + this.table + "` where");
        for(String f : fields) {
            sb.append(" `" + f + "`=? and");
        }
        sb.delete(sb.length()-4, sb.length());
        sb.append(";");

        return sb.toString();
    }

    public String generateSingleFieldQuery(String field) {
        return "select `" + this.keyField + "` from `" + this.table + "` where `" + field + "`=?;";
    }

    private void createTableCommand() {
        StringBuilder sb = new StringBuilder("create table if not exists `" + this.table + "`(");

        this.fields.getFields().forEach((key, value) -> {
            sb.append("`" + key + "` ");
            sb.append(value.getType().getType() + (value.getTypeSize() != -1 ? "(" + value.getTypeSize() + ")": ""));
            if(value.isKey()) {
                sb.append(" PRIMARY KEY");
            }
            sb.append(", ");
        });

        sb.delete(sb.length()-2, sb.length());
        sb.append(");");

        this.createTableCommand = sb.toString();
    }

    public void updateCommand() {
        StringBuilder sb = new StringBuilder("REPLACE into `" + this.table + "` values (");
        for (int i = 0; i < this.fields.getFields().values().size(); i++) {
            sb.append("?, ");
        }
        sb.delete(sb.length()-2, sb.length());
        sb.append(");");

        this.updateCommand = sb.toString();
    }

    public void insertCommand() {
        StringBuilder sb = new StringBuilder("REPLACE into `" + this.table + "` values (");
        for (int i = 0; i < this.fields.getFields().values().size(); i++) {
            sb.append("?, ");
        }
        sb.delete(sb.length()-2, sb.length());
        sb.append(");");

        this.insertCommand = sb.toString();
    }

    public void deleteCommand() {
        this.deleteCommand = "delete from `" + table + "` where `" + this.fields.getKeyValue() + "`=?;";
    }

    public void queryCommand() {
        this.queryCommand = "select * from `" + table + "` where `" + this.fields.getKeyValue() + "`=?;";
    }

    public void hasItemQuery() {
        this.hasItemQuery = "select `" + this.fields.getKeyValue() + "` from `" + table + "` where `" + this.fields.getKeyValue() + "`=?;";
    }

    public String getCountCommand() {
        return countCommand;
    }

    public String getCreateTableCommand() {
        return createTableCommand;
    }

    public String getUpdateCommand() {
        return updateCommand;
    }

    public String getInsertCommand() {
        return insertCommand;
    }

    public String getDeleteCommand() {
        return deleteCommand;
    }

    public SQLFields getFields() {
        return fields;
    }

    public String getTable() {
        return table;
    }

    public String getQueryCommand() {
        return queryCommand;
    }

    public String getHasItemQuery() {
        return hasItemQuery;
    }
}

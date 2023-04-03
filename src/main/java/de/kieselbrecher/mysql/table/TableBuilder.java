package de.kieselbrecher.mysql.table;

import de.kieselbrecher.mysql.database.DatabaseManager;
import de.kieselbrecher.mysql.database.DatabaseOperation;

import java.util.ArrayList;
import java.util.List;

public class TableBuilder {

    private final DatabaseManager databaseManager;
    private final String name;
    private final List<TableColumn> tableColumns;

    public TableBuilder(DatabaseManager databaseManager, String name) {
        this.databaseManager = databaseManager;
        this.name = name;
        this.tableColumns = new ArrayList<>();
    }

    public TableBuilder addString(String name) {
        tableColumns.add(new TableColumn(name, TableDataType.STRING));
        return this;
    }

    public TableBuilder addInt(String name) {
        tableColumns.add(new TableColumn(name, TableDataType.INT));
        return this;
    }

    public TableBuilder addLong(String name) {
        tableColumns.add(new TableColumn(name, TableDataType.LONG));
        return this;
    }

    public TableBuilder addFloat(String name) {
        tableColumns.add(new TableColumn(name, TableDataType.FLOAT));
        return this;
    }

    public TableBuilder addDouble(String name) {
        tableColumns.add(new TableColumn(name, TableDataType.DOUBLE));
        return this;
    }

    public TableBuilder addBoolean(String name) {
        tableColumns.add(new TableColumn(name, TableDataType.BOOLEAN));
        return this;
    }

    public void create() {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ? (");
        for(int i = 0; i < tableColumns.size(); i++) {
            query.append(tableColumns.get(i).getName()).append(" ").append(tableColumns.get(i).getDataType().getMysqlType());
            if(i < tableColumns.size() - 1)
                query.append(", ");
        }
        query.append("), ");
        for(int i = 0; i < tableColumns.size(); i++) {
            query.append("ADD COLUMN IF NOT EXISTS ").append(tableColumns.get(i).getName()).append(" ").append(tableColumns.get(i).getDataType().getMysqlType());
            if(i < tableColumns.size() - 1)
                query.append(", ");
        }
        databaseManager.executeUpdate(query.toString(), name);
    }
}

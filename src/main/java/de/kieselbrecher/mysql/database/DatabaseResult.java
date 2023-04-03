package de.kieselbrecher.mysql.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public record DatabaseResult(ResultSet result) {

    public boolean next() {
        try {
            return result.next();
        } catch (SQLException exception) {
            throw new RuntimeException("Error while navigating through the ResultSet.", exception);
        }
    }

    public int getColumnCount() {
        try {
            return result.getMetaData().getColumnCount();
        } catch (SQLException exception) {
            throw new RuntimeException("Error while retrieving column count from ResultSet metadata.", exception);
        }
    }

    public String getColumnName(int index) {
        try {
            return result.getMetaData().getColumnName(index);
        } catch (SQLException exception) {
            throw new RuntimeException("Error while retrieving column name from ResultSet metadata.", exception);
        }
    }

    public String getColumnType(int index) {
        try {
            return result.getMetaData().getColumnTypeName(index);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getString(String name) {
        try {
            return result.getString(name);
        } catch (SQLException exception) {
            throw new RuntimeException("Error while retrieving column type from ResultSet metadata.", exception);
        }
    }

    public int getInt(String name) {
        try {
            return result.getInt(name);
        } catch (SQLException exception) {
            throw new RuntimeException("An error occurred while trying to retrieve an integer value for column '" + name + "'", exception);
        }
    }

    public long getLong(String name) {
        try {
            return result.getLong(name);
        } catch (SQLException exception) {
            throw new RuntimeException("An error occurred while trying to retrieve an long value for column '" + name + "'", exception);
        }
    }

    public float getFloat(String name) {
        try {
            return result.getFloat(name);
        } catch (SQLException exception) {
            throw new RuntimeException("An error occurred while trying to retrieve an float value for column '" + name + "'", exception);
        }
    }

    public double getDouble(String name) {
        try {
            return result.getDouble(name);
        } catch (SQLException exception) {
            throw new RuntimeException("An error occurred while trying to retrieve an double value for column '" + name + "'", exception);
        }
    }

    public boolean getBoolean(String name) {
        try {
            return result.getBoolean(name);
        } catch (SQLException exception) {
            throw new RuntimeException("An error occurred while trying to retrieve an boolean value for column '" + name + "'", exception);
        }
    }

    public void close() {
        try {
            result.close();
        } catch (SQLException exception) {
            throw new RuntimeException("Error occurred while trying to close the ResultSet.", exception);
        }
    }
}

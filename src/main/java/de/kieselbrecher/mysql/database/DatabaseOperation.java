package de.kieselbrecher.mysql.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseOperation {

    void executeOperation(Connection connection) throws SQLException;
}

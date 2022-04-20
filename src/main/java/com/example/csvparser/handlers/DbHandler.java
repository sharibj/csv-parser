package com.example.csvparser.handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.logging.Logger;

import lombok.SneakyThrows;

import static com.example.csvparser.constants.QueryConstants.CREATE_TABLE;
import static com.example.csvparser.constants.QueryConstants.DELETE_ALL;
import static com.example.csvparser.constants.QueryConstants.DROP_TABLE;
import static com.example.csvparser.constants.QueryConstants.INSERT_ROW;
import static com.example.csvparser.constants.QueryConstants.SELECT_COUNT;

public class DbHandler implements AutoCloseable {
    private static final Logger logger = Logger.getLogger(DbHandler.class.getName());
    String url;
    Connection conn;
    Statement statement;
    PreparedStatement preparedStatement;

    public DbHandler(String url) {
        this.url = url;
        this.init();
    }

    @SneakyThrows
    public void init() {
        logger.info("Initialising database");
        conn = DriverManager.getConnection(url);
        statement = conn.createStatement();
        createFreshTable(statement);
        preparedStatement = conn.prepareStatement(INSERT_ROW);
    }

    private void createFreshTable(Statement statement) throws SQLException {
        logger.info("Creating table");
        statement.execute(DROP_TABLE);
        statement.execute(CREATE_TABLE);
    }

    @SneakyThrows
    public void deleteAll() {
        statement.executeUpdate(DELETE_ALL);
    }

    public synchronized void insert(Set<String> messages) {
        messages.forEach(msg -> {
            try {
                preparedStatement.setString(1, msg);
                preparedStatement.executeUpdate();
            } catch (SQLException ignore) {
                //ignore
            }
        });
    }

    @SneakyThrows
    public Long getCount() {
        ResultSet rs = statement.executeQuery(SELECT_COUNT);
        rs.next();
        return rs.getLong(1);
    }

    @Override
    public void close() throws Exception {
        statement.close();
        preparedStatement.close();
        conn.close();
    }
}

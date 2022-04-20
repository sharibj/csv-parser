package com.example.csvparser.handlers;

import com.example.csvparser.constants.QueryConstants;
import com.example.csvparser.model.PageVisitModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import lombok.SneakyThrows;

import static com.example.csvparser.constants.QueryConstants.CREATE_TABLE;
import static com.example.csvparser.constants.QueryConstants.DELETE_ALL;
import static com.example.csvparser.constants.QueryConstants.DROP_TABLE;
import static com.example.csvparser.constants.QueryConstants.SELECT_COUNT;

public class DbHandler implements AutoCloseable {
    private static final Logger logger = Logger.getLogger(DbHandler.class.getName());
    static final Semaphore semaphore = new Semaphore(1);
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
        preparedStatement = conn.prepareStatement(QueryConstants.INSERT_ROW);
    }

    private void createFreshTable(Statement statement) throws SQLException {
        logger.info("Creating table");
        statement.execute(DROP_TABLE);
        statement.execute(CREATE_TABLE);
    }

    @SneakyThrows
    public int deleteAll() {
        return statement.executeUpdate(DELETE_ALL);
    }

    public int insert(PageVisitModel model) {
        int rowsInserted = -1;
        if (!model.isValid()) {
            return rowsInserted;
        }
        try {
            synchronized (semaphore) {
                setModelToPreparedStatement(model);
                rowsInserted = preparedStatement.executeUpdate();

            }
        } catch (SQLException ignore) {
        }
        return rowsInserted;
    }

    private void setModelToPreparedStatement(PageVisitModel model) throws SQLException {
        preparedStatement.setString(1, model.getEmail());
        preparedStatement.setString(2, model.getPhone());
        preparedStatement.setString(3, model.getSource());
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

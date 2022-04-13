package com.example.csvparser.handlers;

import com.example.csvparser.constants.QueryConstants;
import com.example.csvparser.model.PageVisitModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class DbHandler implements AutoCloseable {
    String url = "jdbc:h2:file:./db/proddb";
    Connection conn;
    Statement statement;
    PreparedStatement preparedStatement;

    public DbHandler(String url) {
        this.url = url;
        this.init();
    }

    //TODO: Consider using singleton
    public DbHandler() {
        this.init();
    }

    public void init() {
        try {
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();
            createFreshTable(statement);
            preparedStatement = conn.prepareStatement(QueryConstants.INSERT_ROW);
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }

    private void createFreshTable(Statement statement) throws SQLException {
        statement.execute(QueryConstants.DROP_TABLE);
        statement.execute(QueryConstants.CREATE_TABLE);
    }

    public int deleteAll() {
        try {
            return statement.executeUpdate(QueryConstants.DELETE_ALL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int insert(PageVisitModel model) {
        //TODO: Add batch counter here
        if (model.isValid()) {
            //TODO: Execute Batch
            try {
                setModelToPreparedStatement(model);
                return preparedStatement.executeUpdate();
            } catch (SQLException e) {
                //ignore duplicate
            }
        }
        return -1;
    }

    private void setModelToPreparedStatement(PageVisitModel model) throws SQLException {
        preparedStatement.setString(1, model.getEmail());
        preparedStatement.setString(2, model.getPhone());
        preparedStatement.setString(3, model.getSource());

    }

    public Optional<Long> getCount() {
        try {
            ResultSet rs = statement.executeQuery("SELECT COUNT (*) from pagevisits;");
            rs.next();
            return Optional.of(rs.getLong(1));
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void close() throws Exception {
        statement.close();
        preparedStatement.close();
        conn.close();
    }
}

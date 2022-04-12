package com.example.csvparser.handlers;

import com.example.csvparser.constants.QueryConstants;
import com.example.csvparser.model.PageVisitModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbHandler implements AutoCloseable {
    String url = "jdbc:h2:file:./db/proddb";
    Connection conn;
    Statement statement;
    PreparedStatement preparedStatement;

    public DbHandler(String url) {
        this.url = url;
    }

    //TODO: Consider using singleton
    public DbHandler() {
    }

    public void init() throws SQLException {
        conn = DriverManager.getConnection(url);
        statement = conn.createStatement();
        createFreshTable(statement);
        preparedStatement = conn.prepareStatement(QueryConstants.INSERT_ROW);
    }

    private void createFreshTable(Statement statement) throws SQLException {
        statement.execute(QueryConstants.DROP_TABLE);
        statement.execute(QueryConstants.CREATE_TABLE);
    }

    public void deleteAll() throws SQLException {
        statement.executeUpdate(QueryConstants.DELETE_ALL);
    }

    public void insert(PageVisitModel model) throws SQLException {
        if (model.isValid()) {
            setModelToPreparedStatement(model);
            //TODO: Execute Batch
            try {
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                //ignore duplicate
            }
        }
    }

    private void setModelToPreparedStatement(PageVisitModel model) throws SQLException {
        preparedStatement.setString(1, model.getEmail());
        preparedStatement.setString(2, model.getPhone());
        preparedStatement.setString(3, model.getSource());

    }

    public int getCount() throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT COUNT (*) from pagevisits;");
        rs.next();
        return rs.getInt(1);
    }

    @Override
    public void close() throws Exception {
        statement.close();
        preparedStatement.close();
        conn.close();
    }
}

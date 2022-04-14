package com.example.csvparser.constants;

public final class QueryConstants {

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS pagevisits;";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS pagevisits(email VARCHAR, phone VARCHAR, source VARCHAR, PRIMARY KEY(email,phone));";
    public static final String INSERT_ROW = "INSERT INTO pagevisits VALUES (?, ?, ?);";
    public static final String DELETE_ALL = "TRUNCATE TABLE pagevisits;";
    public static final String SELECT_COUNT = "SELECT COUNT (*) from pagevisits;";

    private QueryConstants() {
    }
}

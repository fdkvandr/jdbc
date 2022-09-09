package com.jdbcStarter.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager { //��������� ������ ������ final � ����� ��������� �����������.
    public static final String PASSWORD_KEY = "db.password";
    public static final String USER_KEY = "db.username";
    public static final String URL_KEY = "db.url";

    static { //����������� ����� ������������� ����������� ����� ���� ��� - ��� �������� ������ � meta space.
        loadDriver(); //��� �������� PostgreSQL driver, ����� ������ �������, ��� ����� �� ������ (���� ����� �������� �� Java 1.8).
    }                 //��������� ����� � ������ JVM (meta space)

    private ConnectionManager() {
    }

    public static Connection open() {
        try {
            return DriverManager.getConnection( // ����� �� ����� �������� �� ���������� � ����� ��������� ������ ������ get() �� properties �����.
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USER_KEY),
                    PropertiesUtil.get(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new RuntimeException(e); //���������� ���������� exception.
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e); //���������� ���������� exception.
        }
    }
}

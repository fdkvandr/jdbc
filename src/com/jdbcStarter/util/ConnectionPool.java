package com.jdbcStarter.util;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class ConnectionPool { //��������� ������ ������ final � ����� ��������� �����������.
    private static final String PASSWORD_KEY = "db.password";
    private static final String USER_KEY = "db.username";
    private static final String URL_KEY = "db.url";
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static final Integer DEFAULT_POOL_SIZE = 10;

    // ���������� ����������� ������� ��� ������ � ����������������
    private static BlockingQueue<Connection> pool; // ����� ����� ��������� proxyConnections � ���������������� ������� close. ����� �� ����� ���� final, ������ ��� �� ������ ��� � ����������� ������.
    private static List<Connection> sourceConnections; // ����� ����� ��������� �������� ����������, ����� �� ���������

    static {
        loadDriver(); //���������� ���� ���������� ����� �� ����������, ����� �� ��������� ������ ��� Java < 8
        initConnectionPool();
    }

    private ConnectionPool() {
    }

    public static Connection get() { // ����� ��� ����, ����� ����� ���������� �� ������� � ������� ��� ����.
        try {
            return pool.take(); // �������� ����������, ���� ��� ����, � ���� ������, �� �� ���� � �������.
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closePool() { // ����� ������� ��������� ��� ���� ����������
        for (Connection sourceConnection : sourceConnections) {
            try {
                sourceConnection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void initConnectionPool() { // �������� ������� � ���������� ���� ��������� ����������.
        String poolSize = PropertiesUtil.get(POOL_SIZE_KEY);
        int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize); // poolSize ����� �� ������������, ������� ��� ������� �������� ��������
        pool = new ArrayBlockingQueue<>(size);
        sourceConnections = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Connection connection = open(); // ��������� ����������
            sourceConnections.add(connection); // ��������� ��� � ����, ����� ����� �������� �� ���� � ������� �� ���� � ������ closePool()
            //���������� reflection api ��� �������� ������ ������� �� ������ ������ connection � ������� ����� �������������� ������ ������ close ����� add
            Connection proxyConnection = (Connection) Proxy.newProxyInstance(ConnectionPool.class.getClassLoader(), new Class[]{Connection.class},
                    (proxy, method, args) -> method.getName().equals("close")
                            ? pool.add((Connection) proxy)
                            : method.invoke(connection, args));
            pool.add(proxyConnection); //��������� � ������� ��� connection
        }
    }

    private static Connection open() {
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

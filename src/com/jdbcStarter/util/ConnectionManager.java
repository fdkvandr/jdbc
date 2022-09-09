package com.jdbcStarter.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager { //Утилитные классы всегда final и имеет приватный конструктор.
    public static final String PASSWORD_KEY = "db.password";
    public static final String USER_KEY = "db.username";
    public static final String URL_KEY = "db.url";

    static { //Статический класс инициализации срабатывает всего один раз - при загрузки класса в meta space.
        loadDriver(); //Для загрузки PostgreSQL driver, чтобы небыло проблем, что класс не найден (была такая проблема до Java 1.8).
    }                 //Загружаем класс в память JVM (meta space)

    private ConnectionManager() {
    }

    public static Connection open() {
        try {
            return DriverManager.getConnection( // Берем по ключу значения из созданного в нашем утилитном классе метода get() из properties файла.
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USER_KEY),
                    PropertiesUtil.get(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new RuntimeException(e); //Необходимо обработать exception.
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e); //Необходимо обработать exception.
        }
    }
}

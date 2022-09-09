package com.jdbcStarter;


import com.jdbcStarter.util.ConnectionManager;
import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS info(
                    id SERIAL PRIMARY KEY,
                    data TEXT NOT NULL
                );
                """;

        try (Connection connection = ConnectionManager.open()) { //Используем метод open() который мы создали для создания connection.

            Statement statement = connection.createStatement(); //У Сonnection вызываем метод createStatement() который возвращает объект Statement.
            boolean executeResult = statement.execute(sql);

            System.out.println(executeResult); //Вернул false.
        }
    }
}

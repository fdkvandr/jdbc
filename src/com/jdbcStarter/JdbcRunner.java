package com.jdbcStarter;


import com.jdbcStarter.util.ConnectionManager;
import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {

        String sql1 = """
                CREATE TABLE IF NOT EXISTS info(
                    id SERIAL PRIMARY KEY,
                    data TEXT NOT NULL
                );
                """;

        String sql2 = """
                INSERT INTO info (data)
                VALUES ('Test1'), ('Test2'), ('Test3'), ('Test4');
                """;

        try (Connection connection = ConnectionManager.open()) { //���������� ����� open() ������� �� ������� ��� �������� connection.

            Statement statement = connection.createStatement(); //� �onnection �������� ����� createStatement() ������� ���������� ������ Statement.
            boolean executeResult1 = statement.execute(sql1); //������ false.
            boolean executeResult2 = statement.execute(sql2); //������ false.
            System.out.println(statement.getUpdateCount()); //������ 4.

            int executeResult3 = statement.executeUpdate(sql2); //������ 4.


        }
    }
}

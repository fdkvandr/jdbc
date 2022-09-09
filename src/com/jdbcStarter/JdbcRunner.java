package com.jdbcStarter;


import com.jdbcStarter.util.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {

        String sql1 = """
                SELECT *
                FROM ticket;
                """;

        try (Connection connection = ConnectionManager.open();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {//�������� � try with resources.

            ResultSet executeResult = statement.executeQuery(sql1); //ResultSet ����� �� ����������� � try with resources, �.�. ��������� ������������� ����� ��� ��������� statement.
            while (executeResult.next()) {
                System.out.println(executeResult.getLong("id"));
                System.out.println(executeResult.getString("passenger_no"));
                System.out.println(executeResult.getBigDecimal("cost")); //BigDecimal - ����� ������ ��� Double, �� ������������ � �� ���������� ������������ �����.
                executeResult.first(); //last(), beforeFirst(), afterLast(), isLast(), isFirst().
                executeResult.updateLong("id", 1000); //��������� ������� ������ �� ������� �����������.
                System.out.println("--------");
            }
        }
    }
}

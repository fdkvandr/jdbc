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
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {//Обернуто в try with resources.

            ResultSet executeResult = statement.executeQuery(sql1); //ResultSet можно не оборачивать в try with resources, т.к. закроетса автоматически сразу как закроется statement.
            while (executeResult.next()) {
                System.out.println(executeResult.getLong("id"));
                System.out.println(executeResult.getString("passenger_no"));
                System.out.println(executeResult.getBigDecimal("cost")); //BigDecimal - более точный чем Double, он безграничный и не откидывает вещественную часть.
                executeResult.first(); //last(), beforeFirst(), afterLast(), isLast(), isFirst().
                executeResult.updateLong("id", 1000); //Обновляет текущую строку по которой итерируемся.
                System.out.println("--------");
            }
        }
    }
}

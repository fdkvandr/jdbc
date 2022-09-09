package com.jdbcStarter;


import com.jdbcStarter.util.ConnectionManager;
import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {

        try (Connection connection = ConnectionManager.open()) {

        }
    }
}

package com.jdbcStarter;


import com.jdbcStarter.util.ConnectionManager;
import com.jdbcStarter.util.ConnectionPool;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {
        try {
            checkMetaData();
        } finally {
            ConnectionPool.closePool();
        }
    }

    private static void checkMetaData() throws SQLException {
        try (Connection connection = ConnectionPool.get()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet catalogs = metaData.getCatalogs(); //�������� ���� ������
            while (catalogs.next()) {
                String catalog = catalogs.getString("TABLE_CAT");

                ResultSet schemas = metaData.getSchemas();
                while (schemas.next()) {
                    String schema = schemas.getString("TABLE_SCHEM");
                    if (schema.equals("public")) {
                        ResultSet tables = metaData.getTables(catalog, schema, "%", null);
                        while (tables.next()) {
                            String table_name = tables.getString("TABLE_NAME");
                            System.out.println(String.format("%s.%s.%s", catalog, schema, table_name));
                        }
                    }
                }
            }
        }
    }
}


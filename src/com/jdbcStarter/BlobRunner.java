package com.jdbcStarter;

import com.jdbcStarter.util.ConnectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlobRunner {

    public static void main(String[] args) throws SQLException, IOException {
        saveImage(); // для сохранения в БД
        getImage(); // для сохранения на компьютер
    }

    private static void saveImage() throws SQLException, IOException {

        String sql = """
                UPDATE aircraft
                SET image = ?
                WHERE id = 1;
                """;

        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setBytes(1, Files.readAllBytes(Path.of("resources", "boeing.png"))); // для типа bytea в PostgreSQL
//            preparedStatement.setString(); // для типа text в PostgreSQL
            preparedStatement.executeUpdate();
        }
    }

    private static void getImage() throws SQLException, IOException {
        String sql = """
                SELECT image
                FROM aircraft
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, 1);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                byte[] image = resultSet.getBytes("image");
                Files.write(Path.of("resources", "boeing777new.jpg"), image, StandardOpenOption.CREATE);
            }
        }
    }
}

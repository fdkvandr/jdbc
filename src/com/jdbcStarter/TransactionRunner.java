package com.jdbcStarter;

import com.jdbcStarter.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionRunner {

    public static void main(String[] args) throws SQLException {
        long fligthId = 9;
        String deleteFlightSql = "DELETE FROM flight WHERE id = ?;";

        String deleteTicketsSql = "DELETE FROM ticket WHERE flight_id = ?;";

        Connection connection = null; // мы должны вынести объявление соединения, чтобы потом обращаться к ним для выполнения метода rollback
        PreparedStatement deleteFlightStatement = null; // мы должны вынести объявление statement`ов, чтобы потом обращаться к ним для выполнения метода rollback
        PreparedStatement deleteTicketsStatement = null; // мы должны вынести объявление statement`ов, чтобы потом обращаться к ним для выполнения метода rollback

        // Тпеерь мы не можем использовать try with resources и поэтому нам придется самим их закрывать. Используем просто блок try catch
        try {
            connection = ConnectionManager.open(); // инициализируем теперь тут
            deleteFlightStatement = connection.prepareStatement(deleteFlightSql); // инициализируем теперь тут
            deleteTicketsStatement = connection.prepareStatement(deleteTicketsSql); // инициализируем теперь тут

            connection.setAutoCommit(false); // Убираем автокоммит

            deleteTicketsStatement.setLong(1, fligthId);
            deleteFlightStatement.setLong(1, fligthId);

            deleteTicketsStatement.executeUpdate();
            if (true) {
                throw new RuntimeException("Oooops");
            }
            deleteFlightStatement.executeUpdate();

            connection.commit(); // Сохраняем сразу все наши изменения. Если будет включен автокоммит, то вызвав этот метод проихойдет исключение
        } catch (Exception e) {
            if (connection != null) { // Если произойдет ошибка при инициализации connection`a то он будет null и мы не сможем к нему обращаться
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (deleteFlightStatement != null) {
                deleteFlightStatement.close();
            }
            if (deleteTicketsStatement != null) {
                deleteTicketsStatement.close();
            }
        }
    }
}

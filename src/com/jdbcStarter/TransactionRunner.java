package com.jdbcStarter;

import com.jdbcStarter.util.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TransactionRunner {

    public static void main(String[] args) throws SQLException {
        long fligthId = 8;
        String deleteFlightSql = "DELETE FROM flight WHERE id = " + fligthId + ';';
        String deleteTicketsSql = "DELETE FROM ticket WHERE flight_id = " + fligthId + ';';

        Connection connection = null; // мы должны вынести объявление соединения, чтобы потом обращаться к ним для выполнения метода rollback
        Statement statement = null;
        // Тпеерь мы не можем использовать try with resources и поэтому нам придется самим их закрывать. Используем просто блок try catch
        try {
            connection = ConnectionPool.get(); // инициализируем теперь тут
            connection.setAutoCommit(false); // Убираем автокоммит

            statement = connection.createStatement();
            statement.addBatch(deleteTicketsSql);
            statement.addBatch(deleteFlightSql);

            int[] ints = statement.executeBatch();

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
            if (statement != null) {
                statement.close();
            }
        }

        try {
            connection = ConnectionPool.get(); // инициализируем теперь тут
            connection.setAutoCommit(false); // Убираем автокоммит

            statement = connection.createStatement();
            statement.addBatch(deleteTicketsSql);
            statement.addBatch(deleteFlightSql);

            int[] ints = statement.executeBatch();

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
            if (statement != null) {
                statement.close();
            }
        }


    }
}

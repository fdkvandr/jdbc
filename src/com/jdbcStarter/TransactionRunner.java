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

        Connection connection = null; // �� ������ ������� ���������� ����������, ����� ����� ���������� � ��� ��� ���������� ������ rollback
        Statement statement = null;
        // ������ �� �� ����� ������������ try with resources � ������� ��� �������� ����� �� ���������. ���������� ������ ���� try catch
        try {
            connection = ConnectionPool.get(); // �������������� ������ ���
            connection.setAutoCommit(false); // ������� ����������

            statement = connection.createStatement();
            statement.addBatch(deleteTicketsSql);
            statement.addBatch(deleteFlightSql);

            int[] ints = statement.executeBatch();

            connection.commit(); // ��������� ����� ��� ���� ���������. ���� ����� ������� ����������, �� ������ ���� ����� ���������� ����������
        } catch (Exception e) {
            if (connection != null) { // ���� ���������� ������ ��� ������������� connection`a �� �� ����� null � �� �� ������ � ���� ����������
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

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

        Connection connection = null; // �� ������ ������� ���������� ����������, ����� ����� ���������� � ��� ��� ���������� ������ rollback
        PreparedStatement deleteFlightStatement = null; // �� ������ ������� ���������� statement`��, ����� ����� ���������� � ��� ��� ���������� ������ rollback
        PreparedStatement deleteTicketsStatement = null; // �� ������ ������� ���������� statement`��, ����� ����� ���������� � ��� ��� ���������� ������ rollback

        // ������ �� �� ����� ������������ try with resources � ������� ��� �������� ����� �� ���������. ���������� ������ ���� try catch
        try {
            connection = ConnectionManager.open(); // �������������� ������ ���
            deleteFlightStatement = connection.prepareStatement(deleteFlightSql); // �������������� ������ ���
            deleteTicketsStatement = connection.prepareStatement(deleteTicketsSql); // �������������� ������ ���

            connection.setAutoCommit(false); // ������� ����������

            deleteTicketsStatement.setLong(1, fligthId);
            deleteFlightStatement.setLong(1, fligthId);

            deleteTicketsStatement.executeUpdate();
            if (true) {
                throw new RuntimeException("Oooops");
            }
            deleteFlightStatement.executeUpdate();

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
            if (deleteFlightStatement != null) {
                deleteFlightStatement.close();
            }
            if (deleteTicketsStatement != null) {
                deleteTicketsStatement.close();
            }
        }
    }
}

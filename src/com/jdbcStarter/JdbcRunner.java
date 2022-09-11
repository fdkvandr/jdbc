package com.jdbcStarter;


import com.jdbcStarter.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {
        Long fligthId = 2L; //��� L ����� int.
        List<Long> result = getTicketsByFlighId(fligthId);
        System.out.println(result);
    }

    private static List<Long> getTicketsByFlighId(Long flightId) throws SQLException {
        String sql = """
                SELECT id 
                FROM ticket
                WHERE flight_id = ?;
                """; // ? ����������, ��� ���� ����� �������� ���������� ��������.

        List<Long> result = new ArrayList<>();
        try (Connection connection = ConnectionManager.open();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setLong(1, flightId); // ������ ���������� ���������� ����� � �������� ����������� ���������. ����� �������� ��������, ��� �������� � �����������, ������� �� �����, ��� ����� null.
            preparedStatement.setObject(1, flightId, java.sql.Types.BIGINT); //NULL SAFE.
            ResultSet resultSet = preparedStatement.executeQuery(); // ������ �� ���������
            while (resultSet.next()) {
                result.add(resultSet.getObject("id", Long.class)); // NULL SAFE. ���� ���� ������� ����� ����� null ��������, ��, ���������
                                                                             // ������ ���� getLong ���������� �������� long, � ������ � null,
                                                                             // �� �� ������ ��� �������� � ������������ ����, ������� ������������ getObject.
            }
        }
        return result;
    }
}


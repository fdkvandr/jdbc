package com.jdbcStarter.dao;

import com.jdbcStarter.entity.TicketEntity;
import com.jdbcStarter.exception.DaoExcaption;
import com.jdbcStarter.util.ConnectionPool;

import java.sql.*;

public class TicketDao { // �� ����� ��������� final ����� ������ ��� � �������� �����������, ��� Hibernate � Spring ����� ����� ������� Proxy �� ���� ������, ������������� �� ����� ����� ������

    private static final TicketDao INSTANCE = new TicketDao(); //������������� single tone. �.�. ������� ����� ������ � ������ private �����������, ����� ����� ������ �� ���� ������� ���� ������. ����� �������, �� ����� ������������
    private static final String DELETE_SQL = """
            DELETE FROM ticket
            WHERE id = ?;
            """;
    private static final String SAVE_SQL = """
            INSERT INTO ticket (passenger_no, passenger_name, flight_id, seat_no, cost)
            VALUES (?, ?, ?, ?, ?);
            """;

    private TicketDao() {
    }

    public static TicketDao getInstance() { //������ ��� �������, ������� ����� �������� � TicketDao, ����� ������ �������� ���� �����
        return INSTANCE;
    }

    public boolean delete(Long id) { //����� ��������� ������������ ��� void, ���� ��� �� ���������, ��������� ������ ��� ����� ������ �� ������� � ������ �� ���������
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoExcaption(e);
        }
    }

    //����� update ����� ����� �� save, � ORM ���������� �� ����������, ������ ��� ����� �� ������ ��������� ��� ���� ����, ������� � ��� ����
    public TicketEntity save(TicketEntity ticketEntity) { // ������ ������������ ���� ���� ������ (c ������������� id) ���� ������ ��� id. ��� ticketEntity ��� �������� ��� �������� ��������� ���-�� �� ������ Service, ������� ��������� �� ������� Dao, ��� �� ������� �� ���� SQL ������
        try (Connection connection = ConnectionPool.get();
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) { //  Statement.RETURN_GENERATED_KEYS ����� ������ ����
            preparedStatement.setString(1, ticketEntity.getPassengerNo());
            preparedStatement.setString(2, ticketEntity.getPassengerName());
            preparedStatement.setLong(3, ticketEntity.getFlightId()); //������� ��������, ��� ��� null ���������� ����� ������ ����� setObject
            preparedStatement.setString(4, ticketEntity.getSeatNo());
            preparedStatement.setBigDecimal(5, ticketEntity.getCost());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) { //��������� ���� ���� (���� ������). �� ���� ����� ���� if �� ������, ������ ��� �� � ��� ������ �����.
                ticketEntity.setId(generatedKeys.getLong("id")); // ���������� ��� id � entity
            }
            return ticketEntity;
        } catch (SQLException e) {
            throw new DaoExcaption(e);
        }
    }
}

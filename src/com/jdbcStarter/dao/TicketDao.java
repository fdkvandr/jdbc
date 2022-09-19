package com.jdbcStarter.dao;

import com.jdbcStarter.entity.TicketEntity;
import com.jdbcStarter.exception.DaoExcaption;
import com.jdbcStarter.util.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private static final String UPDATE_SQL = """
            UPDATE ticket
            SET passenger_no = ?,
                passenger_name = ?,
                flight_id = ?,
                seat_no = ?,
                cost = ?
            WHERE id = ?;
            """;
    public static final String FIND_ALL_SQL = """
            SELECT id, passenger_no, passenger_name, flight_id, seat_no, cost
            FROM ticket
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?;
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

    public void update(TicketEntity ticketEntity) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, ticketEntity.getPassengerNo());
            preparedStatement.setString(2, ticketEntity.getPassengerName());
            preparedStatement.setLong(3, ticketEntity.getFlightId());
            preparedStatement.setString(4, ticketEntity.getSeatNo());
            preparedStatement.setBigDecimal(5, ticketEntity.getCost());
            preparedStatement.setLong(6, ticketEntity.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoExcaption(e);
        }
    }

    public List<TicketEntity> findAll() {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            List<TicketEntity> ticketEntitys = new ArrayList<>(); //���������� ������ ���������, ���� �� ����� � ���� while
            while (resultSet.next()) {
                ticketEntitys.add(buildTicket(resultSet));
            }

            return ticketEntitys;
        } catch (SQLException e) {
            throw new DaoExcaption(e);
        }
    }

    public Optional<TicketEntity> findById(Long id) { //������� �������� ����, ��� ��� ����� ��������� null, �� ���������� Optional. � ������ � ����������� - ������ ���������.
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            TicketEntity ticketEntity = null;
            if (resultSet.next()) {
                ticketEntity = buildTicket(resultSet);
            }

            return Optional.ofNullable(ticketEntity);
        } catch (SQLException e) {
            throw new DaoExcaption(e);
        }
    }

    private TicketEntity buildTicket(ResultSet resultSet) throws SQLException{
        return new TicketEntity(
                resultSet.getLong("id"),
                resultSet.getString("passenger_no"),
                resultSet.getString("passenger_name"),
                resultSet.getLong("flight_id"),
                resultSet.getString("seat_no"),
                resultSet.getBigDecimal("cost")
        );
    }
}
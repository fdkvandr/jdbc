package com.jdbcStarter.dao;

import com.jdbcStarter.entity.FlightEntity;
import com.jdbcStarter.exception.DaoExcaption;
import com.jdbcStarter.util.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FlightDao implements Dao<Long, FlightEntity> {

    private static final FlightDao INSTANCE = new FlightDao();

    private static final String FIND_BY_ID_SQL = """
            SELECT id, flight_no, departure_date, departure_airport_code, arrival_date, arrival_airport_code, status, aircraft_id
            FROM flight
            WHERE id = ?
            """;

    private FlightDao() {
    }

    public static FlightDao getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public FlightEntity save(FlightEntity entity) {
        return null;
    }

    @Override
    public void update(FlightEntity entity) {

    }

    @Override
    public Optional<FlightEntity> findById(Long id) {
        try (Connection connection = ConnectionPool.get()) {

            return findById(id, connection); // т.е. мы вызываем перегруженный метод передава€ созданный connection
        } catch (SQLException e) {
            throw new DaoExcaption(e);
        }
    }

    public Optional<FlightEntity> findById(Long id, Connection connection) { //  огда мы передаем еще и connection то мы его не открываем и не закрываем, а просто возвращаем назад. ћы просто его переиспользуем
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            FlightEntity flightEntity = null;
            if (resultSet.next()) {
                flightEntity = new FlightEntity(resultSet.getLong("id"),
                        resultSet.getString("flight_no"),
                        resultSet.getTimestamp("departure_date").toLocalDateTime(),
                        resultSet.getString("departure_airport_code"),
                        resultSet.getTimestamp("arrival_date").toLocalDateTime(),
                        resultSet.getString("arrival_airport_code"),
                        resultSet.getInt("aircraft_id"),
                        resultSet.getString("status")
                );
            }

            return Optional.ofNullable(flightEntity);
        } catch (SQLException e) {
            throw new DaoExcaption(e);
        }
    }

    @Override
    public List<FlightEntity> findAll() {
        return null;
    }
}

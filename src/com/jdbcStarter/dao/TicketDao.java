package com.jdbcStarter.dao;

import com.jdbcStarter.dto.TicketFilter;
import com.jdbcStarter.entity.FlightEntity;
import com.jdbcStarter.entity.TicketEntity;
import com.jdbcStarter.exception.DaoExcaption;
import com.jdbcStarter.util.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

public class TicketDao implements Dao<Long, TicketEntity>{ // Не стоит создавать final класс потому что в разлиных фреймворках, как Hibernate и Spring очень часто создают Proxy на наши классы, следовательно не стоит этого делать

    public static final String FIND_ALL_SQL = """
            SELECT id, passenger_no, passenger_name, flight_id, seat_no, cost
            FROM ticket
            """;
    private static final TicketDao INSTANCE = new TicketDao(); //Реализовываем single tone. Т.е. создаем сразу объект и делаем private конструктор, чтобы никто другой не смог создать этот объект. Таким образом, он будет единственным
    private static final String DELETE_SQL = """
            DELETE FROM ticket
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO ticket (passenger_no, passenger_name, flight_id, seat_no, cost)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE ticket
            SET passenger_no = ?,
                passenger_name = ?,
                flight_id = ?,
                seat_no = ?,
                cost = ?
            WHERE id = ?
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;

    FlightDao flightDao = FlightDao.getInstance(); // Создаем объект flightDao чтобы явно видеть зависимость.

    private TicketDao() {
    }

    public static TicketDao getInstance() { //Теперь все сервисы, которые будут работать с TicketDao, будут просто вызывать этот метод
        return INSTANCE;
    }

    public boolean delete(Long id) { //можно поставить возвращаемый тип void, если нам не интересно, удалилась запись или такой записи не нашлось и ничего не удалилось
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoExcaption(e);
        }
    }

    //метод update очень похож на save, и ORM фреймворки их объединяют, потому что также мы должны обновлять все наши поля, которые у нас есть
    public TicketEntity save(TicketEntity ticketEntity) { // Обычно возвращается либо весь объект (c установленным id) либо только его id. Сам ticketEntity для передачи как параметр создается где-то на уровне Service, который переается на уровень Dao, где мы создаем из него SQL запрос
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) { //  Statement.RETURN_GENERATED_KEYS чтобы вернул ключ
            preparedStatement.setString(1, ticketEntity.getPassengerNo());
            preparedStatement.setString(2, ticketEntity.getPassengerName());
            preparedStatement.setLong(3, ticketEntity.getFlightEntity().id()); // Берем id потому что это и есть flight.id = flight_id поле. Обращаю внимание, что при null необходимо будет делать через setObject
            preparedStatement.setString(4, ticketEntity.getSeatNo());
            preparedStatement.setBigDecimal(5, ticketEntity.getCost());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) { //поскольку один ключ (одна запись). По идее можно даже if не писать, потому что он у нас всегда будет.
                ticketEntity.setId(generatedKeys.getLong("id")); // выставляем наш id в entity
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
            preparedStatement.setLong(3, ticketEntity.getFlightEntity().id());// Берем id потому что это и есть flight_id поле. О
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

            List<TicketEntity> ticketEntitys = new ArrayList<>(); //возвращаем пустую коллекцию, если не зашли в цикл while
            while (resultSet.next()) {
                ticketEntitys.add(buildTicket(resultSet));
            }

            return ticketEntitys;
        } catch (SQLException e) {
            throw new DaoExcaption(e);
        }
    }

    public Optional<TicketEntity> findById(Long id) { //Правило хорошего тона, там где может вернуться null, то возвращать Optional. В случае с коллекциями - пустую коллекцию.
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

    public List<TicketEntity> findAll(TicketFilter filter) {
        List<Object> parameters = new ArrayList<>(); //Тут будут параметры для нашего запроса (на место '?')
        List<String> whereSQL = new ArrayList<>(); //Здесь будут параметры для запросов WHERE, LIKE
        if (filter.seatNo() != null) { // Если мы хотим добавлять параметры, которые могут быть null
            whereSQL.add("seat_no LIKE ?");
            parameters.add("%" + filter.seatNo() + "%"); // "%" ставятся для фильтра LIKE. Для полного соответсвия их не указывают
        }
        if (filter.passengerName() != null) {
            whereSQL.add("passenger_name = ?");
            parameters.add(filter.passengerName());
        }
        parameters.add(filter.limit()); //Поскольку мы создавали класс record, то getter выглядит просто как название полей.
        parameters.add(filter.offset());

        String where = ""; //Чтобы если все параметры будут null, добавить пустую строку.
        where = where + (whereSQL.isEmpty() ? "" : " WHERE ") + whereSQL.stream().collect(joining(" AND ", "", "LIMIT ? OFFSET ?"));

        String sql = FIND_ALL_SQL + where;

        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) { //SQL условие у нас будет динамическим. Он будет выглядить как FIND_ALL_SQL, но к нему будет добавляться различные условия WHERE. Поэтому SQL будет формироваться во время выполнения метода findAll()
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i)); // И заполняем из нашего массива параметры. Лучше всего как раз использовать ArrayList, потому что у него есть доступ по индексу.
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            List<TicketEntity> ticketEntities = new ArrayList<>();
            while (resultSet.next()) {
                ticketEntities.add(buildTicket(resultSet));
            }

            return ticketEntities;
        } catch (SQLException e) {
            throw new DaoExcaption(e);
        }
    }

    private TicketEntity buildTicket(ResultSet resultSet) throws SQLException {
        return new TicketEntity(
                resultSet.getLong("id"),
                resultSet.getString("passenger_no"),
                resultSet.getString("passenger_name"),
                flightDao.findById(resultSet.getLong("flight_id"),
                        resultSet.getStatement().getConnection()).orElse(null), //Каждый resultSet знает о prepareStatement, который его открыл, а он знает о соединении, который его создал. Таким образом мы можем получить доступ к connection из нашего resultSet. Вставляем ссылку на наш только что созданный объект. Поскольку он у нас Optional, то пишем orElse(null)
                resultSet.getString("seat_no"),
                resultSet.getBigDecimal("cost")
        );
    }
}

package com.jdbcStarter;


import com.jdbcStarter.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {
        Long fligthId = 2L; //без L будет int.
        List<Long> result = getTicketsByFlighId(fligthId);
        System.out.println(result);
    }

    private static List<Long> getTicketsByFlighId(Long flightId) throws SQLException {
        String sql = """
                SELECT id 
                FROM ticket
                WHERE flight_id = ?;
                """; // ? обозначает, что сюда будет вставлен изменяемый параметр.

        List<Long> result = new ArrayList<>();
        try (Connection connection = ConnectionManager.open();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setLong(1, flightId); // Должны установить порядковый номер и значение изменяемого параметра. Опять обращаем внимание, что работаем с примитивами, которые не знают, что такое null.
            preparedStatement.setObject(1, flightId, java.sql.Types.BIGINT); //NULL SAFE.
            ResultSet resultSet = preparedStatement.executeQuery(); // ничего не вставляем
            while (resultSet.next()) {
                result.add(resultSet.getObject("id", Long.class)); // NULL SAFE. Если наша колонка может иметь null значения, то, поскольку
                                                                             // методы типа getLong возвращают примитив long, в случае с null,
                                                                             // мы не сможем его привести к примитивному типу, поэтому используется getObject.
            }
        }
        return result;
    }
}


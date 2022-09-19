package com.jdbcStarter;

import com.jdbcStarter.dao.TicketDao;
import com.jdbcStarter.entity.TicketEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class DaoRunner {

    public static void main(String[] args) {
        List<TicketEntity> ticketEntitys = TicketDao.getInstance().findAll();
        System.out.println(ticketEntitys);
    }

    private static void updateTest() {
        TicketDao ticketDao = TicketDao.getInstance();
        Optional<TicketEntity> maybeTicket = findByIdTest(221L);

        maybeTicket.ifPresent(ticket -> {
            ticket.setCost(BigDecimal.valueOf(188.88));
            ticketDao.update(ticket);
        });

        findByIdTest(221L);
    }

    public static Optional<TicketEntity> findByIdTest(Long id) {
        TicketDao ticketDao = TicketDao.getInstance();
        Optional<TicketEntity> ticketEntity = ticketDao.findById(id);
        System.out.println(ticketEntity);
        return ticketEntity;
    }

    public static void deleteTest(Long id) {
        TicketDao ticketDao = TicketDao.getInstance();
        boolean deleteResult = ticketDao.delete(id);
        System.out.println(deleteResult);
    }

    public static void saveTest() {
        TicketDao ticketDao = TicketDao.getInstance();
        TicketEntity ticketEntity = new TicketEntity();
        ticketEntity.setPassengerNo("1234567");
        ticketEntity.setPassengerName("test");
        ticketEntity.setFlightId(3L);
        ticketEntity.setSeatNo("B3");
        ticketEntity.setCost(BigDecimal.TEN);

        TicketEntity savedTicketEntity = ticketDao.save(ticketEntity);
        System.out.println(savedTicketEntity);
    }
}

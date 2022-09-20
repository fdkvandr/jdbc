package com.jdbcStarter.dto;

public record TicketFilter(int limit, // record нам автоматически сгенерируют конструкторы для всех полей которые мы перечислили в круглых скобочках, сразу же реализуют toString, equals, hashCode и getter к нашим полям. setter здесь нельзя делать поскольку это immutable объект, его изменить после создания нельзя.
                           int offset,
                           String passengerName,
                           String seatNo) {

}

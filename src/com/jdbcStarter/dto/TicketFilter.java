package com.jdbcStarter.dto;

public record TicketFilter(int limit, // record ��� ������������� ����������� ������������ ��� ���� ����� ������� �� ����������� � ������� ���������, ����� �� ��������� toString, equals, hashCode � getter � ����� �����. setter ����� ������ ������ ��������� ��� immutable ������, ��� �������� ����� �������� ������.
                           int offset,
                           String passengerName,
                           String seatNo) {

}

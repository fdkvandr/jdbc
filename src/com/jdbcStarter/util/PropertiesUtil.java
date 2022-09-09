package com.jdbcStarter.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {

    //��� ������������� ������ properties  ����� ���� ����������� ����� Properties (extends Hashtable), �.�. ������������� ������.
    public static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private PropertiesUtil() {
    }

    public static String get(String key) { //�����, ������� ����� ���������� �� ������ ����� ��������.
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        //����� ������� �� ������ ����� ����������� �� ������ properties ����� ���� �� ����� � ��� � ������� /src � ������� ���.
        try (InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")) { //��������� InputStream �������������� ��������� Closable �� ����������� ��� � try with resources.
            PROPERTIES.load(inputStream); //��������� ��� properties ���� � ��������� ������ ������ Properties.
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

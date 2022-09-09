package com.jdbcStarter.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {

    //Для представления нашего properties  файла есть специальный класс Properties (extends Hashtable), т.е. ассоциятивный массив.
    public static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private PropertiesUtil() {
    }

    public static String get(String key) { //Метод, который будет возвращать по нашему ключу значение.
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        //Таким образом мы всегда можем достучаться до нашего properties файла если он лежит у нас в папочке /src и считать его.
        try (InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")) { //Поскольку InputStream имплементирует интерфейс Closable то обарачиваем все в try with resources.
            PROPERTIES.load(inputStream); //Загружаем наш properties файл в созданный объект класса Properties.
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

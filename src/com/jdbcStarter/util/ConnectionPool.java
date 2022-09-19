package com.jdbcStarter.util;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class ConnectionPool { //”тилитные классы всегда final и имеет приватный конструктор.
    private static final String PASSWORD_KEY = "db.password";
    private static final String USER_KEY = "db.username";
    private static final String URL_KEY = "db.url";
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static final Integer DEFAULT_POOL_SIZE = 10;

    // »спользуем блокирующую очередь дл€ работы с многопоточностью
    private static BlockingQueue<Connection> pool; // «десь будут хранитьс€ proxyConnections с переопределенным методом close. «десь не может быть final, потому что мы делаем это в статическом методе.
    private static List<Connection> sourceConnections; // «десь будут хранитьс€ исходные соединени€, чтобы их закрывать

    static {
        loadDriver(); //Ќеобходимо €вно подгрузить класс из библиотеки, чтобы не возникало ошибок дл€ Java < 8
        initConnectionPool();
    }

    private ConnectionPool() {
    }

    public static Connection get() { // метод дл€ того, чтобы вз€ть соединение из очереди и вернуть его туда.
        try {
            return pool.take(); // забираем соединение, если оно есть, а если пустой, то он ждем в очереди.
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closePool() { // ћетод который закрывает все наши соединени€
        for (Connection sourceConnection : sourceConnections) {
            try {
                sourceConnection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void initConnectionPool() { // —оздание очереди и добавление туда созданных соединений.
        String poolSize = PropertiesUtil.get(POOL_SIZE_KEY);
        int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize); // poolSize может не установитьс€, поэтому еще зададим дефолное значение
        pool = new ArrayBlockingQueue<>(size);
        sourceConnections = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Connection connection = open(); // ќткрываем соединение
            sourceConnections.add(connection); // ƒобавл€ем его в лист, чтобы потом пройтись по нему и закрыть их всех в методу closePool()
            //»спользуем reflection api дл€ создани€ прокси объекта на основе нашего connection в котором будет использоватьс€ вместо метода close метод add
            Connection proxyConnection = (Connection) Proxy.newProxyInstance(ConnectionPool.class.getClassLoader(), new Class[]{Connection.class},
                    (proxy, method, args) -> method.getName().equals("close")
                            ? pool.add((Connection) proxy)
                            : method.invoke(connection, args));
            pool.add(proxyConnection); //ƒобавл€ем в очередь наш connection
        }
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection( // Ѕерем по ключу значени€ из созданного в нашем утилитном классе метода get() из properties файла.
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USER_KEY),
                    PropertiesUtil.get(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new RuntimeException(e); //Ќеобходимо обработать exception.
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e); //Ќеобходимо обработать exception.
        }
    }
}

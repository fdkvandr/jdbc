package com.jdbcStarter.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> { //K - ключ id (Long, Integer), по которому мы параметризуем, E - entity по которому мы параметризуем

    boolean delete (K id);

    E save(E entity);

    void update(E entity);

    Optional<E> findById(K id);

    List<E> findAll();
}

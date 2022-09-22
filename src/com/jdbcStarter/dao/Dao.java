package com.jdbcStarter.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> { //K - ���� id (Long, Integer), �� �������� �� �������������, E - entity �� �������� �� �������������

    boolean delete (K id);

    E save(E entity);

    void update(E entity);

    Optional<E> findById(K id);

    List<E> findAll();
}

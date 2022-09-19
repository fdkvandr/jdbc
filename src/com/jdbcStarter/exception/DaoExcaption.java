package com.jdbcStarter.exception;

public class DaoExcaption extends RuntimeException{

    public DaoExcaption(Throwable throwable) {
        super(throwable);
    }
}

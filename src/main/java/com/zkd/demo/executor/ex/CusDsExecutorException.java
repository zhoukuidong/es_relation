package com.zkd.demo.executor.ex;

/**
 * 自定义异常
*/
public class CusDsExecutorException extends RuntimeException {
    public CusDsExecutorException(String message) {
        super(message);
    }
}

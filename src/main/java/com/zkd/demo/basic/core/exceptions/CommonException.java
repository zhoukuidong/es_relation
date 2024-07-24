package com.zkd.demo.basic.core.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommonException extends RuntimeException {
    public Integer code;

    public String msg;

    public CommonException(IErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public CommonException(int errorCode, String msg) {
        this.code = errorCode;
        this.msg = msg;
    }

    public static CommonException formatException(IErrorCode iError, Object... args) {
        return new CommonException(iError.getCode(), MessageFormat.format(iError.getMsg(), args));
    }

    public static CommonException formatException(int errorCode, String msg, Object... args) {
        return new CommonException(errorCode, MessageFormat.format(msg, args));
    }
}
package com.zkd.demo.basic.core.result;

import com.zkd.demo.basic.core.exceptions.IErrorCode;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class R<T> {

    private static final int SUCCESS_CODE = 200;
    private static final String SUCCESS_STR = "成功";
    private static final int ERROR_CODE = 500;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 返回code码
     */
    private Integer code;

    /**
     * 返回对象信息
     */
    private T data;

    /**
     * 成功标识
     */
    private boolean success;

    public R() {
        this.requestId = UUID.randomUUID().toString();
    }

    public static R ok() {
        return new R().setCode(SUCCESS_CODE).setMsg(SUCCESS_STR).setSuccess(true);
    }

    public static <T> R<T> ok(T data) {
        return new R<T>().setCode(SUCCESS_CODE).setMsg(SUCCESS_STR).setData(data).setSuccess(true);
    }

    public static R fail(int code, String msg) {
        return new R().setCode(code).setMsg(msg).setSuccess(false);
    }

    public Boolean isSuccess() {
        return null != this.code && SUCCESS_CODE == this.code ? Boolean.TRUE : Boolean.FALSE;
    }

    public static R fail(IErrorCode errorCode) {
        return new R().setCode(errorCode.getCode()).setMsg(errorCode.getMsg()).setSuccess(false);
    }

    public static R failWithFormate(IErrorCode errorCode, String reWrite) {
        return new R().setCode(errorCode.getCode()).setMsg(String.format(errorCode.getMsg(), reWrite)).setSuccess(false);
    }


}

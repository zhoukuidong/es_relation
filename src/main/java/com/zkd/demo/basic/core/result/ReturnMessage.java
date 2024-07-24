package com.zkd.demo.basic.core.result;

import com.zkd.demo.basic.core.exceptions.IErrorCode;

public enum ReturnMessage implements IErrorCode {
    SYSTEM_ERROR(101, "服务器开小差了..."),
    SQL_RUN_EXCEPTION(102, "SQL执行异常"),
    PARAM_EXCEPTION(103, "请求参数异常"),
    OUT_OF_MAX_FILE_SIZE(105, "文件大小超过限制"),
    METHOD_NOT_SUPPORT(104, "[%s]不支持[%s]方法"),
    PARAMS_NOT_EXISTS(106, "参数缺省"),
    REQUEST_JSON_ERROR(108, "请求JSON参数格式不正确，请检查参数格式"),
    PARAM_FORMATE_EXCEPTION(109, "请求参数异常: %s"),


    NULL_POINTER_EXCEPTION(201, "服务发送空指针异常，异常信息为："),
    SOCKET_TIMEOUT_EXCEPTION(202, "请求客户端资源连接超时，异常信息为："),

    DUPLICATE_KEY_ERROR(110, "SQL执行异常, 主键或唯一索引重复: %s"),
    DATA_INTEGRITY_VIOLATION_ERROR(111, "SQL执行异常, 违反数据库完整性约束；如字段非空约束、字段长度超长等异常。"),

    ;


    ReturnMessage(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Integer code;

    private String msg;

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}

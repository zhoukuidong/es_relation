package com.zkd.demo.dict.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeleteEnum {

    IS_DELETE(0, "未删除"),
    NOT_DELETE(1, "已删除");

    private int code;
    private String description;
}

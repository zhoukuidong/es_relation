package com.zkd.demo.datasource.entity.enums;

import lombok.Getter;


@Getter
public enum DeleteEnum {

    /**
     * Y-删除
     */
    Y("Y"),
    /**
     * N-未删除
     */
    N("N"),
    ;

    DeleteEnum(String key) {
        this.key = key;
    }

    private String key;

    /**
     * 重写用于valid
     */
    @Override
    public String toString() {
        return key;
    }
}

package com.zkd.demo.datasource.entity.enums;

import lombok.Getter;

@Getter
public enum SwitchEnum {

    /**
     * TRUE-TRUE
     */
    TRUE("true"),
    /**
     * FALSE-FALSE
     */
    FALSE("false"),
    ;

    SwitchEnum(String key) {
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

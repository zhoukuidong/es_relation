package com.zkd.demo.datasource.entity.enums;
import com.zkd.demo.basic.generator.ModulesEnum;
import lombok.Getter;

@Getter
public enum NumberGeneratorEnum implements ModulesEnum {

    /**
     * DS-datasource
     */
    DS("DS_"),
    ;

    NumberGeneratorEnum(String key) {
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

    @Override
    public String getPrefix() {
        return key;
    }

    @Override
    public String getCode() {
        return null;
    }
}

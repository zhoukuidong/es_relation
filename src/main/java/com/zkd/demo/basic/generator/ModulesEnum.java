package com.zkd.demo.basic.generator;

public interface ModulesEnum {

    /**
     * 编号前缀 例如：DW
     */
    public String getPrefix();

    /**
     * 编号后紧跟的编码，如DW100
     */
    public String getCode();


}

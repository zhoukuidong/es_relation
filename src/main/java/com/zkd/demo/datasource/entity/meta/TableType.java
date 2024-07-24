package com.zkd.demo.datasource.entity.meta;

public enum TableType {
	TABLE("TABLE"),
	VIEW("VIEW"),
	SYSTEM_TABLE ("SYSTEM TABLE"),
	GLOBAL_TEMPORARY("GLOBAL TEMPORARY"),
	LOCAL_TEMPORARY("LOCAL TEMPORARY"),
	ALIAS("ALIAS"),
	SYNONYM("SYNONYM");
	
	private String value;
	
	/**
	 * 构造
	 * @param value 值
	 */
	TableType(String value){
		this.value = value;
	}
	/**
	 * 获取值
	 * @return 值
	 */
	public String value(){
		return this.value;
	}
	
	@Override
	public String toString() {
		return this.value();
	}
}

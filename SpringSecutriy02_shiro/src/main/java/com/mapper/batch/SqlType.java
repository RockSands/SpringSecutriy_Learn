package com.mapper.batch;

public enum SqlType {
	INSERT("insert"), UPDATE("update"), DELETE("delete");

	private final String sqlType;

	SqlType(String sqlType) {
		this.sqlType = sqlType;
	}

	public String getSqlType() {
		return sqlType;
	}
	
}

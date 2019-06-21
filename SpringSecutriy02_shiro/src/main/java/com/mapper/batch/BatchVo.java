package com.mapper.batch;

public class BatchVo {
	private SqlType sqlType;
	
	/**
	 * 完全的Key,包括nameSpaces.对象.方法
	 */
	private String sqlKey;
	
	private Object obj;
	
	public BatchVo() {
		super();
	}

	public BatchVo(SqlType sqlType, String sqlKey, Object obj) {
		super();
		this.sqlType = sqlType;
		this.sqlKey = sqlKey;
		this.obj = obj;
	}

	public SqlType getSqlType() {
		return sqlType;
	}

	public void setSqlType(SqlType sqlType) {
		this.sqlType = sqlType;
	}

	public String getSqlKey() {
		return sqlKey;
	}

	public void setSqlKey(String sqlKey) {
		this.sqlKey = sqlKey;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

}

package com.example.common.sql.types;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.example.common.sql.SqlName;
import com.example.common.sql.SqlType;

/**
 * SQL名型
 * (スキーマ名、テーブル名等で使用)
 */
public class SqlNameType extends SqlType<SqlName> {
	public String toConstant(SqlName value, Map<String,String> hint) {
		return value.toString();
	}
	public SqlName getValue(ResultSet rs, String columnName)
	throws SQLException {
		throw new RuntimeException("not implemented");
	}
}

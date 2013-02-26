package com.example.common.sql.types;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.example.common.sql.SqlType;

/**
 * Long型
 */
public class LongType extends SqlType<Long> {
	public String toConstant(Long value, Map<String,String> hint) {
		return (value == null)? NULL : 
			value.toString();
	}
	public Long getValue(ResultSet rs, String columnName)
	throws SQLException {
		return rs.getLong(columnName);
	}
}
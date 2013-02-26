package com.example.common.sql.types;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.example.common.sql.SqlType;

/**
 * 整数型
 */
public class IntegerType extends SqlType<Integer> {
	public String toConstant(Integer value, Map<String,String> hint) {
		return (value == null)? NULL : 
			value.toString();
	}
	public Integer getValue(ResultSet rs, String columnName)
	throws SQLException {
		return rs.getInt(columnName);
	}
}

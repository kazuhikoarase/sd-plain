package com.example.common.sql.types;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.example.common.sql.SqlType;

/**
 * Double型
 */
public class DoubleType extends SqlType<Double> {
	public String toConstant(Double value, Map<String,String> hint) {
		return (value == null)? NULL : 
			value.toString();
	}
	public Double getValue(ResultSet rs, String columnName)
	throws SQLException {
		return rs.getDouble(columnName);
	}
}
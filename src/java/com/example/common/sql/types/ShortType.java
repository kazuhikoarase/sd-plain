package com.example.common.sql.types;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.example.common.sql.SqlType;

/**
 * Short型
 */
public class ShortType extends SqlType<Short> {
	public String toConstant(Short value, Map<String,String> hint) {
		return (value == null)? NULL : 
			value.toString();
	}
	public Short getValue(ResultSet rs, String columnName)
	throws SQLException {
		return rs.getShort(columnName);
	}
}

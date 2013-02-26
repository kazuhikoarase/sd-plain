package com.example.common.sql.types;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.example.common.sql.SqlType;

/**
 * BigDecimal型
 */
public class BigDecimalType extends SqlType<BigDecimal> {
	public String toConstant(BigDecimal value, Map<String,String> hint) {
		return (value == null)? NULL : 
			value.toString();
	}
	public BigDecimal getValue(ResultSet rs, String columnName)
	throws SQLException {
		return rs.getBigDecimal(columnName);
	}
}
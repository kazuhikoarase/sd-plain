package com.example.common.sql.types;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.example.common.sql.SqlType;

/**
 * Timestamp型
 */
public class TimestampType extends SqlType<Timestamp> {
	public String toConstant(Timestamp value, Map<String,String> hint) {
		return (value == null)? NULL : 
			String.format("'%s'",
				new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSSSSS").
					format(value) );
	}
	public Timestamp getValue(ResultSet rs, String columnName)
	throws SQLException {
		return rs.getTimestamp(columnName);
	}
}
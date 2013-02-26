package com.example.common.sql.types;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.example.common.sql.SqlType;

/**
 * ByteArrayType型
 */
public class ByteArrayType extends SqlType<byte[]> {
	public String toConstant(byte[] value, Map<String,String> hint) {
		if (value == null) {
			return NULL;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("x'");
		for (byte b : value) {
			sb.append(String.format("%02x", b) );
		}
		sb.append("'");
		return sb.toString();
	}
	public byte[] getValue(ResultSet rs, String columnName)
	throws SQLException {
		return rs.getBytes(columnName);
	}
}

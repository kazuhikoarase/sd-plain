package com.example.common.sql;

/**
 * TableRow
 * @author Kazuhiko Arase
 */
public interface TableRow {
	boolean isSet(String name);
	void setValue(String name, Object value);
	Object getValue(String name);
}
package com.example.common.sql;

import java.sql.SQLException;

/**
 * RowAccessException
 * @author Kazuhiko Arase
 */
@SuppressWarnings("serial")
public class RowAccessException extends SQLException {
	public RowAccessException(String message) {
		super(message);
	}
}
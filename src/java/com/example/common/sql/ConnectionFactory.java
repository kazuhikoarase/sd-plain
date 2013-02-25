package com.example.common.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ConnectionFactory
 * @author Kazuhiko Arase
 */
public interface ConnectionFactory {
	Connection create() throws SQLException;
}
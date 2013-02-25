package com.example.common.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ConnectionHolder
 * @author Kazuhiko Arase
 */
public class ConnectionHolder {
	
	private Connection conn = null;
	
	private ConnectionFactory factory;
	
	public ConnectionHolder(ConnectionFactory factory) {
		this.factory = factory;
	}
	
	public Connection getConnection()
	throws SQLException {
		if (conn == null) {
			conn = factory.create();
		}
		return conn;
	}

	public Connection peekConnection() {
		return conn;
	}
}
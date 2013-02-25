package com.example.common.sql;

import java.sql.Connection;
import java.sql.SQLException;

import com.example.common.Constants;

/**
 * Transaction
 * @author Kazuhiko Arase
 */
public class Transaction {

	public static final Transaction DEFAULT = new Transaction(new ConnectionFactory() {
		@Override
		public Connection create() throws SQLException {
			return DataSource.getConnection(Constants.DATASOURCE_NAME_APP);
		}
	} );
	
	public static final Transaction SECOND = new Transaction(new ConnectionFactory() {
		@Override
		public Connection create() throws SQLException {
			return DataSource.getConnection(Constants.DATASOURCE_NAME_SECOND);
		}
	} );

	private ThreadLocal<ConnectionHolder> holders = new ThreadLocal<ConnectionHolder>();
	
	private ConnectionFactory factory;
	
	private Transaction(ConnectionFactory factory) {
		this.factory = factory;
	}
	
	public void begin() throws SQLException {
		if (holders.get() != null) {
			throw new IllegalStateException("already in transaction.");
		}
		holders.set(new ConnectionHolder(factory) );
	}
	
	public void end() throws SQLException {
		ConnectionHolder holder = holders.get();
		if (holder == null) {
			throw new IllegalStateException("not in transaction.");
		}
		holders.remove();
		Connection conn = holder.peekConnection();
		if (conn != null) {
			conn.rollback();
			conn.close();
		}
	}
	
	public Connection getConnection() throws SQLException {
		ConnectionHolder holder = holders.get();
		if (holder == null) {
			throw new IllegalStateException("not in transaction.");
		}
		return holder.getConnection();
	}
}

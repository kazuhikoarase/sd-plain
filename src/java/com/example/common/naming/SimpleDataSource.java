package com.example.common.naming;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.example.common.logging.LogFactory;

/**
 * SimpleDataSource
 * @author Kazuhiko Arase
 */
public class SimpleDataSource implements DataSource {

	private List<Connection> pooledConnection;
	
	private String driverClassName;
	private String url;
	private String user;
	private String password;
	
	private PrintWriter logWriter;

	private int loginTimeout;
	
	public SimpleDataSource(String driverClassName, String url, String user, String password) throws Exception {

		this.driverClassName = driverClassName;
		this.url = url;
		this.user = user;
		this.password = password;
		initialize();
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new SQLException("not implemented.");
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException("not implemented.");
	}

	private void initialize() throws Exception {
		Class.forName(driverClassName);
		pooledConnection = new ArrayList<Connection>();
		// プログラム終了時にプールされている接続を閉じる。
		Runtime.getRuntime().addShutdownHook(new ConnectionDisposer() );
	}
	
	private void createConnection() throws SQLException {
		LogFactory.getLog(SimpleDataSource.class).debug("create connection.");
		pooledConnection.add(DriverManager.getConnection(url, user, password) );
	}
	
	public Connection getConnection() throws SQLException {

		LogFactory.getLog(SimpleDataSource.class).debug("connection requested.");

		synchronized(pooledConnection) {
			
			if (pooledConnection.isEmpty() ) {
				// 新しくコネクションを作る
				createConnection();
			}

			// 末尾のコネクションを取り出す。
			Connection conn = pooledConnection.remove(pooledConnection.size() - 1);
			
			return (Connection)Proxy.newProxyInstance(
					getClass().getClassLoader(),
					new Class[]{Connection.class},
					new ConnectionHandler(conn) );
		}
	}

	public Connection getConnection(String user, String password) throws SQLException {
		throw new SQLException("not implemented");
	}

	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}
	
	public void setLoginTimeout(int loginTimeout) throws SQLException {
		this.loginTimeout = loginTimeout;
	}

	public PrintWriter getLogWriter() throws SQLException {
		return logWriter;
	}
	
	public void setLogWriter(PrintWriter logWriter) throws SQLException {
		this.logWriter = logWriter;
	}

	private class ConnectionHandler implements InvocationHandler {
		
		private Connection conn;
		
		public ConnectionHandler(Connection conn) {
			this.conn = conn;
		}
		
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

			if (method.getName().equals("close") && method.getParameterTypes().length == 0) {

				// close処理。
				// コネクションを閉じずにプールに戻す。
				if (conn != null) {
					LogFactory.getLog(SimpleDataSource.class).debug("connection returned.");
					pooledConnection.add(conn);
					conn = null;
				}				
				return null;
			}
			return method.invoke(conn, args);
		}		
	}
	
	private class ConnectionDisposer extends Thread {

		@Override
		public void run() {
			for (Connection conn : pooledConnection) {
				try {
					LogFactory.getLog(SimpleDataSource.class).debug("close pooled connection.");
					conn.close();
				} catch(Exception e) {
				}
			}
		}
	}

}
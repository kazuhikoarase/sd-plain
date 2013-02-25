package com.example.common.sql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;

import com.example.common.logging.Log;
import com.example.common.logging.LogFactory;
import com.example.common.sql.logging.ISqlLogHandler;
import com.example.common.sql.logging.LogConnection;

/**
 * データソース.
 * @author Kazuhiko Arase
 */
class DataSource {
	
	private static final Log log = LogFactory.getLog("com.example.common.sql");
    
	
	/**
     * データベース接続を取得する。
     */
    public static Connection getConnection(String dataSourceName)
    throws SQLException {
    
        long waitTimeMillis = 100;

        for (int i = 0; i < 10; i++) {
        
        	try {
                return getConnectionImpl(dataSourceName);
        	} catch(SQLException e) {
        		log.error(e.getMessage(), e);
        	}
        	
        	try {
        		log.info(String.format("%dミリ秒後にリトライします。", waitTimeMillis) );
	    		Thread.sleep(waitTimeMillis);
	        	waitTimeMillis *= 2;
        	} catch(InterruptedException e) {
        		throw new RuntimeException(e);
        	}
        }

        // リトライオーバー
        throw new RuntimeException("connection not established.");
    }

    /**
     * データベース接続を取得する。
     */
    public static Connection getConnectionImpl(final String dataSourceName)
    throws SQLException {
    	
        Connection conn = getDataSource(dataSourceName).getConnection();

        conn.setAutoCommit(false);
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        execStatements(conn, DataSourceConfig.get(dataSourceName).
        		getPreExecStatements().split(";") );
        
        conn = ConnectionWrapper.newProxy(conn, new ICloseHandler() {
			@Override
			public void handle(Connection conn) throws SQLException {
		        execStatements(conn, DataSourceConfig.get(dataSourceName).
		        		getPostExecStatements().split(";") );
			}
		} );

        conn = LogConnection.newProxy(conn, new CustomLog(dataSourceName) );
        
        return conn;
    }

    /**
     * ステートメントの実行
     * @param conn データベース接続
     * @throws java.sql.SQLException
     */
    private static void execStatements(Connection conn, String[] statements) 
    throws SQLException {
    	
    	for (String sql : statements) {
    		sql = SqlUtil.trim(sql);
    		if (sql.length() == 0) {
    			continue;
    		}
    		Statement stmt = conn.createStatement();
    		try {
    			log.debug("=> [" + sql + "]");
    			stmt.execute(sql);
    			log.debug("<= " + sql);
/*    		} catch(SQLException e) {

    			if (e.getErrorCode() == -402) {
    				// [PWS0011] 文字変換中にエラーが起こった。
    				// の例外は無視
    			} else {
        			log.debug(e.getMessage() );
    				throw e;
    			}
*/    			
    		} finally {
    			stmt.close();
    		}
    	}
    }
    
    /**
     * データソースのインスタンス。
     */
    private static Map<String,javax.sql.DataSource> dataSourceMap = new HashMap<String, javax.sql.DataSource>();

    /**
     * データソースのインスタンスを取得する。
     */
    private static synchronized javax.sql.DataSource getDataSource(String dataSourceName) {
    	try {
    		
    		javax.sql.DataSource dataSource = dataSourceMap.get(dataSourceName);
    		
	        if (dataSource == null) {

	            InitialContext initCtx = new InitialContext();
	            Context envCtx = (Context)initCtx.lookup("java:comp/env");
	            dataSource = (javax.sql.DataSource)envCtx.lookup(dataSourceName);
	            
	            dataSourceMap.put(dataSourceName, dataSource);
	        }
	        
	        return dataSource;

    	} catch(Exception e) {
    		throw new RuntimeException(e);
    	}
    }

    private static class CustomLog implements ISqlLogHandler {
    	private String dataSourceName;
    	public CustomLog(String dataSourceName) {
    		this.dataSourceName = dataSourceName;
    	}
        public void log(String method, String info) {
        	log.debug(String.format("%s - %s - %s", dataSourceName, method, info) );
        }
    }
    
    /**
     * 接続を閉じるときの前処理
     */
    private static interface ICloseHandler {
    	void handle(Connection conn) throws SQLException;
    }

    private static class ConnectionWrapper 
    implements InvocationHandler {

        /**
         * 新しいプロクシのインスタンスを生成する。
         */
        public static Connection newProxy(Connection conn, ICloseHandler closeHandler) {
            return (Connection)Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[] { Connection.class },
                new ConnectionWrapper(conn, closeHandler) );
        }

        private Connection connection;
        private ICloseHandler closeHandler;
        
        private ConnectionWrapper(Connection connection, ICloseHandler closeHandler) {
            this.connection = connection;
            this.closeHandler = closeHandler;
        }

        /**
         * メソッドを実行する。
         */
        public Object invoke(Object proxy, Method method, Object[] args)  
        throws Throwable {

        	if (method.getName().equals("close") && 
	                method.getParameterTypes().length == 0) {
        		closeHandler.handle(connection);
            }
        	
            try {
                return method.invoke(connection, args);
            } catch(InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }
}


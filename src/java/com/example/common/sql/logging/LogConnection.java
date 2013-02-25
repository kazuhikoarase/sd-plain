package com.example.common.sql.logging;

import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Connection のログ出力用プロクシ.
 * @author Kazuhiko Arase
 */
public class LogConnection implements InvocationHandler {

    /**
     * 新しいプロクシのインスタンスを生成する。
     */
    public static Connection newProxy(Connection conn, ISqlLogHandler logHandler) {
        return (Connection)Proxy.newProxyInstance(
            Connection.class.getClassLoader(),
            new Class[] { Connection.class },
            new LogConnection(conn, logHandler) );
    }

    private ISqlLogHandler logHandler;
    private Connection connection;

    private long openTime;

    private LogConnection(Connection connection, ISqlLogHandler logHandler) {

        this.connection = connection;
        this.logHandler = logHandler;

        String tranName = getTransactionIsolationName(connection);

        // 開始ログ
        log("open", String.format("transaction isolation level:%s", tranName) );

        // 開始時間設定
        openTime = System.currentTimeMillis();
    }

    private String getTransactionIsolationName(
    	Connection connection
    ) {
        try {
        	int tran = connection.getTransactionIsolation();
	        switch(tran) {
	        case Connection.TRANSACTION_NONE :
	        	return "NONE";
	        case Connection.TRANSACTION_READ_COMMITTED :
	        	return "READ_COMMITTED";
	        case Connection.TRANSACTION_READ_UNCOMMITTED :
	        	return "READ_UNCOMMITTED";
	        case Connection.TRANSACTION_REPEATABLE_READ :
	        	return "REPEATABLE_READ";
	        case Connection.TRANSACTION_SERIALIZABLE :
	        	return "SERIALIZABLE";
	        default :
	        	return "UNDEFINED(" + tran + ")";
	        }
        } catch(SQLException e) {
        	throw new RuntimeException(e);
        }
    }
    
    /**
     * メソッドを実行する。
     */
    public Object invoke(Object proxy, Method method, Object[] args)  
    throws Throwable {

        if (method.getName().equals("commit") && 
                method.getParameterTypes().length == 0) {

            // commit (引数なし)
            log(method.getName(), "");

        } else if (method.getName().equals("rollback") && 
                method.getParameterTypes().length == 0) {

            // rollback (引数なし)
            log(method.getName(), "");

        } else if (method.getName().equals("close") && 
                method.getParameterTypes().length == 0) {
            
            // close (引数なし)
            // 経過時間と共に出力
        	long closeTime = System.currentTimeMillis();
            log(method.getName(), (closeTime - openTime) + "ms" );
        }

        try {

            if (method.getName().equals("createStatement") && 
                    method.getParameterTypes().length == 0) {
                // 引数なしの createStatement
                // Statement の Proxy を返す。
                Statement stmt =  (Statement)method.invoke(connection, args);
                return LogStatement.newProxy(stmt, logHandler);
            } else if (method.getName().equals("prepareStatement") && 
                    method.getParameterTypes().length == 1) {

            	// 引数1の prepareStatement
                // PreparedStatement の Proxy を返す。
                String sql = (String)args[0];
            	PreparedStatement stmt =  (PreparedStatement)method.invoke(connection, args);
                return LogPreparedStatement.newProxy(stmt, sql, logHandler);
            }

            // メソッド実体呼出
            return method.invoke(connection, args);

        } catch(InvocationTargetException e) {

            // 例外のログを取る。
            log("exception", e.getTargetException().getMessage() );

            throw e.getTargetException();
        }
    }

    private void log(String method, String info) {
        if (logHandler != null) {
            try {
                logHandler.log(method, info);
            } catch(Exception logException) {}// ログ出力時のエラーは無視。
        }
    }

}

package com.example.common.sql.logging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;


/**
 * PreparedStatement のログ出力用プロクシ.
 * @author Kazuhiko Arase
 */
public class LogPreparedStatement implements InvocationHandler {

    /**
     * 新しいプロクシのインスタンスを生成する。
     */
    public static PreparedStatement newProxy(PreparedStatement statement, String sql, ISqlLogHandler logHandler) {
        return (PreparedStatement)Proxy.newProxyInstance(
        		PreparedStatement.class.getClassLoader(),
            new Class[] { PreparedStatement.class },
            new LogPreparedStatement(statement, sql, logHandler) );
    }
    
    private static final Object NULL = new Object();
    
    private PreparedStatement statement;
    private String sql;
    private ISqlLogHandler logHandler;
    private Object[] params;
    
    private LogPreparedStatement(PreparedStatement statement, String sql, ISqlLogHandler logHandler) {
        this.statement = statement;
        this.sql = sql;
        this.logHandler = logHandler;
        initParams();
    }

    private void initParams() {
    	try {
	    	params = new Object[statement.getParameterMetaData().getParameterCount()];
    	} catch(SQLException e) {
    		throw new RuntimeException(e);
    	}
    }
    
    private String getLogMessage() throws Exception {
    	
    	StringBuilder buf = new StringBuilder();

    	buf.append(sql);
    	buf.append("; PARAMS(");

    	ParameterMetaData meta = statement.getParameterMetaData();
    	
    	for (int i = 0; i < meta.getParameterCount(); i++) {

    		Object param = params[i];
    		
    		if (i > 0) {
    			buf.append(",");
    		}
    		
    		if (param == null) {
    			buf.append('?');
    		} else if (param == NULL) {
    			buf.append("null");
    		} else if (param instanceof String || param instanceof Date) {
    			buf.append('\'');
    			buf.append(param);
    			buf.append('\'');
    		} else {
    			buf.append(param);
    		}
    	}
    	
    	buf.append(')');
    	
    	return buf.toString();
    }
    
    /**
     * メソッドを実行する。
     */
    public  Object invoke(Object proxy, Method method, Object[] args)  
    throws Throwable {

        if (method.getName().equals("execute") && method.getParameterTypes().length == 0) {
            // execute (引数無し)
            log(method.getName(), getLogMessage() );
        } else if (method.getName().equals("executeUpdate") && method.getParameterTypes().length == 0) {
            // execute update (引数無し)
            log(method.getName(), getLogMessage() );
        } else if (method.getName().equals("executeQuery") && method.getParameterTypes().length == 0) {
            // execute query (引数無し)
            log(method.getName(), getLogMessage() );
        } else if (method.getName().startsWith("set") && method.getParameterTypes().length == 2
        			&& method.getParameterTypes()[0].equals(Integer.TYPE) ) {
        	int index = ((Integer)args[0]).intValue() - 1;
        	if (method.getName().equals("setNull") ) {
            	params[index] = NULL;
        	} else {
            	params[index] = args[1];
        	}

        } else if (method.getName().equals("clearParameters") && method.getParameterTypes().length == 0) {
        	initParams();
        } 

        try {

            // メソッド実体呼出
            return method.invoke(statement, args);

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

package com.example.common.sql.logging;

import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

import java.sql.Statement;


/**
 * Statement のログ出力用プロクシ.
 * @author Kazuhiko Arase
 */
public class LogStatement implements InvocationHandler {

    /**
     * 新しいプロクシのインスタンスを生成する。
     */
    public static Statement newProxy(Statement statement, ISqlLogHandler logHandler) {
        return (Statement)Proxy.newProxyInstance(
            Statement.class.getClassLoader(),
            new Class[] { Statement.class },
            new LogStatement(statement, logHandler) );
    }

    private ISqlLogHandler logHandler;
    private Statement statement;

    private LogStatement(Statement statement, ISqlLogHandler logHandler) {
        this.statement = statement;
        this.logHandler = logHandler;
    }

    /**
     * メソッドを実行する。
     */
    public  Object invoke(Object proxy, Method method, Object[] args)  
    throws Throwable {

        if (method.getName().equals("execute") && method.getParameterTypes().length == 1) {
            // execute (引数1)
            log(method.getName(), String.valueOf(args[0]) );
        } else if (method.getName().equals("executeUpdate") && method.getParameterTypes().length == 1) {
            // execute update (引数1)
            log(method.getName(), String.valueOf(args[0]) );
        } else if (method.getName().equals("executeQuery") && method.getParameterTypes().length == 1) {
            // execute query (引数1)
            log(method.getName(), String.valueOf(args[0]) );
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

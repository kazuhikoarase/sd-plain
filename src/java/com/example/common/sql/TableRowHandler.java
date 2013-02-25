package com.example.common.sql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * TableRowHandler
 * @author Kazuhiko Arase
 */
public class TableRowHandler
implements InvocationHandler, TableRow {

	private final Map<String,Object> props = new HashMap<String, Object>();

	public TableRowHandler() {
	}

	public Object invoke(Object proxy, Method method, Object[] args)
	throws Throwable {
		if (SqlUtil.isSetter(method) ) {
			setValue(SqlUtil.getPropertyNameBySetter(method), args[0]);
			return null;
		} else if (SqlUtil.isGetter(method) ) {
			return getValue(SqlUtil.getPropertyNameByGetter(method) );
		}
		return method.invoke(this, args);
	}
	
	public Object getValue(String name) {
		if (!isSet(name) ) {
			// 設定されていないプロパティへのアクセス
			throw new IllegalStateException(
					String.format("property '%s' is not set.", name) );
		}
		return props.get(name);
	}
	
	public boolean isSet(String name) {
		return props.containsKey(name);
	}
	
	public void setValue(String name, Object value) {
		props.put(name, value);
	}
	
	public String toString() {
		return props.toString();
	}
}
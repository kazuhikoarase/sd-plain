package com.example.common.sql;

import java.lang.reflect.Proxy;

/**
 * TableRowFactory
 * @author Kazuhiko Arase
 */
public class TableRowFactory {

	private TableRowFactory() {
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> c) {
		try {
			return (T)Proxy.newProxyInstance(
				c.getClassLoader(), new Class[]{c},
				new TableRowHandler() );
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}

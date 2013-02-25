package com.example.common.logging;

/**
 * LogFactory
 * @author Kazuhiko Arase
 */
public class LogFactory {

	private LogFactory() {
	}

	public static Log getLog(String s) {
		return new JULLog(s);
	}

	public static Log getLog(Class<?> c) {
		return getLog(c.getName() );
	}
}

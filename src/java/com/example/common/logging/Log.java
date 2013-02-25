package com.example.common.logging;

/**
 * Log
 * @author Kazuhiko Arase
 */
public interface Log {	
	void info(Object msg);
	void debug(Object msg);
	void error(Object msg);
	void error(Object msg, Throwable t);
}
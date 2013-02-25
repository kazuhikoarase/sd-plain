package com.example.common.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JULLog
 * @author Kazuhiko Arase
 */
class JULLog implements Log {

	private Logger log;
	
	public JULLog(String name) {
		log = Logger.getLogger(name);
	}

	public void info(Object msg) {
		log.log(Level.INFO, LogUtil.format(msg) );
	}
	
	public void error(Object msg) {
		log.log(Level.SEVERE, LogUtil.format(msg) );
	}
	
	public void error(Object msg, Throwable t) {
		log.log(Level.SEVERE, LogUtil.format(msg), t);
	}
	
	public void debug(Object msg) {
		if (!LogUtil.enableDebugLog() ) return;
		log.log(Level.FINER, LogUtil.format(msg) );
	}
}
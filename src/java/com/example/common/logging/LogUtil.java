package com.example.common.logging;

import com.example.filter.SessionManager;


/**
 * LogUtil
 * @author Kazuhiko Arase
 */
class LogUtil {	
	
	private LogUtil() {
		
	}

	/**
	 * ログ出力するメッセージを整形する
	 * @param msg メッセージ
	 * @return 整形後のメッセージ
	 */
	public static String format(Object msg) {
		String sessionId = SessionManager.getInstance().getCurrentSession();
		if (sessionId == null) {
			sessionId = "{nosession}";
		}
		return String.format("%s %s", sessionId, String.valueOf(msg) );
	}

	/**
	 * デバッグログを出力するかどうか
	 * @return
	 */
	public static boolean enableDebugLog() {
		return true;
	}
}
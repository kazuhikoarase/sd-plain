package com.example.filter;


/**
 * セッションマネージャ
 * @author Kazuhiko Arase
 */
public class SessionManager {

	private static final SessionManager instance = new SessionManager();
	
	/**
	 * 本インスタンスを取得する
	 * @return インスタンス
	 */
	public static SessionManager getInstance() {
		return instance;
	}

	/**
	 * スレッドとセッションIDのマップ
	 */
	private final ThreadLocal<String> sessionMap = new ThreadLocal<String>();
	
	/**
	 * コンストラクタ
	 */
	private SessionManager() {
	}

	/**
	 * セッションを登録する
	 * @param id セッションID
	 */
	public void registerSession(String id) {
		sessionMap.set(id);
	}

	/**
	 * 現在のセッションIDを取得する。
	 * @return セッションID
	 * セッションが無い場合、 null となります。
	 */
	public String getCurrentSession() {
		return sessionMap.get();
	}

	/**
	 * セッションの登録を解除します。
	 */
	public void unregisterSession() {
		sessionMap.remove();
	}
}
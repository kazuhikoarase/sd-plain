package com.example.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 既定フィルタ
 * (キャッシュ無効化等)
 * @author Kazuhiko Arase 
 */
public class DefaultFilter extends FilterBase {

	private static final String HTTPS = "https";
	
	/**
	 * フィルタ
	 */
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		
		if (!HTTPS.equals(request.getScheme() ) ) {
			
			// キャッシュ無効化
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "no-cache");
			response.setIntHeader("Expires", 0);
			
		}

		request.setCharacterEncoding("Utf-8");

		SessionManager sm = SessionManager.getInstance();
		// セッション登録
		sm.registerSession(request.getSession(true).getId() );
		try {
			chain.doFilter(request, response);
		} finally {
			// セッション登録解除
			sm.unregisterSession();
		}
	}
}
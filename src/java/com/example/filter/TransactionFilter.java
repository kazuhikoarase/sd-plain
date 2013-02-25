package com.example.filter;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.example.common.sql.Transaction;

/**
 * トランザクションフィルタ
 * @author Kazuhiko Arase 
 */
public class TransactionFilter extends FilterBase {

	/**
	 * フィルタ
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	throws IOException, ServletException {

		try {
			// default transaction
			Transaction t1 = Transaction.DEFAULT;
			t1.begin();
			try {
				// second transaction
				Transaction t2 = Transaction.SECOND;
				t2.begin();
				try {
					chain.doFilter(request, response);
				} finally {
					t2.end();
				}
			} finally {
				t1.end();
			}
		} catch(SQLException e) {
			throw new ServletException(e);
		}
	}
}
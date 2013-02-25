package com.example.filter;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import com.example.common.logging.Log;
import com.example.common.logging.LogFactory;

/**
 * 基底フィルタ
 * @author Kazuhiko Arase 
 */
public abstract class FilterBase implements Filter {

	protected final Log log = LogFactory.getLog(getClass() );

	protected FilterConfig config;
	
	protected FilterBase() {
	}

	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}

	public void destroy() {
	}
}
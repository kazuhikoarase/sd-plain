package com.example.common;

import com.example.common.logging.Log;
import com.example.common.logging.LogFactory;
import com.example.common.sql.Sql;

public class TestCaseBase {

	protected final Log log = LogFactory.getLog(getClass() );

	static {
		Sql.setupBatchEnv();
	}
	
}
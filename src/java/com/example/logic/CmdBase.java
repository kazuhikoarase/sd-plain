package com.example.logic;

import com.example.common.logging.Log;
import com.example.common.logging.LogFactory;

/**
 * CmdBase
 * @author Kazuhiko Arase
 */
public abstract class CmdBase {

	protected final Log log = LogFactory.getLog(getClass() );
	
	protected CmdBase() {
	}

	public abstract void execute() throws Exception ;
}
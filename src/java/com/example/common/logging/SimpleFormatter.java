package com.example.common.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


public class SimpleFormatter extends Formatter{
	
	private static final String SEPARATOR = " - ";
	private static final String EOL = "\r\n";
	
	@Override
	public String format(LogRecord record) {

		StringBuilder sb = new StringBuilder();
	    sb.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS").format(new Date(record.getMillis() ) ) );
	    sb.append(SEPARATOR);
	    sb.append(record.getLevel() );
	    sb.append(SEPARATOR);
	    sb.append(record.getMessage() );
	    sb.append(EOL);
	    
	    try {
	        StringWriter sw = new StringWriter();
	        PrintWriter pw = new PrintWriter(sw);
	        try {
		        record.getThrown().printStackTrace(pw);
	        } finally {
		        pw.close();
	        }
		    sb.append(" - ");
			sb.append(sw.toString() );
	    } catch (Exception ex) {
	    }
	    
	    return sb.toString();
	}
}
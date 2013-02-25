package com.example.logic;

import java.math.BigDecimal;

import org.junit.Test;

import com.example.common.TestCaseBase;
import com.example.common.sql.Sql;

public class CmdX02Test extends TestCaseBase {

	@Test
	public void test1() throws Exception {

		Sql.transaction(new Sql.TransactionCommand() {
			
			@Override
			public void execute() throws Exception {
				CmdX02 cmdX02 = new CmdX02();
				cmdX02.setCol1("Z");
				cmdX02.setCol2(new BigDecimal(3) );
				cmdX02.execute();
				System.out.println(cmdX02.getHeader() );
			}
		} );
	}

	
}

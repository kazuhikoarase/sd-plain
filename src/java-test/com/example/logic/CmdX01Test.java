package com.example.logic;

import org.junit.Test;

import com.example.common.TestCaseBase;
import com.example.common.sql.Sql;

public class CmdX01Test extends TestCaseBase {

	@Test
	public void test1() throws Exception {
		Sql.transaction(new Sql.TransactionCommand() {
			
			@Override
			public void execute() throws Exception {
				CmdX01 cmdX01 = new CmdX01();
				cmdX01.execute();
				System.out.println(cmdX01.getMax() );

			}
		} );		
	}
}

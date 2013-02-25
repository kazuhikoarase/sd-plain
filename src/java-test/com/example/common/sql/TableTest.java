package com.example.common.sql;


import java.math.BigDecimal;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import com.example.common.TestCaseBase;
import com.example.db.table.Tx0001;


public class TableTest extends TestCaseBase {

	/**
	 * insert テスト
	 * @throws Exception 例外
	 */
	@Test 
	public void testIns() throws Exception {

		Sql.transaction(new Sql.TransactionCommand() {
			
			@Override
			public void execute() throws SQLException {

				// 1行登録
				Tx0001.Row row = Tx0001.newRow();
				row.setCol1("1");
				row.setCol2(BigDecimal.ZERO);
				row.setCol3(4);
				row.setCol4("a");
				Tx0001.insertRow(row);

				Sql.currentConnection().commit();

			}
		});
	}

	@Test 
	public void testSel() throws Exception {
		Sql.transaction(new Sql.TransactionCommand() {
			
			@Override
			public void execute() throws SQLException {

				// col1 = 1 の行を検索
				Tx0001.Row row = Tx0001.newRow();
				row.setCol1("1");
				
				boolean selected = Tx0001.selectRow(row);
				Assert.assertTrue(selected);
				Assert.assertEquals(row.getCol3(), Integer.valueOf(4) );
				Assert.assertEquals(row.getCol4(), "a");

				if (selected) {
					// 既存の場合 、 update
					Tx0001.updateRow(row);
				} else {
					// 未登録の場合、 insert
					Tx0001.insertRow(row);
				}
				
				//currentConnection().commit();
			}
		});
	}

	@Test 
	public void testUpd() throws Exception {
		Sql.transaction(new Sql.TransactionCommand() {
			
			@Override
			public void execute() throws SQLException {

				// col1 = 1 の行を更新
				Tx0001.Row row = Tx0001.newRow();
				row.setCol1("1");
				row.setCol3(5);
				row.setCol4("b");
				Tx0001.updateRow(row);

				Sql.currentConnection().commit();

			}
		});
	}

	@Test 
	public void testDel() throws Exception {
		Sql.transaction(new Sql.TransactionCommand() {
			
			@Override
			public void execute() throws SQLException {

				// col1 = 1 の行を削除
				Tx0001.Row row = Tx0001.newRow();
				row.setCol1("1");
				Tx0001.deleteRow(row);

				Sql.currentConnection().commit();

			}
		});
	}

	@Test 
	public void testDel2() throws Exception {
		Sql.transaction(new Sql.TransactionCommand() {
			
			@Override
			public void execute() throws SQLException {

				// update のサンプル(何も削除しない)
				Sql.currentConnection().update(new SqlString("delete from TX0001 where 1<>1") );

				Sql.currentConnection().commit();

			}
		});
	}
}

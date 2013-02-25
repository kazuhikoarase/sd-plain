package com.example.logic;

import com.example.common.sql.Sql;
import com.example.common.sql.SqlString;

/**
 * 共通処理サンプル1.
 * TX0001.COL3 の最大値を取得する。
 * @author Kazuhiko Arase
 */
public class CmdX01 extends CmdBase {

	private int max;
	
	public void execute() throws Exception {
		Integer max = Sql.currentConnection().selectFirstColumnOne(
				new SqlString("select max(COL3) from TX0001") );
		this.max = (max != null)? max.intValue() : 0;
	}
	
	public int getMax() {
		return max;
	}
}

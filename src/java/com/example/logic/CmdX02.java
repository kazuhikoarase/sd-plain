package com.example.logic;

import java.math.BigDecimal;
import java.util.List;

import com.example.common.sql.Sql;
import com.example.db.table.Tx0001;

/**
 * 共通処理サンプル2.
 * 結合と絞り込み検索を取得します。
 * @author Kazuhiko Arase
 */
public class CmdX02 extends SqlCmdBase {

	private String col1;
	private BigDecimal col2;

	private Tx0001.Row header;
	private List<MyData> list;
	
	/**
	 */
	public void execute() throws Exception {
		
		// COL1
		Tx0001.Row header = Tx0001.newRow();
		header.setCol1(getCol1() );
		Tx0001.selectRow(header);
		
/*// .pukee old style
		
		StringBuilder sql = new StringBuilder();
		sql.append("select T2.COL1, T2.COL2, T2.COL3, T2.COL4");
		sql.append(" from TX0001 T1,TX0002 T2");
		sql.append(" where T1.COL1 = T2.COL1");
		sql.append(" and T2.COL1='");
		sql.append(SqlUtil.escape(getCol1() ) );
		sql.append("'");

		if (getCol2() != null) {
			sql.append(" and T2.COL2='");
			sql.append(SqlUtil.escape(getCol2() ) );
			sql.append("'");
		}
		sql.append(" order by T2.COL1, T2.COL2");

		List<MyData> list = Sql.currentConnection().
			selectAll(MyData.class, sql.toString() );
*/		

		// buildSql() で自クラスと同名の SQL 外部ファイル CmdX02.sql を読み込みます。
		// buildSql("_a") のようにサフィックスを指定すると、
		// 外部ファイル CmdX02_a.sql を読み込みます。
		List<MyData> list = Sql.currentConnection().
			selectAll(MyData.class, buildSql() );

		setHeader(header);
		setList(list);
	}

	public String getCol1() {
		return col1;
	}

	public void setCol1(String col1) {
		this.col1 = col1;
	}

	public BigDecimal getCol2() {
		return col2;
	}

	public void setCol2(BigDecimal col2) {
		this.col2 = col2;
	}

	public Tx0001.Row getHeader() {
		return header;
	}

	public void setHeader(Tx0001.Row header) {
		this.header = header;
	}

	public List<MyData> getList() {
		return list;
	}

	public void setList(List<MyData> list) {
		this.list = list;
	}

	public static class MyData {

		private String col1;
		private BigDecimal col2;
		private Integer col3;
		private String col4;
		
		public String getCol1() {
			return col1;
		}
		public void setCol1(String col1) {
			this.col1 = col1;
		}
		public BigDecimal getCol2() {
			return col2;
		}
		public void setCol2(BigDecimal col2) {
			this.col2 = col2;
		}
		public Integer getCol3() {
			return col3;
		}
		public void setCol3(Integer col3) {
			this.col3 = col3;
		}
		public String getCol4() {
			return col4;
		}
		public void setCol4(String col4) {
			this.col4 = col4;
		}
	}
}

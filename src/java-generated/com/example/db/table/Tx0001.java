// 自動生成されたファイルです。 編集しないでください。

package com.example.db.table;

import com.example.common.sql.Table;
import com.example.common.sql.TableRow;

/**
 * テストテーブル1.
 * @author Apache Ant
 */
public class Tx0001 {

	/**
	 * テーブルクラスの唯一のインスタンス
	 */	
	private static final Tx0001Impl instance = new Tx0001Impl();

	/**
	 * テーブルクラスの唯一のインスタンスを取得する。
	 */
	private static Tx0001Impl getInstance() {
		return instance;
	}

	/**
	 * インスタンス化禁止
	 */
	private Tx0001() {
	}

	/**
	 * 検索条件を指定して検索を行う。
	 * @param whereClause 検索条件
	 * @return 検索結果の行データリスト
	 */
	public static java.util.List<Row> select(final com.example.common.sql.SqlString whereClause) throws java.sql.SQLException {
		return getInstance().select(whereClause);
	}

	/**
	 * 検索条件を指定して件数取得を行う。
	 * @param whereClause 検索条件
	 * @return 件数
	 */
	public static int selectCount(final com.example.common.sql.SqlString whereClause) throws java.sql.SQLException {
		return getInstance().selectCount(whereClause);
	}

	/**
	 * プライマリキーを指定して1行検索を行う。
	 * @param row プライマリキーを設定した行データ
	 * @return 検索ヒットした場合は true
	 */
	public static boolean selectRow(final Row row) throws java.sql.SQLException {
		return getInstance().selectRow(row);
	}	

	/**
	 * 1行登録を行う。
	 * @param row 登録内容を設定した行データ
	 */
	public static void insertRow(final Row row) throws java.sql.SQLException {
		getInstance().insertRow(row);
	}

	/**
	 * プライマリキーを指定して1行更新を行う。
	 * @param row プライマリキーを設定した行データ
	 */
	public static void updateRow(final Row row) throws java.sql.SQLException {
		getInstance().updateRow(row);
	}

	/**
	 * プライマリキーを指定して1行削除を行う。
	 * @param row プライマリキーを設定した行データ
	 */
	public static void deleteRow(final Row row) throws java.sql.SQLException {
		getInstance().deleteRow(row);
	}

	/**
	 * 新規行データを作成する。
	 */
	public static Row newRow() {
		return getInstance().newRow();
	}

	/**
     * テーブルクラスの実装
     */	
	private static class Tx0001Impl extends Table<Tx0001.Row> {

		/**
		 * コンストラクタ
		 */
		public Tx0001Impl() {
			super(Row.class);
		}

		/**
		 * 本テーブルの表メタデータを取得する。
		 */
		protected Table.TableMetaData getTableMetaData() {
			return new Table.TableMetaData("DB2ADMIN", "TX0001");
		}
	
		/**
		 * 本テーブルの列メタデータを取得する。
		 */
		protected Table.ColumnMetaData[] getColumnMetaData() {
		 	return new Table.ColumnMetaData[]{
				new Table.ColumnMetaData("COL1", "col1", java.lang.String.class, true),
				new Table.ColumnMetaData("COL2", "col2", java.math.BigDecimal.class, false),
				new Table.ColumnMetaData("COL3", "col3", java.lang.Integer.class, false),
				new Table.ColumnMetaData("COL4", "col4", java.lang.String.class, false)
			};
		}
	}	

	/**
	 * テストテーブル1 の行データ.
	 */
	public interface Row extends TableRow {
		/**
		 * テスト列1を設定する
		 * @param col1 テスト列1
		 */
		public void setCol1(java.lang.String col1);

		/**
		 * テスト列1を取得する
		 * @return テスト列1
		 */
		public java.lang.String getCol1();
	
		/**
		 * テスト列2を設定する
		 * @param col2 テスト列2
		 */
		public void setCol2(java.math.BigDecimal col2);

		/**
		 * テスト列2を取得する
		 * @return テスト列2
		 */
		public java.math.BigDecimal getCol2();
	
		/**
		 * テスト列3を設定する
		 * @param col3 テスト列3
		 */
		public void setCol3(java.lang.Integer col3);

		/**
		 * テスト列3を取得する
		 * @return テスト列3
		 */
		public java.lang.Integer getCol3();
	
		/**
		 * テスト列4を設定する
		 * @param col4 テスト列4
		 */
		public void setCol4(java.lang.String col4);

		/**
		 * テスト列4を取得する
		 * @return テスト列4
		 */
		public java.lang.String getCol4();
	
	}
}


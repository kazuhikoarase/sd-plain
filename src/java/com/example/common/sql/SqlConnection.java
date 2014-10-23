package com.example.common.sql;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SqlConnection
 * @author Kazuhiko Arase
 */
public class SqlConnection {

	/**
	 * 検索結果が見つからなかった際に発行されるエラーの SQLコード
	 */
	private static final int SQL0100N = -100;

	private final Connection conn;

	/**
	 * コンストラクタ
	 * @param conn JDBC接続
	 */
	public SqlConnection(final Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * 内包する JDBC接続を取得する。
	 * @throws SQLException
	 */
	public Connection getJdbcConnection() {
		return conn;
	}
	
	/**
	 * コミットする。
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		conn.commit();
	}

	/**
	 * ロールバックする。
	 * @throws SQLException
	 */
	public void rollback() throws SQLException {
		conn.rollback();
	}

	/**
	 * 先頭列を一件取得する。
	 * @param sql SQL文字列
	 * @return 検索結果が 0 件の場合 null, 1件の場合 該当列, それ以外の場合, 例外送出
	 * @throws SQLException
	 */
	public <T> T selectFirstColumnOne(
		final SqlString sql
	) throws SQLException {
		final List<T> list = selectFirstColumnAllImpl(2, sql);
		if (list.size() == 0) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new RowAccessException("more than 1 rows found.");
		}
	}

	/**
	 * 先頭列を全件取得する。
	 * @param sql SQL文字列
	 * @return 先頭列のリスト
	 * @throws SQLException
	 */
	public <T> List<T> selectFirstColumnAll(
		final SqlString sql
	) throws SQLException {
		return selectFirstColumnAllImpl(-1, sql);
	}

	/**
	 * 一件取得する。
	 * @param clazz 取得するレコードのクラス
	 * @param sql SQL文字列
	 * @return 検索結果が 0 件の場合 null, 1件の場合 該当レコード,  それ以外の場合, 例外送出
	 * @throws SQLException
	 */
	public <T> T selectOne(
		final Class<T> clazz,
		final SqlString sql
	) throws SQLException {
		return selectOne(
				new DefaultResultRecordFactory<T>(clazz),
				null, sql, clazz);
	}
	
	/**
	 * レコードを全件取得する。
	 * @param clazz 取得するレコードのクラス
	 * @param sql SQL文字列
	 * @return レコードのリスト
	 * @throws SQLException
	 */
	public <T> List<T> selectAll(
		final Class<T> clazz,
		final SqlString sql
	) throws SQLException {
		return selectAllImpl(
				new DefaultResultRecordFactory<T>(clazz), 
				null, -1, sql, clazz);
	}

	/**
	 * レコードを取得する。
	 * @param clazz 取得するレコードのクラス
	 * @param sql SQL文字列
	 * @param handler フェッチハンドラ
	 * @throws SQLException
	 */
	public <T> void select(
		final Class<T> clazz,
		final SqlString sql,
		final FetchHandler<T> handler
	) throws SQLException {
		selectImpl(
				new DefaultResultRecordFactory<T>(clazz),
				handler,
				null, -1, sql, clazz);
	}

	<T> T selectOne(
		final ResultRecordFactory<T> factory,
		final String[] columnNames,
		final SqlString sql,
		final Class<T> clazz
	) throws SQLException {
		final List<T> list = selectAllImpl(factory, columnNames, 2, sql, clazz);
		if (list.size() == 0) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new RowAccessException("more than 1 rows found.");
		}
	}
	
	<T> List<T> selectAll(
		final ResultRecordFactory<T> factory,
		final String[] columnNames,
		final SqlString sql,
		final Class<T> clazz
	) throws SQLException {
		return selectAllImpl(factory, columnNames, -1, sql, clazz);
	}

	private <T> List<T> selectAllImpl(
		final ResultRecordFactory<T> factory,
		final String[] columnNames,
		final int maxRows,
		final SqlString sql,
		final Class<T> clazz
	) throws SQLException {
		final List<T> list = new ArrayList<T>();
		selectImpl(
			factory,
			new FetchHandler<T>() {
				public boolean handle(T t) {
					list.add(t);
					return CONTINUE_FETCHING;
				};
			},
			columnNames,
			maxRows,
			sql,
			clazz
		);
		return list;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> selectFirstColumnAllImpl(
		final int maxRows,
		final SqlString sql
	) throws SQLException {
		final List<T> list = new ArrayList<T>();
		final Statement stmt = conn.createStatement();
		try {
			if (maxRows >= 0) {
				stmt.setMaxRows(maxRows);
			}
			final ResultSet rs = stmt.executeQuery(sql.toString() );
			try {
				while (rs.next() ) {
					list.add( (T)rs.getObject(1) );
				}
			} finally {
				rs.close();
			}
		} catch(SQLException e) {
			if (e.getErrorCode() != SQL0100N) {
				throw e;
			}
		} finally {
			stmt.close();
		}
		return list;
	}

	private <T> void selectImpl(
		final ResultRecordFactory<T> factory,
		final FetchHandler<T> handler,
		final String[] columnNames,
		final int maxRows,
		final SqlString sql,
		final Class<T> clazz
	) throws SQLException {
		final Statement stmt = conn.createStatement();
		try {
			if (maxRows >= 0) {
				stmt.setMaxRows(maxRows);
			}
			final ResultSet rs = stmt.executeQuery(sql.toString() );
			try {
				final String[] cols = (columnNames != null)?	
					columnNames : SqlUtil.getColumnNames(rs);
				final Map<String,Method> setters =
					SqlUtil.getSetters(cols, clazz);
				while (rs.next() ) {
					try {
						boolean fetching = handler.handle(SqlUtil.
							setTo(setters, rs, factory.create() ) );
						if (fetching == FetchHandler.FETCHING_COMPLETE) {
							// フェッチ終了
							break;
						}
					} catch(Exception e) {
						throw new RuntimeException(e);
					}
				}
			} finally {
				rs.close();
			}
		} catch(SQLException e) {
			if (e.getErrorCode() != SQL0100N) {
				throw e;
			}
		} finally {
			stmt.close();
		}
	}

	/**
	 * 更新する。
	 * @param sql SQL文字列
	 * @return 更新件数
	 * @throws SQLException
	 */
	public int update(final SqlString sql) throws SQLException {
		final Statement stmt = conn.createStatement();
		try {
			return stmt.executeUpdate(sql.toString() );
		} finally {
			stmt.close();
		}
	}

	/**
	 * レコードファクトリ
	 */
	public interface ResultRecordFactory<T> {
		T create() throws Exception;
	}

	/**
	 * フェッチハンドラ
	 */
	public interface FetchHandler<T> {
		
		/**
		 * フェッチ続行
		 */
		boolean CONTINUE_FETCHING = true;
		
		/**
		 * フェッチ終了
		 */
		boolean FETCHING_COMPLETE = false;

		/**
		 * 検索結果をハンドリングする
		 * @param resultRecord 検索結果
		 * @return 検索を続行するならば true
		 * 戻り値には FETCHING_COMPLETE 
		 * または CONTINUE_FETCHING を指定します。
		 */
		boolean handle(T resultRecord) throws Exception;
	}
	
	private static class DefaultResultRecordFactory<T>
	implements ResultRecordFactory<T> {
		
		private final Class<T> clazz;

		public DefaultResultRecordFactory(final Class<T> clazz) {
			this.clazz = clazz;
		}
		
		@Override
		public T create() throws Exception {
		    if (TableRow.class.isAssignableFrom(clazz) ) {
		        return TableRowFactory.create(clazz);
		    }
			return clazz.newInstance();
		}
	}
}
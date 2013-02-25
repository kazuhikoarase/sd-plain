package com.example.common.sql;

import java.util.Map;


/**
 * SQL文字列
 * @author Kazuhiko Arase
 */
public class SqlString {

	/**
	 * SQL
	 */
	private final String sql;

	/**
	 * コンストラクタ
	 * @param sql
	 */
	public SqlString(String sql) {
		this.sql = sql;
	}

	/**
	 * SQL文字列をフォーマットします。
	 * SQL文字列中の埋め込みは ${フォーマット引数の添え字} という形式で指定します。
	 * @param args フォーマット引数
	 * @return フォーマット後の SQL文字列
	 */
	public SqlString format(final Object... args) {
		
		String sql = new SqlFormatter(this.sql).format(
			new SqlFormatter.FormatHandler() {
				@Override
				public String getConstant(String propName, Map<String,String> hint) {
					if (!propName.matches("[0-9]+") ) {
						return null;
					}
					final int index = Integer.valueOf(propName);
					propName = String.format("args[%d]", index);
					return SqlType.toConstant(propName, args[index], hint);
				}
			} 
		);

		return new SqlString(sql);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SqlString) {
			return sql.equals( ( (SqlString)obj).sql);
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return sql.hashCode();
	}
	
	public String toString() {
		return sql;
	}
}
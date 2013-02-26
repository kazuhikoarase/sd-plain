package com.example.common.sql;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.example.common.sql.types.BigDecimalType;
import com.example.common.sql.types.ByteArrayType;
import com.example.common.sql.types.DoubleType;
import com.example.common.sql.types.IntegerType;
import com.example.common.sql.types.LongType;
import com.example.common.sql.types.ShortType;
import com.example.common.sql.types.SqlNameType;
import com.example.common.sql.types.StringType;
import com.example.common.sql.types.TimestampType;

/**
 * SqlType
 * @author Kazuhiko Arase
 */
public abstract class SqlType<T> {
	
	protected static final String NULL = "null";

	private static final Map<Class<?>,SqlType<?>> map;

	static {
		map = new HashMap<Class<?>,SqlType<?>>();
		map.put(String.class, new StringType() );
		map.put(SqlName.class, new SqlNameType() );
		map.put(Integer.class, new IntegerType() );
		map.put(Long.class, new LongType() );
		map.put(Short.class, new ShortType() );
		map.put(Double.class, new DoubleType() );
		map.put(BigDecimal.class, new BigDecimalType() );
		map.put(Timestamp.class, new TimestampType() );
		map.put(byte[].class, new ByteArrayType() );
	}

	/**
	 * Sql型を取得する。
	 * @param clazz Javaの型
	 * @param propName 対象プロパティ名
	 * @return Sql型
	 */
	@SuppressWarnings("unchecked")
	public static SqlType<Object> get(Class<?> clazz, String propName) {
		if (!map.containsKey(clazz) ) {
			throw new IllegalArgumentException(
					String.format("unsupported type %s for %s",
							clazz.getName(), propName) );
		}
		return (SqlType<Object>)map.get(clazz);
	}

	/**
	 * 値をSQL定数の文字列に変換する。
	 * @param propName 対象プロパティ名
	 * @param hint 変換時のヒント
	 * @param value 値
	 * @return SQL定数の文字列
	 */
	public static String toConstant(String propName, Object value, Map<String,String> hint) {
		if (value == null) {
			return NULL;
		} else if (value.getClass().isArray() ) {
			int length = Array.getLength(value);
			StringBuilder buf = new StringBuilder();
			buf.append('(');
			for (int i = 0; i < length; i++) {
				if (i > 0) {
					buf.append(',');
				}
			buf.append(toConstant(propName, Array.get(value, i), hint) );
			}
			buf.append(')');
			return buf.toString();
		} else if (value instanceof Collection<?>) {
			Collection<?> collection = (Collection<?>)value;
			int count = 0;
			StringBuilder buf = new StringBuilder();
			buf.append('(');
			for (Object item : collection) {
				if (count > 0) {
					buf.append(',');
				}
				buf.append(toConstant(propName, item, hint) );
				count++;
			}
			buf.append(')');
			return buf.toString();
		} else {
			return get(value.getClass(), propName).
				toConstant(value, hint);
		}
	}

	/**
	 * コンストラクタ
	 */
	protected SqlType() {
	}
	
	/**
	 * 値をSQL定数の文字列に変換する。
	 * @param value 値
	 * @return SQL定数の文字列
	 */
	public abstract String toConstant(T value, Map<String,String> hint);

	/**
	 * 結果セットから値を取得する。
	 * @param rs 結果セット 
	 * @param columnName 列名
	 * @return 値
	 * @throws SQLException SQL例外
	 */
	public abstract T getValue(ResultSet rs, String columnName) throws SQLException;
}
package com.example.common.sql;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * SqlType
 * @author Kazuhiko Arase
 */
public abstract class SqlType<T> {
	
	private static final String NULL = "null";

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
	 * 文字列型
	 */
	private static class StringType extends SqlType<String> {

		/**
		 * like 演算子のヒント
		 */
		private static final String HINT_LIKE = "like";
		/**
		 * like 演算子のヒント - 前方一致
		 */
		private static final String HINT_LIKE_START = "start";
		/**
		 * like 演算子のヒント - 後方一致
		 */
		private static final String HINT_LIKE_END = "end";
		/**
		 * like 演算子のヒント - 中間一致
		 */
		private static final String HINT_LIKE_MIDDLE = "middle";

	    /**
	     * 単一引用符
	     */
	    private final static char QUOT = '\'';

	    /**
	     * エスケープ文字。
	     */
	    private final static char ESCAPE_CHAR = '\'';
	    // for postgres
	    //private final static char ESCAPE_CHAR = '\\'; 
		
		public String toConstant(String value, Map<String,String> hint) {

			if (value == null) {
				return NULL;
			}
			char escapeChar = '\\';
			String like = (hint != null)? hint.get(HINT_LIKE) : null;
			if (like == null) {
				// 通常のエスケープ
				value = escape( (String)value);
			} else if (HINT_LIKE_START.equals(like) ) {
				value = String.format("%s%%", escapeLike( (String)value, escapeChar) );
			} else if (HINT_LIKE_END.equals(like) ) {
				value = String.format("%%%s", escapeLike( (String)value, escapeChar) );
			} else if (HINT_LIKE_MIDDLE.equals(like) ) {
				value = String.format("%%%s%%", escapeLike( (String)value, escapeChar) );
			} else {
				throw new IllegalArgumentException("bad hint for like:" + like);
			}
			return String.format("'%s'", value);
		}
		public String getValue(ResultSet rs, String columnName)
		throws SQLException {
			return SqlUtil.rtrim(rs.getString(columnName) );
		}

	    /**
	     * 文字列中の単一引用符(')とエスケープ文字をエスケープする。(定数エンコード)
	     */
	    public static String escape(String s) {
	        
	        if (s == null) {
	            return s;
	        }

	        StringBuilder buffer = new StringBuilder();

	        for (int i = 0; i < s.length(); i++) {
	            char c = s.charAt(i); 
	            if (c == QUOT || c == ESCAPE_CHAR) {
	                buffer.append(ESCAPE_CHAR);
	            }
	            buffer.append(c);
	        }

	        return buffer.toString();
	    }

	    /**
	     * LIKE述部をエスケープする。
	     * <br>SQL文では like ～ escape 'escapeChar' と指定します。
	     */
	    public static String escapeLike(String s, char escapeChar) {
	        
	        if (s == null) {
	            return s;
	        }

	        StringBuilder buffer = new StringBuilder();

	        for (int i = 0; i < s.length(); i++) {
	            
	            char c = s.charAt(i); 

	            if (c == '_' || c == '%' || c == escapeChar) {
	                buffer.append(escapeChar);
	            } else if (c == '＿' || c == '％') {
	                // DB2固有の仕様。
	                // (MBCSの下線とパーセント)
	                buffer.append(escapeChar);
	            }
	            
	            buffer.append(c);
	        }
	        
	        return escape(buffer.toString() );
	    }	
	}

	/**
	 * SQL名型
	 * (スキーマ名、テーブル名等で使用)
	 */
	public static class SqlNameType extends SqlType<SqlName> {
		public String toConstant(SqlName value, Map<String,String> hint) {
			return value.toString();
		}
		public SqlName getValue(ResultSet rs, String columnName)
		throws SQLException {
			throw new RuntimeException("not implemented");
		}
	}

	/**
	 * 整数型
	 */
	private static class IntegerType extends SqlType<Integer> {
		public String toConstant(Integer value, Map<String,String> hint) {
			return (value == null)? NULL : 
				value.toString();
		}
		public Integer getValue(ResultSet rs, String columnName)
		throws SQLException {
			return rs.getInt(columnName);
		}
	}

	/**
	 * BigDecimal型
	 */
	private static class BigDecimalType extends SqlType<BigDecimal> {
		public String toConstant(BigDecimal value, Map<String,String> hint) {
			return (value == null)? NULL : 
				value.toString();
		}
		public BigDecimal getValue(ResultSet rs, String columnName)
		throws SQLException {
			return rs.getBigDecimal(columnName);
		}
	}

	/**
	 * Long型
	 */
	private static class LongType extends SqlType<Long> {
		public String toConstant(Long value, Map<String,String> hint) {
			return (value == null)? NULL : 
				value.toString();
		}
		public Long getValue(ResultSet rs, String columnName)
		throws SQLException {
			return rs.getLong(columnName);
		}
	}

	/**
	 * Short型
	 */
	private static class ShortType extends SqlType<Short> {
		public String toConstant(Short value, Map<String,String> hint) {
			return (value == null)? NULL : 
				value.toString();
		}
		public Short getValue(ResultSet rs, String columnName)
		throws SQLException {
			return rs.getShort(columnName);
		}
	}

	/**
	 * Double型
	 */
	private static class DoubleType extends SqlType<Double> {
		public String toConstant(Double value, Map<String,String> hint) {
			return (value == null)? NULL : 
				value.toString();
		}
		public Double getValue(ResultSet rs, String columnName)
		throws SQLException {
			return rs.getDouble(columnName);
		}
	}

	/**
	 * Timestamp型
	 */
	private static class TimestampType extends SqlType<Timestamp> {
		public String toConstant(Timestamp value, Map<String,String> hint) {
			return (value == null)? NULL : 
				String.format("'%s'",
					new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSSSSS").
						format(value) );
		}
		public Timestamp getValue(ResultSet rs, String columnName)
		throws SQLException {
			return rs.getTimestamp(columnName);
		}
	}

	/**
	 * Timestamp型
	 */
	private static class ByteArrayType extends SqlType<byte[]> {
		public String toConstant(byte[] value, Map<String,String> hint) {
			if (value == null) {
				return NULL;
			}
			StringBuilder sb = new StringBuilder();
			sb.append("x'");
			for (byte b : value) {
				sb.append(String.format("%02x", b) );
			}
			sb.append("'");
			return sb.toString();
		}
		public byte[] getValue(ResultSet rs, String columnName)
		throws SQLException {
			return rs.getBytes(columnName);
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
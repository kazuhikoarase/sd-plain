package com.example.common.sql.types;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.example.common.sql.SqlType;
import com.example.common.sql.SqlUtil;

/**
 * 文字列型
 */
public class StringType extends SqlType<String> {

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
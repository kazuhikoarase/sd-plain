package com.example.common.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SqlFormatter
 * @author Kazuhiko Arase
 */
public class SqlFormatter {
	
	private final String sql;
	
	/**
	 * コンストラクタ
	 * @param sql SQL文字列
	 */
	public SqlFormatter(String sql) {
		this.sql = sql;
	}

	/**
	 * フォーマットハンドラ
	 */
	public interface FormatHandler {
		/**
		 * 定数文字列を取得する
		 * @param propName プロパティ名
		 * @param hint 定数化のヒント
		 * @return 定数文字列
		 */
		String getConstant(String propName, Map<String,String> hint);
	}

	/**
	 * フォーマットする
	 * @param handler フォーマットハンドラ
	 * @return フォーマット後のSQL文字列
	 */
	public String format(FormatHandler handler) {

		final StringBuilder sql = new StringBuilder();
		final SqlTokenizer st = new SqlTokenizer(this.sql);
		
		while (st.hasMoreTokens() ) {
		
			SqlTokenizer.Token token = st.nextToken();
			
			if (token.getType() == SqlTokenizer.Token.COMMAND_TEXT) {
			
				String text = token.getText();
				Pattern pat = Pattern.compile("\\$\\{([^\\{^\\}]+)\\}");
				Matcher mat = pat.matcher(text);
				int start = 0;
				
				while (mat.find(start) ) {
					sql.append(text.substring(start, mat.start() ) );
					String propName = mat.group(1);
					String sqlConst;
					int index = propName.indexOf(',');
					if (index != -1) {
						sqlConst = handler.getConstant(
							propName.substring(0, index).trim(),
							createHint(propName.substring(index + 1) ) );
					} else {
						sqlConst = handler.getConstant(propName, null);
					}
					if (sqlConst != null) {
						sql.append(sqlConst);
					} else {
						sql.append(mat.group() );
					}
					start = mat.end();
				}
				
				sql.append(text.substring(start) );

			} else if (token.getType() == SqlTokenizer.Token.STRING) {
				sql.append(token.getText() );
			}
		}
		
		return sql.toString();
	}
	
	public Map<String,String> createHint(String hintString) {
		Map<String,String> hint = new HashMap<String, String>();
		for (String keyValue : hintString.split(";") ) {
			int index = keyValue.indexOf('=');
			if (index != -1) {
				hint.put(keyValue.substring(0, index).trim(),
						keyValue.substring(index + 1).trim() );
			} else {
				hint.put(keyValue.trim(), keyValue.trim() );
			}
		}
		return hint;
	}
}
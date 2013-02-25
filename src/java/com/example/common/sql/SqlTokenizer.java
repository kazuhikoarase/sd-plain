package com.example.common.sql;

/**
 * SqlTokenizer
 * @author Kazuhiko Arase
 */
public class SqlTokenizer {

	private final String sql;
	private int start;

	/**
	 * コンストラクタ
	 * @param sql SQL文字列
	 */
	public SqlTokenizer(String sql) {
		this.sql = sql;
		this.start = 0;
	}

	/**
	 * 次のトークンが存在するかどうかを取得する
	 * @return
	 */
	public boolean hasMoreTokens() {
		return start < sql.length();
	}

	/**
	 * 次のトークン
	 * @return トークン
	 */
	public Token nextToken() {
		
		if (!hasMoreTokens() ) {
			throw new IllegalStateException("no tokens.");
		}
		
		if (startsWith("--", start) ) {
			return nextComment();
		} else if (startsWith("'", start) ) {
			return nextString();
		} else {
			return nextCommandText();
		}
	}

	private Token nextComment() {
		int index = start + 2;
		while (index < sql.length() ) {
			if (startsWith("\n", index) ) {
				index += 1;
				break;
			}
			index += 1;
		}
		Token token = new Token(Token.COMMENT, sql.substring(start, index) );
		start = index;
		return token;
	}
	
	private Token nextString() {
		int index = start + 1;
		while (index < sql.length() ) {
			if (startsWith("''", index) ) {
				index += 2;
				continue;
			} else if (startsWith("'", index) ) {
				index += 1;
				break;
			}
			index += 1;
		}
		Token token = new Token(Token.STRING, sql.substring(start, index) );
		start = index;
		return token;
	}

	private Token nextCommandText() {
		int index = start + 1;
		while (index < sql.length() ) {
			if (startsWith("--", index) || startsWith("'", index) ) {
				break;
			}
			index++;
		}
		Token token = new Token(Token.COMMAND_TEXT, sql.substring(start, index) );
		start = index;
		return token;
	}
	
	private boolean startsWith(String text, int index) {
		return (index + text.length() <= sql.length() )?
				sql.substring(index, index + text.length() ).equals(text) : false;
	}

	public static class Token {
		/**
		 * コマンドテキスト
		 */
		public static final int COMMAND_TEXT = 1;
		/**
		 * コメント
		 */
		public static final int COMMENT = 2;

		/**
		 * 単一引用符で括られた文字列
		 */
		public static final int STRING = 3;
		
		private final int type;
		private final String text;
		
		public Token(int type, String text) {
			this.type =type;
			this.text = text;
		}
		public int getType() {
			return type;
		}
		public String getText() {
			return text;
		}
	}
}
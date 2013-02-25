package com.example.common.sql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * SqlScript
 * @author Kazuhiko Arase
 */
public class SqlScript {
	
	/**
	 * SQL中のコメントの接頭子
	 */
	private static final String COMMENT_PREFIX = "--";
	
	/**
	 * SQL中のスクリプトコメントの接頭子
	 */
	private static final String SCRIPT_COMMENT_PREFIX = COMMENT_PREFIX + "!";

	/**
	 * ソースの文字コード
	 */
	private static final String SOURCE_ENCODING = "Utf-8";

	/**
	 * コンストラクタ
	 */
	public SqlScript() {
	}

	/**
	 * パスで指定されたリソースをスクリプトとして評価する。
	 * @param engine スクリプトエンジン
	 * @param path パス
	 * @exception ScriptException スクリプト実行時の例外
	 */
	protected void eval(final ScriptEngine engine, String path) 
	throws ScriptException {

		// 共通関数実行
		try {
			Reader in = new BufferedReader(new InputStreamReader(
				getClass().getResourceAsStream(path), SOURCE_ENCODING) );
			try {

				engine.put(ScriptEngine.FILENAME, path);
				engine.eval(in);
			} finally {
				in.close();
				
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public SqlString buildSql(String path, Map<String,Object> vars)
	throws ScriptException {
		
		final Source src = readScriptSource(path);

		final ScriptEngine engine = new ScriptEngineManager().getEngineByName("ECMAScript");

		// オンメモリーのファイル名
		final String memorySrcFileName = path + ".js";
		
		try {
			
			eval(engine, "SqlScript.js");

			for (Entry<String,Object> varEntry : vars.entrySet() ) {
				engine.put(varEntry.getKey(), varEntry.getValue() );
			}

			engine.put(ScriptEngine.FILENAME, memorySrcFileName);
			Object sql = engine.eval(src.toString() );

			// エラーチェック
			Object lastError = engine.get("$$lastError");
			if (lastError != null) {
				throw new RuntimeException(lastError.toString() );
			}

			return new SqlString(String.valueOf(sql) );

		} catch(ScriptException e) {
			
			if (e.getFileName().equals(memorySrcFileName) ) {
				// オンメモリスクリプトのエラーの場合、行番号を書き換えて再送出。
				throw new ScriptException(e.getMessage(),
						src.getPath(),
						src.get(e.getLineNumber() ).getLineNumber() );
			}

			throw e;
		}
	}

	/**
	 * SQL 文字列を組み立てる
	 * @param srcObject ソースオブジェクト(.sql ファイルの基点)
	 * @param suffix .sql ファイルのサフィックス
	 * @param vars スクリプトで利用する変数のマップ
	 * @return
	 * @throws ScriptException
	 */
	public SqlString buildSql(Object srcObject, String suffix, Map<String,Object> vars)
	throws ScriptException {
		return buildSql(getPath(srcObject, suffix), vars);
	}
	
	/**
	 * オブジェクトと同階層の .sql ファイルのパスを取得する。
	 * @param object オブジェクト
	 * @param suffix サフィックス
	 * @return
	 */
	private String getPath(Object object, String suffix) {
		Class<?> paramClass = object.getClass();
		String paramClassPath = "/" + 
			paramClass.getName().replace('.', '/');
		return String.format("%s%s.sql", paramClassPath, suffix);
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public Source readScriptSource(String path) {
		try {
			return readScriptSourceImpl(path);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Source readScriptSourceImpl(String path) throws IOException {

		final BufferedReader in = new BufferedReader(new InputStreamReader(
				getClass().getResourceAsStream(path), SOURCE_ENCODING) );
		
		try {

			Source src = new Source(path);
			src.add("var $$sql = \"\";", 0);

			String line;
			int lineNumber = 0;
			boolean first = true;
			
			while ( (line = in.readLine() ) != null) {

				lineNumber++;
				line = line.trim();
				
				if (line.length() == 0) {
					continue;
				}
				
				int index = line.indexOf(COMMENT_PREFIX);
				if (index != -1) {

					// 「--」 で開始するコメント行を検出
					String comment = line.substring(index);
					
					if (comment.startsWith(SCRIPT_COMMENT_PREFIX) ) {
						// 「--!」 で開始するコメントをスクリプトとして扱う
						String script = comment.substring(
								SCRIPT_COMMENT_PREFIX.length() );
						src.add(script, lineNumber);
					}
					
					line = line.substring(0, index);
				}

				if (line.length() > 0) {
					if (!first) {
						src.add("$$sql += \" \";", lineNumber);
					}
					String script = "$$sql += $$Sql.formatSql.call(this, \""  +
						SqlUtil.encodeScriptString(line) +
						"\");";
					src.add(script, lineNumber);
					first = false;
				}
			}
			
			src.add("$$sql;", lineNumber);
			return src;

		} finally {
			in.close();
		}
	}

	/**
	 * スクリプトソース
	 */
	public static class Source {
		
		private final List<SourceLine> list = new ArrayList<SourceLine>();
		
		private final String path;
		
		public Source(String path) {
			this.path = path;
		}
		
		public String getPath() {
			return path;
		}
		
		public void add(String line, int lineNumber) {
			list.add(new SourceLine(line, lineNumber) );
		}
		
		public SourceLine get(int lineNo) {
			return list.get(lineNo - 1);
		}

		public int length() {
			return list.size();
		}

		public String toString() {
			final StringBuilder src = new StringBuilder();
			for (SourceLine line : list) {
				src.append(line.getLine() );
				src.append('\n');
			}
			return src.toString();
		}
		
		public static class SourceLine {
			private final String line;
			private final int lineNumber;
			public SourceLine(String line, int lineNumber) {
				this.line = line;
				this.lineNumber = lineNumber;
			}
			public String getLine() {
				return line;
			}
			public int getLineNumber() {
				return lineNumber;
			}
			public String toString() {
				return String.format("%d: %s", lineNumber, line);
			}
		}
	}
}
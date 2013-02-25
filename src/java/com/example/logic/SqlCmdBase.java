package com.example.logic;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import com.example.common.sql.SqlScript;
import com.example.common.sql.SqlString;

/**
 * SqlCmdBase
 * @author Kazuhiko Arase
 */
public abstract class SqlCmdBase extends CmdBase {
	
	/**
	 * スクリプトで使用するパラメータのキー。
	 */
	private static final String PARAM_KEY = "param";
	
	/**
	 * コンストラクタ
	 */
	protected SqlCmdBase() {
	}
	
	/**
	 * SQL を組み立てる。
	 * @return SQL
	 * @throws ScriptException
	 * クラス名 + ".sql" のリソースを読み込んで SQLを組み立てます。
	 */
	protected SqlString buildSql() throws ScriptException {
		return buildSql("");
	}

	/**
	 * サフィックスを指定して SQL を組み立てる。
	 * @param suffix サフィックス
	 * @return SQL
	 * @throws ScriptException
	 * クラス名 + サフィックス + ".sql" のリソースを読み込んで SQLを組み立てます。
	 */
	protected SqlString buildSql(String suffix) throws ScriptException {
		return buildSql(suffix, null);
	}

	/**
	 * サフィックスを指定して SQL を組み立てる。
	 * @param suffix サフィックス
	 * @param scriptVars スクリプトで利用する変数のマップ
	 * @return SQL
	 * @throws ScriptException
	 * クラス名 + サフィックス + ".sql" のリソースを読み込んで SQLを組み立てます。
	 */
	protected SqlString buildSql(String suffix, Map<String,Object> scriptVars) throws ScriptException {
		Map<String,Object> vars = new HashMap<String, Object>();
		vars.put(PARAM_KEY, this);
		if (scriptVars != null) {
			vars.putAll(scriptVars);
		}
		return new SqlScript().buildSql(this, suffix, vars);
	}
}
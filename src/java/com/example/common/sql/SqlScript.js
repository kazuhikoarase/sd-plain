/**
 * SqlScript.js
 * @author Kazuhiko Arase
 */

/**
 * 最後に発生したエラー
 */
var $$lastError = null;

var $$Sql = {

	formatSql : function(sql) {
		return new Packages.com.example.common.sql.SqlFormatter(sql).format(
			new Packages.com.example.common.sql.SqlFormatter.FormatHandler( {
				getConstant : function(propName, hint) {
					var value = null;
					try {
						value = eval(propName);
					} catch(e) {
						// 存在しない変数の場合、無視
						return null;
					}
					// 変数のプロパティが見つからない場合、エラー
					if (typeof(value) == "undefined") {
						$$lastError = "undefined property:" + propName;
						return null;
					}
					return Packages.com.example.common.sql.SqlType.toConstant(propName, value, hint);
				}
			} )
		);	
	}
};

/**
 * 頻出クラスのインポート
 */
var Constants = Packages.com.example.common.Constants;
var Util = Packages.com.example.common.Util;

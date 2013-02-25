package com.example.common.sql;

import java.sql.SQLException;

/**
 * Sql
 * @author Kazuhiko Arase
 */
public class Sql {

	/**
	 * コンストラクタ
	 */
	private Sql() {
	}

	/**
	 * バッチ起動用のシステムプロパティをセットアップする。
	 */
	public static void setupBatchEnv() {
		System.setProperty("java.util.logging.config.file",
				"/home/sd-home/conf/logging.properties");
		System.setProperty("java.naming.factory.initial",
				"com.example.common.naming.InitialContextFactory");
	}

	/**
	 * 既定のトランザクションの、現在のSQL接続を取得する。
	 * @return SQL接続
	 * @throws SQLException
	 */
	public static SqlConnection currentConnection()
	throws SQLException {
		return currentConnection(Transaction.DEFAULT);
	}
	
	/**
	 * 現在のSQL接続を取得する。
	 * @param transaction トランザクション
	 * @return SQL接続
	 * @throws SQLException
	 */
	public static SqlConnection currentConnection(Transaction transaction)
	throws SQLException {
		return new SqlConnection(transaction.getConnection() );
	}
	
	/**
	 * 既定のトランザクションで、 トランザクション処理を実行する。
	 * @param command トランザクション処理
	 * @throws Exception
	 */
	public static void transaction(TransactionCommand command)
	throws Exception {
		transaction(Transaction.DEFAULT, command);
	}

	/**
	 * トランザクション処理を実行する。
	 * @param transaction トランザクション
	 * @param command トランザクション処理
	 * @throws Exception
	 */
	public static void transaction(Transaction transaction, TransactionCommand command)
	throws Exception {
		transaction.begin();
		try {
			command.execute();
		} finally {
			transaction.end();
		}
	}

	/**
	 * トランザクション処理
	 */
	public interface TransactionCommand{
		/**
		 * 処理を実行する
		 * @throws Exception システムエラー
		 */
		void execute() throws Exception;
	}
}
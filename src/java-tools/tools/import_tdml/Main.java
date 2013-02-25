package tools.import_tdml;

import com.example.common.sql.Sql;
import com.example.common.sql.Transaction;

/**
 * TDMLインポート メイン
 * @author Kazuhiko Arase
 */
public class Main {

	public static void main(String[] args)
	throws Exception {
		Sql.setupBatchEnv();
		
		{
			String schema = "SCHEMA1";
			ImportTdml importer = new ImportTdml();
			importer.addConversion(schema, "jdbc/APP_DS");
			importer.setTran(Transaction.DEFAULT);
			importer.setSchemaPattern(schema);
			//importer.setTableNamePattern("***");
			importer.setOutputDir("src/xml/com/example/db/table");
			importer.start();
		}
		
		{
			String schema = "SCHEMA2";
			ImportTdml importer = new ImportTdml();
			importer.addConversion(schema, "jdbc/RMT_DS");
			importer.setTran(Transaction.SECOND);
			importer.setSchemaPattern(schema);
			//importer.setTableNamePattern("***");
			importer.setOutputDir("src/xml/com/example/db/table");
			importer.start();
		}
	}
}
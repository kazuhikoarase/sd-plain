package tools.import_tdml;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.example.common.logging.Log;
import com.example.common.logging.LogFactory;
import com.example.common.sql.Sql;
import com.example.common.sql.Transaction;

/**
 * TDMLインポート
 * @author Kazuhiko Arase
 */
public class ImportTdml {

	private final Log log = LogFactory.getLog(getClass() );

	/**
	 * トランザクション
	 */
	private Transaction tran;
	
	/**
	 * カタログ
	 */
	private String catalog;
	
	/**
	 * スキーマパターン
	 */
	private String schemaPattern;
	
	/**
	 * テーブル名パターン
	 */
	private String tableNamePattern;

	/**
	 * 種別
	 */
	private String[] types;

	/**
	 * 出力先ディレクトリ
	 */
	private String outputDir;
	
	private Map<String,String> conversionMap = new HashMap<String, String>(); 
	
	public void start() throws Exception {

		Sql.transaction(tran, new Sql.TransactionCommand() {
			@Override
			public void execute() throws Exception {
				doConnection(Sql.
					currentConnection(tran).
					getJdbcConnection() );
			}
		} );
	}
	
	public void addConversion(String from, String to) {
		conversionMap.put(from, to);
	}
	
	private void doConnection(Connection conn) throws Exception {

		DatabaseMetaData meta = conn.getMetaData();
		ResultSet tablesRs = meta.getTables(
				getCatalog(),
				getSchemaPattern(),
				getTableNamePattern(),
				getTypes() );
		try {
			while (tablesRs.next() ) {
				doTable(meta, tablesRs);
			}
		} finally {
			tablesRs.close();
		}
	}
	
	private void doTable(DatabaseMetaData meta, ResultSet tablesRs) throws Exception {

		String catalog = tablesRs.getString("TABLE_CAT");
		String schema = tablesRs.getString("TABLE_SCHEM");
		String tableName = tablesRs.getString("TABLE_NAME");
		
		log.info(String.format("%s.%s.%s", catalog, schema, tableName) );

		Document doc = DocumentBuilderFactory.
			newInstance().newDocumentBuilder().
			newDocument();

		Node tableNode = doc.appendChild(doc.createElement("table") );
		appendTextNode(doc, tableNode, "id", tablesRs.getString("TABLE_NAME") );
		appendTextNode(doc, tableNode, "name", tablesRs.getString("REMARKS") );
		appendTextNode(doc, tableNode, "schema", tablesRs.getString("TABLE_SCHEM") );

		Set<String> pkeys = new HashSet<String>();
		ResultSet pkeysRs = meta.getPrimaryKeys(catalog, schema, tableName);
		try {
			while(pkeysRs.next() ) {
				pkeys.add(pkeysRs.getString("COLUMN_NAME") );
			}
		} finally {
			pkeysRs.close();
		}
		
		ResultSet columnsRs = meta.getColumns(catalog, schema, tableName, null);
		try {
			while (columnsRs.next() ) {
				Node columnNode = tableNode.appendChild(doc.createElement("column") );
				doColumn(doc, columnNode, pkeys, columnsRs);
			}
		} finally {
			columnsRs.close();
		}
		
		File dir = new File(getOutputDir() );
		File tdmlFile = new File(dir, tableName + ".tdml");
		TransformerFactory.newInstance().newTransformer().
			transform(new DOMSource(doc),
					new StreamResult(tdmlFile) );
	}
	
	private void doColumn(Document doc, Node columnNode, Set<String> pkeys, ResultSet columnsRs) throws Exception {

		int size = columnsRs.getInt("COLUMN_SIZE");
		int digits = columnsRs.getInt("DECIMAL_DIGITS");
		appendTextNode(doc, columnNode, "id", columnsRs.getString("COLUMN_NAME") );
		appendTextNode(doc, columnNode, "name", columnsRs.getString("REMARKS") );
		appendTextNode(doc, columnNode, "type", columnsRs.getString("TYPE_NAME") );
		appendTextNode(doc, columnNode, "size", (digits > 0)? size + "," + digits : String.valueOf(size) );
		appendTextNode(doc, columnNode, "not-null", (columnsRs.getInt("NULLABLE") != DatabaseMetaData.columnNullable)? "yes" : "");
		appendTextNode(doc, columnNode, "primary-key", pkeys.contains(columnsRs.getString("COLUMN_NAME") )? "yes" : "");

	}
	
	private void appendTextNode(Document doc, Node node, String name, String value) {
		if (value == null || value.length() == 0) {
			return;
		}
		if (conversionMap.containsKey(value) ) {
			value = conversionMap.get(value);
		}
		node.appendChild(doc.createElement(name) ).
			appendChild(doc.createTextNode(value) );
	}

	/**
	 * トランザクションを設定する。
	 * @param tran トランザクション
	 */
	public void setTran(Transaction tran) {
		this.tran = tran;
	}

	/**
	 * トランザクションを取得する。
	 * @return トランザクション
	 */
	public Transaction getTran() {
		return tran;
	}

	/**
	 * カタログを設定する。
	 * @param catalog カタログ
	 */
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	/**
	 * カタログを取得する。
	 * @return カタログ
	 */
	public String getCatalog() {
		return catalog;
	}

	/**
	 * スキーマパターンを設定する。
	 * @param schemaPattern スキーマパターン
	 */
	public void setSchemaPattern(String schemaPattern) {
		this.schemaPattern = schemaPattern;
	}

	/**
	 * スキーマパターンを取得する。
	 * @return スキーマパターン
	 */
	public String getSchemaPattern() {
		return schemaPattern;
	}

	/**
	 * テーブル名パターンを設定する。
	 * @param tableNamePattern テーブル名パターン
	 */
	public void setTableNamePattern(String tableNamePattern) {
		this.tableNamePattern = tableNamePattern;
	}

	/**
	 * テーブル名パターンを取得する。
	 * @return テーブル名パターン
	 */
	public String getTableNamePattern() {
		return tableNamePattern;
	}

	/**
	 * 種別を設定する。
	 * @param types 種別
	 */
	public void setTypes(String[] types) {
		this.types = types;
	}

	/**
	 * 種別を取得する。
	 * @return 種別
	 */
	public String[] getTypes() {
		return types;
	}

	/**
	 * 出力先ディレクトリを設定する。
	 * @param outputDir 出力先ディレクトリ
	 */
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	/**
	 * 出力先ディレクトリを取得する。
	 * @return 出力先ディレクトリ
	 */
	public String getOutputDir() {
		return outputDir;
	}
}
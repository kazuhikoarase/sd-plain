package com.example.common.sql;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.example.common.logging.Log;
import com.example.common.logging.LogFactory;

/**
 * データソース設定.
 * @author Kazuhiko Arase
 */
public class DataSourceConfig {
	
	private static final String CONFIG_FILE = "datasource-config.xml";
	
	private static final Log log = LogFactory.getLog(DataSourceConfig.class);

	private DataSourceConfig() {
	}
	
	private static Map<String,DataSource> dataSourceMap =
			new HashMap<String, DataSource>();
	
	static {
		try {

			Document dataSourceConfigDoc = loadConfig();
			NodeList dataSourceNodes = dataSourceConfigDoc.getElementsByTagName("datasource");
			
			for (int i = 0; i < dataSourceNodes.getLength(); i++) {
				Element dataSourceNode = (Element)dataSourceNodes.item(i);
				DataSource dataSource = new DataSource();
				dataSource.setName(dataSourceNode.getAttribute("name") );
				dataSource.setDriverClassName(dataSourceNode.getAttribute("driverClassName") );
				dataSource.setUrl(dataSourceNode.getAttribute("url") );
				dataSource.setUsername(dataSourceNode.getAttribute("username") );
				dataSource.setPassword(dataSourceNode.getAttribute("password") );
				dataSource.setPreExecStatements(dataSourceNode.getAttribute("preExecStatements") );
				dataSource.setPostExecStatements(dataSourceNode.getAttribute("postExecStatements") );
				dataSourceMap.put(dataSource.getName(), dataSource);
			}

		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Document loadConfig() {

        Document config = null;

        InputStream in = DataSourceConfig.class.
        		getResourceAsStream("/" + CONFIG_FILE);

        if (in == null) {
        	in = ClassLoader.getSystemResourceAsStream(CONFIG_FILE);
        }

        if (in != null) {

            try {

            	try {

	            	log.info("loading config");
	            	
	            	config = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
	                
	            	log.info("done.");
	                
                } finally {
	                in.close();
	            }
            
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        return config;
    }
    
	public static DataSource get(String dataSourceName) {
		if (!dataSourceMap.containsKey(dataSourceName) ) {
			throw new RuntimeException("no such datasource: " + dataSourceName);
		}
		return dataSourceMap.get(dataSourceName);
	}
	
	public static Set<String> getDataSourceNames() {
		return dataSourceMap.keySet();
	}

	public static class DataSource {
		
		/**
		 * データソース名
		 */
		private String name;

		/**
		 * ドライバクラス名
		 */
		private String driverClassName;
		
		/**
		 * 接続URL
		 */
		private String url;
		
		/**
		 * ユーザー
		 */
		private String username;
		
		/**
		 * パスワード
		 */
		private String password;

		/**
		 * 主処理開始前ステートメント
		 */
		private String preExecStatements;

		/**
		 * 主処理開始後ステートメント
		 */
		private String postExecStatements;
		
		public DataSource() {
		}
		

		/**
		 * データソース名を設定する。
		 * @param name データソース名
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * データソース名を取得する。
		 * @return データソース名
		 */
		public String getName() {
			return name;
		}

		/**
		 * ドライバクラス名を設定する。
		 * @param driverClassName ドライバクラス名
		 */
		public void setDriverClassName(String driverClassName) {
			this.driverClassName = driverClassName;
		}

		/**
		 * ドライバクラス名を取得する。
		 * @return ドライバクラス名
		 */
		public String getDriverClassName() {
			return driverClassName;
		}

		/**
		 * 接続URLを設定する。
		 * @param url 接続URL
		 */
		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * 接続URLを取得する。
		 * @return 接続URL
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * ユーザーを設定する。
		 * @param username ユーザー
		 */
		public void setUsername(String username) {
			this.username = username;
		}

		/**
		 * ユーザーを取得する。
		 * @return ユーザー
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * パスワードを設定する。
		 * @param password パスワード
		 */
		public void setPassword(String password) {
			this.password = password;
		}

		/**
		 * パスワードを取得する。
		 * @return パスワード
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * 主処理開始前ステートメントを設定する。
		 * @param preExecStatements 主処理開始前ステートメント
		 */
		public void setPreExecStatements(String preExecStatements) {
			this.preExecStatements = preExecStatements;
		}

		/**
		 * 主処理開始前ステートメントを取得する。
		 * @return 主処理開始前ステートメント
		 */
		public String getPreExecStatements() {
			return preExecStatements;
		}

		/**
		 * 主処理開始後ステートメントを設定する。
		 * @param postExecStatements 主処理開始後ステートメント
		 */
		public void setPostExecStatements(String postExecStatements) {
			this.postExecStatements = postExecStatements;
		}

		/**
		 * 主処理開始後ステートメントを取得する。
		 * @return 主処理開始後ステートメント
		 */
		public String getPostExecStatements() {
			return postExecStatements;
		}
	}
}
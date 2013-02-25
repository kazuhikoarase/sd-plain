package com.example.common;

/**
 * 定数
 * @author Apache Ant
 */
public class Constants {
	
	/**
	 * コンストラクタ
	 */
	private Constants() {
	}

	/**
	 * OFF、NO、FALSE、いいえ、しない、不可、その他否定全般 : 0
	 */
	public static final String OFF = "0";

	/**
	 * ON、YES、TRUE、はい、する、可、その他肯定全般 : 1
	 */
	public static final String ON = "1";

	/**
	 * データソース名 - 既定 : jdbc/APP_DS
	 */
	public static final String DATASOURCE_NAME_APP = "jdbc/APP_DS";

	/**
	 * データソース名 - 第二データソース : jdbc/SECOND_DS
	 */
	public static final String DATASOURCE_NAME_SECOND = "jdbc/SECOND_DS";

	/**
	 * ＊＊＊＊種別 - あああ : 1
	 */
	public static final String TEST_TYPE_A = "1";

	/**
	 * ＊＊＊＊種別 - いいい : 2
	 */
	public static final String TEST_TYPE_B = "2";

	/**
	 * ＊＊＊＊種別 - ううう : 3
	 */
	public static final String TEST_TYPE_C = "3";

	/**
	 * ＊＊＊＊種別のラベルを取得する。
	 */
    public static String getLabelForTestType(String testType) {
		if (TEST_TYPE_A.equals(testType) ) {
			return "あああ";
		} else if (TEST_TYPE_B.equals(testType) ) {
			return "いいい";
		} else if (TEST_TYPE_C.equals(testType) ) {
			return "ううう";
		} else {
			return "";
		}
    }

}
	


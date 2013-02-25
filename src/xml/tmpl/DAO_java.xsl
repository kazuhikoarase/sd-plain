<?xml version="1.0" encoding="Utf-8" ?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	>

<xsl:import href="functions.xsl"/>

<xsl:output method="text" encoding="Utf-8"/>

<xsl:param name="packageName"/>
<xsl:param name="className"/>
<xsl:param name="package-prefix"/>

<xsl:variable name="types" select="document('DAO_types.xml')/Types"/>

<!--
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
-->
<xsl:template match="table">

<xsl:text/>// 自動生成されたファイルです。 編集しないでください。

package <xsl:value-of select="$packageName"/>;

import <xsl:value-of select="$package-prefix"/>.common.sql.Table;
import <xsl:value-of select="$package-prefix"/>.common.sql.TableRow;

/**
 * <xsl:value-of select="name"/>.
 * @author Apache Ant
 */
public class <xsl:value-of select="$className"/> {

	/**
	 * テーブルクラスの唯一のインスタンス
	 */	
	private static final <xsl:value-of select="$className"/>Impl instance = new <xsl:value-of select="$className"/>Impl();

	/**
	 * テーブルクラスの唯一のインスタンスを取得する。
	 */
	private static <xsl:value-of select="$className"/>Impl getInstance() {
		return instance;
	}

	/**
	 * インスタンス化禁止
	 */
	private <xsl:value-of select="$className"/>() {
	}

	/**
	 * 検索条件を指定して検索を行う。
	 * @param whereClause 検索条件
	 * @return 検索結果の行データリスト
	 */
	public static java.util.List&lt;Row&gt; select(final com.example.common.sql.SqlString whereClause) throws java.sql.SQLException {
		return getInstance().select(whereClause);
	}

	/**
	 * 検索条件を指定して件数取得を行う。
	 * @param whereClause 検索条件
	 * @return 件数
	 */
	public static int selectCount(final com.example.common.sql.SqlString whereClause) throws java.sql.SQLException {
		return getInstance().selectCount(whereClause);
	}

	/**
	 * プライマリキーを指定して1行検索を行う。
	 * @param row プライマリキーを設定した行データ
	 * @return 検索ヒットした場合は true
	 */
	public static boolean selectRow(final Row row) throws java.sql.SQLException {
		return getInstance().selectRow(row);
	}	

	/**
	 * 1行登録を行う。
	 * @param row 登録内容を設定した行データ
	 */
	public static void insertRow(final Row row) throws java.sql.SQLException {
		getInstance().insertRow(row);
	}

	/**
	 * プライマリキーを指定して1行更新を行う。
	 * @param row プライマリキーを設定した行データ
	 */
	public static void updateRow(final Row row) throws java.sql.SQLException {
		getInstance().updateRow(row);
	}

	/**
	 * プライマリキーを指定して1行削除を行う。
	 * @param row プライマリキーを設定した行データ
	 */
	public static void deleteRow(final Row row) throws java.sql.SQLException {
		getInstance().deleteRow(row);
	}

	/**
	 * 新規行データを作成する。
	 */
	public static Row newRow() {
		return getInstance().newRow();
	}

	/**
     * テーブルクラスの実装
     */	
	private static class <xsl:value-of select="$className"/>Impl extends Table&lt;<xsl:value-of select="$className"/>.Row&gt; {

		/**
		 * コンストラクタ
		 */
		public <xsl:value-of select="$className"/>Impl() {
			super(Row.class);
		}

		/**
		 * 本テーブルの表メタデータを取得する。
		 */
		protected Table.TableMetaData getTableMetaData() {
			return new Table.TableMetaData("<xsl:value-of select="schema"/>", "<xsl:value-of select="id"/>");
		}
	
		/**
		 * 本テーブルの列メタデータを取得する。
		 */
		protected Table.ColumnMetaData[] getColumnMetaData() {
		 	return new Table.ColumnMetaData[]{<xsl:text/>
	<xsl:for-each select="column">
			<xsl:variable name="name">
				<xsl:call-template name="Hungarian">
					<xsl:with-param name="s" select="id"/>
					<xsl:with-param name="lower">true</xsl:with-param>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="type" select="$types/Type[@sql-type=current()/type]/@java-type"/>
			<xsl:variable name="primary-key">
				<xsl:choose>
					<xsl:when test="primary-key='yes'">true</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:if test="position()&gt;1">,</xsl:if>
				new Table.ColumnMetaData("<xsl:value-of select="id"
			/>", "<xsl:value-of select="$name"
			/>", <xsl:value-of select="$type"
			/>.class, <xsl:value-of select="$primary-key"/>)<xsl:text/>
	</xsl:for-each>
			};
		}
	}	

	/**
	 * <xsl:value-of select="name"/> の行データ.
	 */
	public interface Row extends TableRow {<xsl:text/>
	<xsl:for-each select="column">
			<xsl:variable name="name">
				<xsl:call-template name="Hungarian">
					<xsl:with-param name="s" select="id"/>
					<xsl:with-param name="lower">true</xsl:with-param>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="Name">
				<xsl:call-template name="Hungarian">
					<xsl:with-param name="s" select="id"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="type" select="$types/Type[@sql-type=current()/type]/@java-type"/>
		/**
		 * <xsl:value-of select="name"/>を設定する
		 * @param <xsl:value-of select="$name"/><xsl:text> </xsl:text><xsl:value-of select="name"/>
		 */
		public void set<xsl:value-of select="$Name"/>(<xsl:value-of select="$type"/>
			<xsl:text> </xsl:text><xsl:value-of select="$name"/>);

		/**
		 * <xsl:value-of select="name"/>を取得する
		 * @return <xsl:value-of select="name"/>
		 */
		public <xsl:value-of select="$type"/> get<xsl:value-of select="$Name"/>();
	</xsl:for-each>
	}
}

</xsl:template>


</xsl:stylesheet>

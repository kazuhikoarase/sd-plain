<?xml version="1.0" encoding="Utf-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:consts="urn:Constants">

<xsl:import href="functions.xsl"/>

<xsl:output method="text" encoding="Utf-8"/>

<xsl:param name="package-prefix"/>

<xsl:template match="/consts:Constants">

	<xsl:text>package </xsl:text>
	<xsl:value-of select="$package-prefix"/>
	<xsl:text>.common;</xsl:text>

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
<xsl:apply-templates select="consts:Const"/>
<xsl:apply-templates select="consts:Const-Group"/>
}
	

</xsl:template>

<xsl:template match="consts:Const">
	/**
	 * <xsl:value-of select="@comment"/> : <xsl:value-of select="@value"/><xsl:if test="@deprecated = 'true'">
	 * @deprecated</xsl:if>
	 */
	public static final String <xsl:value-of select="@name"/> = "<xsl:value-of select="@value"/>";
</xsl:template>

<xsl:template match="consts:Const-Group">

<xsl:variable name="name">
  <xsl:call-template name="Hungarian">
    <xsl:with-param name="s" select="@name"/>
  </xsl:call-template>
</xsl:variable>
<xsl:variable name="lname">
  <xsl:call-template name="Hungarian">
    <xsl:with-param name="s" select="@name"/>
    <xsl:with-param name="lower" select="'yes'"/>
  </xsl:call-template>
</xsl:variable>

<xsl:for-each select="consts:Const">
	/**
	 * <xsl:value-of select="../@comment"/> - <xsl:value-of select="@comment"/> : <xsl:value-of select="@value"/><xsl:if test="@deprecated = 'true'">
	 * @deprecated</xsl:if>
	 */
	public static final String <xsl:value-of select="../@name"/>_<xsl:value-of select="@name"/> = "<xsl:value-of select="@value"/>";
</xsl:for-each>
<xsl:if test="@labelFunction = 'true'">
	/**
	 * <xsl:value-of select="@comment"/>のラベルを取得する。
	 */
    public static String getLabelFor<xsl:value-of select="$name"/>(String <xsl:value-of select="$lname"/>) {
		<xsl:for-each select="consts:Const"><xsl:if test="position() > 1"> else </xsl:if>if (<xsl:value-of select="../@name"/>_<xsl:value-of select="@name"/>.equals(<xsl:value-of select="$lname"/>) ) {
			return "<xsl:value-of select="@comment"/>";
		}</xsl:for-each> else {
			return "";
		}
    }
</xsl:if>
</xsl:template>

</xsl:stylesheet>

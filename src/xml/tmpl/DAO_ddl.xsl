<?xml version="1.0" encoding="Utf-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="functions.xsl"/>

<!-- 
<xsl:output method="text" encoding="Shift_JIS"/>
 -->
<xsl:output method="text" encoding="UTF-8"/>

<xsl:variable name="types" select="document('DAO_types.xml')/Types"/>

<!-- 改行 -->
<xsl:variable name="br"><xsl:text>
</xsl:text></xsl:variable>

<!-- ファイル本体作成 -->
<xsl:template match="table"
>-----------------------------------------------------------------
-- <xsl:value-of select="id"/>
-----------------------------------------------------------------

-----------------------------------------------------------------
-- drop <xsl:value-of select="id"/>
-----------------------------------------------------------------

drop table <xsl:value-of select="id"/>;

-----------------------------------------------------------------
-- create <xsl:value-of select="id"/>
-----------------------------------------------------------------

create table <xsl:value-of select="id"/><xsl:text> (</xsl:text>
<xsl:for-each select="column[not(disable='yes')]">
  <xsl:if test="position()>1">,</xsl:if>
  <xsl:value-of select="$br"/>
  <xsl:text>    </xsl:text>
  <xsl:value-of select="id"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="type"/>
  <xsl:if test="$types/Type[@sql-type=current()/type]/@variable='yes'">
    <xsl:text>(</xsl:text>
    <xsl:value-of select="size"/>
    <xsl:text>)</xsl:text>
  </xsl:if>
  <xsl:if test="not-null='yes'"> not null</xsl:if>
  <xsl:if test="default"> default <xsl:value-of select="default"/></xsl:if>
</xsl:for-each>

<xsl:if test="count(column[primary-key='yes' and not(disable='yes')])>0">
  <xsl:text>,</xsl:text>
  <xsl:value-of select="$br"/>
  <xsl:text>    -- プライマリキー</xsl:text>
  <xsl:value-of select="$br"/>
  <xsl:text>    primary key (</xsl:text>
  <xsl:for-each select="column[primary-key='yes' and not(disable='yes')]">
    <xsl:if test="position()>1">, </xsl:if>
    <xsl:value-of select="id"/>
  </xsl:for-each>
  <xsl:text>)</xsl:text>
</xsl:if>

<xsl:value-of select="$br"/>
<xsl:text>)</xsl:text>

<xsl:if test="table-space">
  <xsl:text> in </xsl:text>
  <xsl:value-of select="table-space"/>
</xsl:if>

<xsl:text>;</xsl:text>

<xsl:value-of select="$br"/>
<xsl:value-of select="$br"/>

<xsl:if test="count(index)>0">
  <xsl:text>-- インデックス</xsl:text>
  <xsl:for-each select="index">
    <xsl:value-of select="$br"/>
    <xsl:text>create</xsl:text>
    <xsl:if test="unique='yes'"> unique</xsl:if>
    <xsl:text> index </xsl:text>
    <xsl:value-of select="id"/> on <xsl:value-of select="../id"/>
    <xsl:text> (</xsl:text>
    <xsl:for-each select="column">
      <xsl:if test="position()>1">,</xsl:if>
      <xsl:value-of select="$br"/>
      <xsl:text>    </xsl:text>
      <xsl:value-of select="id"/>
    </xsl:for-each>
    <xsl:value-of select="$br"/>
    <xsl:text>);</xsl:text>
  </xsl:for-each>
</xsl:if>

<xsl:value-of select="$br"/>
<xsl:value-of select="$br"/>

<xsl:text>-- テーブルコメント</xsl:text>
comment on table <xsl:value-of select="id"/> is '<xsl:value-of select="name"/>';

<xsl:text>-- カラムコメント</xsl:text>
<xsl:for-each select="column[not(disable='yes')]">
  <xsl:value-of select="$br"/>
  <xsl:text>comment on column </xsl:text>
  <xsl:value-of select="../id"/><xsl:text>.</xsl:text>
  <xsl:value-of select="id"/><xsl:text> is '</xsl:text>
  <xsl:value-of select="name"/>
  <xsl:text>';</xsl:text>
</xsl:for-each>

------------------------- END OF FILE ---------------------------

</xsl:template>

</xsl:stylesheet>

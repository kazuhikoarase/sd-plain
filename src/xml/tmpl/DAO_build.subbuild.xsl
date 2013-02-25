<?xml version="1.0" encoding="Utf-8" ?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	>

	<xsl:import href="functions.xsl"/>

	<xsl:output method="xml"/>

	<xsl:param name="srcname"/>
	<xsl:param name="srcdir"/>

	<xsl:template match="/table">

		<xsl:variable name="className" select="substring-before($srcname, '.')"/>
		<xsl:variable name="packageName" select="translate($srcdir, '/', '.')"/>
		<xsl:variable name="hungarianClassName">
			<xsl:call-template name="Hungarian">
				<xsl:with-param name="s" select="$className"/>
			</xsl:call-template>
		</xsl:variable>

		<project default="all">

			<property name="build.dir" location="."/>
			<property name="src.dir" location="."/>
			<property name="dst.dir" location="."/>
			<property name="xslt.style" value=""/>
			<property name="xslt.extention" value=""/>
			<property name="package.prefix" value="" />

			<target name="all" depends="build"/>

			<target name="build">
			<echo>[Enter] subbuild for <xsl:value-of select="$srcdir"/>/<xsl:value-of select="$srcname"/></echo>

			<xslt 
				in="${{src.dir}}/{$srcdir}/{$srcname}"
				out="${{dst.dir}}/{$srcdir}/{$hungarianClassName}${{xslt.extention}}"
				style="${{build.dir}}/${{xslt.style}}">
				<param name="packageName" expression="{$packageName}"/>
				<param name="className" expression="{$hungarianClassName}"/>
				<param name="package-prefix" expression="${{package.prefix}}"/>
			</xslt>

			<echo>[Exit]</echo>

			</target>

		</project>
	
	</xsl:template>

</xsl:stylesheet>

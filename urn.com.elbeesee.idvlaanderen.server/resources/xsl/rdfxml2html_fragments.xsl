<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	xmlns:skos="http://www.w3.org/2004/02/skos/core#"
	xmlns:nk="http://1060.org"
	exclude-result-prefixes="nk xs rdf rdfs"
	version="1.0">
	
	<xsl:output 
    	method="html" 
    	indent="yes"
    	encoding="UTF-8"
    	omit-xml-declaration="yes"
    	media-type="text/html"/>

    <xsl:param name="localurl" nk:class="java.lang.String" />
    <xsl:param name="baseurl" nk:class="java.lang.String" />
    
	<xsl:template match="rdf:RDF">
		<html>
			<head>
			    <title>Fragments</title>
				<style type="text/css">
					@import url(/css/pure-min.css);
   	    		</style>
			</head>
			<body>
				<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="*[@rdf:about]">
		<xsl:variable name="newabout">
			<xsl:call-template name="string-replace-all">
				<xsl:with-param name="text" select="@rdf:about" />
				<xsl:with-param name="replace" select="$baseurl" />
				<xsl:with-param name="by" select="$localurl" />
			</xsl:call-template>
		</xsl:variable>
		<h2>Identifier: <a href="{$newabout}"><xsl:value-of select="@rdf:about"/></a></h2>
		<table class="pure-table pure-table-bordered">
			<xsl:for-each select="*[not(@rdf:resource)]">
				<tr>
					<th><xsl:value-of select="local-name()"/></th>
					<td><xsl:value-of select="."/></td>
				</tr>
			</xsl:for-each>
			<xsl:for-each select="*[@rdf:resource]">
				<xsl:variable name="newresource">
					<xsl:call-template name="string-replace-all">
						<xsl:with-param name="text" select="@rdf:resource" />
						<xsl:with-param name="replace" select="$baseurl" />
						<xsl:with-param name="by" select="$localurl" />
					</xsl:call-template>
				</xsl:variable>
				<tr>
					<th><xsl:value-of select="local-name()"/></th>
					<td><a href="{$newresource}"><xsl:value-of select="@rdf:resource"/></a></td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	
	<xsl:template name="string-replace-all">
		<xsl:param name="text" />
		<xsl:param name="replace" />
		<xsl:param name="by" />
		<xsl:choose>
			<xsl:when test="contains($text, $replace)">
				<xsl:value-of select="substring-before($text,$replace)" />
				<xsl:value-of select="$by" />
				<xsl:call-template name="string-replace-all">
					<xsl:with-param name="text" select="substring-after($text,$replace)" />
					<xsl:with-param name="replace" select="$replace" />
					<xsl:with-param name="by" select="$by" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
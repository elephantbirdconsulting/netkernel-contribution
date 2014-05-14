<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	xmlns:skos="http://www.w3.org/2004/02/skos/core#"
	exclude-result-prefixes="xs rdf rdfs"
	version="1.0">
	
	<xsl:output 
    	method="html" 
    	indent="yes"
    	encoding="UTF-8"
    	omit-xml-declaration="yes"
    	media-type="text/html"/>

	<xsl:template match="rdf:RDF">
		<html>
			<head>
			    <title>Data</title>
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
		<h2>Identifier: <a href="{@rdf:about}"><xsl:value-of select="@rdf:about"/></a></h2>
		<table class="pure-table pure-table-bordered">
			<xsl:for-each select="*[not(@rdf:resource)]">
				<tr>
					<th><xsl:value-of select="local-name()"/></th>
					<td><xsl:value-of select="."/></td>
				</tr>
			</xsl:for-each>
			<xsl:for-each select="*[@rdf:resource]">
				<tr>
					<th><xsl:value-of select="local-name()"/></th>
					<td><a href="{@rdf:resource}"><xsl:value-of select="@rdf:resource"/></a></td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
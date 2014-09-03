<xsl:stylesheet 
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:nk="http://1060.org"
	xmlns:sparql="http://www.w3.org/2005/sparql-results#"
	exclude-result-prefixes="sparql">

	<xsl:output 
    	method="text" 
    	encoding="UTF-8"
    	omit-xml-declaration="yes"
    	media-type="text/plain"/>

	<xsl:param name="search" nk:class="java.lang.String"/>
	<xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'" />
    <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />
    <xsl:variable name="searchlowercase">
    	<xsl:value-of select="translate($search, $uppercase, $smallcase)" />
    </xsl:variable>
 		
	<xsl:template match="/sparql:sparql">
		<results>
			<xsl:for-each select="sparql:results/sparql:result">
				<xsl:text>{</xsl:text>
				<xsl:text>"id":"</xsl:text><xsl:value-of select="sparql:binding[@name='id']/sparql:uri"/><xsl:text>",</xsl:text>
				<xsl:text>"score":"</xsl:text><xsl:value-of select="sparql:binding[@name='sc']/sparql:literal"/><xsl:text>",</xsl:text>
				<xsl:text>"name":"</xsl:text><xsl:value-of select="sparql:binding[@name='label']/sparql:literal"/><xsl:text>",</xsl:text>
				<xsl:text>"type": ["</xsl:text><xsl:value-of select="sparql:binding[@name='type']/sparql:uri"/><xsl:text>"],</xsl:text>
				<xsl:variable name="labellowercase">
					<xsl:value-of select="translate(sparql:binding[@name='label']/sparql:literal, $uppercase, $smallcase)" />
				</xsl:variable>
				<xsl:if test="$labellowercase = $searchlowercase">
					<xsl:text>"match": true</xsl:text>
				</xsl:if>
				<xsl:if test="$labellowercase != $searchlowercase">
					<xsl:text>"match": false</xsl:text>
				</xsl:if>				
				<xsl:text>}</xsl:text>
				<xsl:if test="position() != last()">
					<xsl:text>,</xsl:text>
				</xsl:if>
			</xsl:for-each>
		</results>
	</xsl:template>

</xsl:stylesheet>

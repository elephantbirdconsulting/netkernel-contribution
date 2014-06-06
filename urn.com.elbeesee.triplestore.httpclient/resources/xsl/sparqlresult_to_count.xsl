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
    	
    	<xsl:template match="/sparql:sparql">
    		<xsl:value-of select="sparql:results/sparql:result/sparql:binding[@name='triples']/sparql:literal"/>
    	</xsl:template>
    	
</xsl:stylesheet>
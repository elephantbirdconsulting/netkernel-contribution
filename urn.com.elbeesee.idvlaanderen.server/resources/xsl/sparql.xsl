<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:sp="http://www.w3.org/2005/sparql-results#"
	xmlns:nk="http://1060.org"
	exclude-result-prefixes="nk xs sp"
	version="1.0">
	
	<xsl:output 
    	method="html" 
    	indent="yes"
    	encoding="UTF-8"
    	omit-xml-declaration="yes"
    	media-type="text/html"/>

    <xsl:param name="localurl" nk:class="java.lang.String" />
    <xsl:param name="baseurl" nk:class="java.lang.String" />
    
    <xsl:template match="sp:sparql">
		<html>
			<head>
			    <title>SPARQL Results</title>
				<style type="text/css">
					@import url(/css/pure-min.css);
   	    		</style>
			</head>
			<body>
				<h1>SPARQL Results</h1>
					
				<table>
					<tr>
						<xsl:for-each select="sp:head/sp:variable">
							<th><xsl:value-of select="@name"/></th>
						</xsl:for-each>
					</tr>
					<xsl:for-each select="sp:results/sp:result">
						<tr>
							<xsl:for-each select="sp:binding">
								<td>
									<xsl:choose>
										<xsl:when test="sp:literal">
											<xsl:value-of select="sp:literal"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:variable name="newuri">
												<xsl:call-template name="string-replace-all">
													<xsl:with-param name="text" select="sp:uri" />
													<xsl:with-param name="replace" select="$baseurl" />
													<xsl:with-param name="by" select="$localurl" />
												</xsl:call-template>
											</xsl:variable>
											<a href="{$newuri}"><xsl:value-of select="sp:uri"/></a>
										</xsl:otherwise>
									</xsl:choose>
								</td>
							</xsl:for-each>
						</tr>
					</xsl:for-each>
				</table>
			</body>
		</html>
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
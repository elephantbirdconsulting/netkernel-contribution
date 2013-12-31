<xsl:stylesheet
	version="1.0"
	xmlns:foaf="http://xmlns.com/foaf/0.1/"
	xmlns:kbo="http://data.kbodata.be/def#"
	xmlns:locn="http://www.w3.org/ns/locn#"
	xmlns:org="http://www.w3.org/ns/org#"
	xmlns:oslo="http://purl.org/oslo/ns/localgov#" 
	xmlns:owl="http://www.w3.org/2002/07/owl#" 
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
	xmlns:rov="http://www.w3.org/ns/regorg#"
	xmlns:schema="http://schema.org/"
	xmlns:skos="http://www.w3.org/2004/02/skos/core#" 
	xmlns:vcard="http://www.w3.org/2006/vcard/ns#" 
	xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:nk="http://1060.org" 
	exclude-result-prefixes="foaf kbo locn org oslo owl rdf rdfs rov schema skos vcard xsd xsl">
	
	<xsl:param name="owner" nk:class="java.lang.String" />
	<xsl:param name="id" nk:class="java.lang.String" />

	<xsl:output 
    	method="html" 
    	indent="yes"
    	encoding="UTF-8"
    	omit-xml-declaration="yes"
    	media-type="text/html"/>
    	
    <xsl:template match="/">
    	<html>
    		<head>
    			<title><xsl:value-of select="$owner"/> : <xsl:value-of select="$id"/></title>
    		</head>
    		<body>
    			<xsl:for-each select="descendant::rdf:Description">
	    			<xsl:sort select="rdf:type"/>
					<div about="{@rdf:about}">
						<h2>
							<xsl:value-of select="@rdf:about"/>
						</h2>
						<table>
							<xsl:for-each select="*">
								<tr>
									<th><xsl:value-of select="local-name()"/></th>
									<xsl:if test="@rdf:resource">
										<td><xsl:value-of select="@rdf:resource"/></td>
									</xsl:if>
									<xsl:if test="not(@rdf:resource)">
										<td><xsl:value-of select="."/></td>
									</xsl:if>
								</tr>
							</xsl:for-each>
						</table>
					</div>
    			</xsl:for-each>
    		</body>
    	</html>
    </xsl:template>

</xsl:stylesheet>
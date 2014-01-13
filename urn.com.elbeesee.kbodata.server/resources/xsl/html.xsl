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
	xmlns:void="http://rdfs.org/ns/void#"
	xmlns:prov="http://www.w3.org/ns/prov#"
	xmlns:nk="http://1060.org" 
	exclude-result-prefixes="foaf kbo locn org oslo owl rdf rdfs rov schema skos vcard xsd xsl void prov nk">
	
	<xsl:output 
    	method="html" 
    	indent="yes"
    	encoding="UTF-8"
    	omit-xml-declaration="yes"
    	media-type="text/html"/>
	
	<xsl:param name="owner" nk:class="java.lang.String" />
	<xsl:param name="id" nk:class="java.lang.String" />
    	
    <xsl:template match="/">
    	<html>
    		<head>
    			<meta charset="utf-8"/>
    			<title><xsl:value-of select="$owner"/> : <xsl:value-of select="$id"/></title>
   			    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
   			    
   			    <!-- Le styles -->
   			    <link href="//netdna.bootstrapcdn.com/bootstrap/3.0.3/css/bootstrap.min.css" rel="stylesheet"/>
   			    <link href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet"/>
   			    <style type="text/css">
	   			    body {
						padding-top: 20px;
					}
				</style>
    		</head>
    		<body>
    			<div class="container">
   					<h1><xsl:value-of select="$owner"/> : <xsl:value-of select="$id"/></h1>
   					<xsl:for-each select="descendant::rdf:Description">
						<xsl:variable name="label">
							<xsl:choose>
								<xsl:when test="rdfs:label">
									<xsl:value-of select="rdfs:label"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="@rdf:about"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<div id="{substring-after(@rdf:about,'#')}" about="{@rdf:about}" class="span12">
							<h2><xsl:value-of select="$label"/></h2>
							<table class="table table-condensed table-bordered">
								<xsl:for-each select="*">
									<xsl:sort select="local-name()"/>
									<tr>
										<th><a href="{concat(namespace-uri(.),local-name())}"><xsl:value-of select="local-name()"/></a></th>
										<td>
											<xsl:choose>
												<xsl:when test="@rdf:resource">
													<a property="{local-name()}" href="{@rdf:resource}" resource="{@rdf:resource}"><xsl:value-of select="@rdf:resource"/></a><br/>
												</xsl:when>
												<xsl:otherwise>
													<span><xsl:attribute name="property"><xsl:value-of select="local-name(.)"/></xsl:attribute><xsl:value-of select="."/></span><br/>
												</xsl:otherwise>
											</xsl:choose>											
										</td>
									</tr>
								</xsl:for-each>
							</table>
						</div>
					</xsl:for-each>
					<div class="footer">
    					<table style="margin-left: auto;margin-right: auto;margin-bottom:50px;margin-top:50px;">
    						<tr>
    							<td>&#169; <a href="http://proxml.be">ProXML</a>,<a href="http://netkernelbook.org">Elephant Bird Consulting</a> 2014</td>
    						</tr>
    						<tr>
    							<td><a href="http://www.1060research.com"><img src="http://www.1060research.com/netkernel/poweredbynetkernel.png"/></a><a href="http://stardog.com"><img src="http://stardog.com/img/stardog@2x.png" height="100"/></a></td>
    						</tr>
	    				</table>
    				</div>
				</div>
				<!-- Le javascript
				================================================== -->
				<!-- Placed at the end of the document so the pages load faster -->
				<script src="//code.jquery.com/jquery-2.0.3.min.js"></script>
				<script src="//netdna.bootstrapcdn.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>
    		</body>
    	</html>
    </xsl:template>

</xsl:stylesheet>
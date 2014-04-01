<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:kbo="http://data.kbodata.be/def#"
	xmlns:locn="http://www.w3.org/ns/locn#" xmlns:org="http://www.w3.org/ns/org#"
	xmlns:oslo="http://purl.org/oslo/ns/localgov#" xmlns:owl="http://www.w3.org/2002/07/owl#"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:rov="http://www.w3.org/ns/regorg#"
	xmlns:schema="http://schema.org/" xmlns:skos="http://www.w3.org/2004/02/skos/core#"
	xmlns:vcard="http://www.w3.org/2006/vcard/ns#" xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:void="http://rdfs.org/ns/void#"
	xmlns:prov="http://www.w3.org/ns/prov#" xmlns:nk="http://1060.org"
	xmlns:pt="http://www.proxml.be/xpath/functions/" xmlns:dcterms="http://purl.org/dc/terms/"
	exclude-result-prefixes="foaf kbo locn org oslo owl rdf rdfs rov schema skos vcard xsd xsl void prov nk dcterms pt"
	version="2.0">
	<xsl:output indent="yes" method="xhtml"/>
	<xsl:param name="url" nk:class="java.lang.String" />
	
	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:value-of select="concat('KBO:', 'Fragment Server')"/>
				</title>
				<meta name="viewport"
					content="width=device-width, minimum-scale=1.0, maximum-scale=1.0"/>
				<style type="text/css">
					@import url(/css/page.css);
					@import url(/css/resource.css);
					@import url(/css/responsive.css);</style>
				<link rel="shortcut icon" href="/img/favicon.png"/>
			</head>
			<body>
				<div id="header">
					<div id="logo">
						<a href="">
							<span>KBO DATA</span>
						</a>
					</div>
				</div>
				<div id="content">
					<xsl:for-each select="rdf:RDF/rdf:Description[not(contains(@rdf:about,'uuid'))][not(contains(@rdf:about,$url))]">
						<xsl:variable name="subject">
							&lt;<xsl:value-of select="@rdf:about"/>&gt;
						</xsl:variable>
						<xsl:for-each select="*">
							<xsl:variable name="predicate">
								&lt;<xsl:value-of select="concat(namespace-uri(),local-name())"/>&gt;
							</xsl:variable>
							<xsl:variable name="object">
								<xsl:if test="@rdf:resource">
									&lt;<xsl:value-of select="@rdf:resource"/>&gt;
								</xsl:if>
								<xsl:if test="not(@rdf:resource)">
									"<xsl:value-of select="."/>"
								</xsl:if>
							</xsl:variable>
							<xsl:variable name="language">
								<xsl:if test="@xml:lang">
									<xsl:value-of select="concat('@',@xml:lang)"/>
								</xsl:if>
							</xsl:variable>
							<div><xsl:value-of select="$subject"/> - <xsl:value-of select="$predicate"/> - <xsl:value-of select="$object"/><xsl:value-of select="$language"/></div>
						</xsl:for-each>
					</xsl:for-each>
				</div>
				<div id="footer">
					<div>
						<span class="copyright">
							<span>© 2014</span>
							<a class="proxml" href="http://www.proxml.be"><span>ProXML</span></a>
							<a class="elephantbird" href="http://netkernelbook.org"><span>Elephant Bird Consulting</span></a>
						</span>
						<span class="powered-by">
							<a class="netkernel" href="http://www.1060research.com"><span>NetKernel</span></a>
							<a class="stardog" href="http://stardog.com"><span>Stardog</span></a>
						</span>
					</div>
				</div>
				<script type="text/javascript" src="/lib/jquery/jquery-1.11.0.min.js"></script>
				<script type="text/javascript" src="/lib/jquery/jquery.cookie.js"></script>
				<script type="text/javascript" src="/js/kbodata.js"></script>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
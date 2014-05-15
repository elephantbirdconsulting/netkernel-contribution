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
	<xsl:output indent="yes" method="xhtml" encoding="UTF-8"/>
	<xsl:variable name="list"/>
	<xsl:key name="label" match="rdf:Description" use="@rdf:about"/>
	<xsl:function name="pt:q2uri">
		<xsl:param name="q"/>
		<xsl:sequence select="concat(namespace-uri($q),local-name($q))"/>
	</xsl:function>
	<xsl:template name="atomiclangrow">
		<xsl:param name="key" as="node()"/>
		<div class="predicate">
			<a class="label" href="{pt:q2uri($key)}">
				<xsl:value-of select="local-name($key)"/>
			</a>
			<div class="objects">
				<p>
					<xsl:if test="$key[@xml:lang]">
						<xsl:attribute name="xml:lang">
							<xsl:value-of select="$key/@xml:lang"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="normalize-space($key)"/>
				</p>
				<xsl:for-each select="$key/following-sibling::*[name(.)=name($key)]">
					<p>
						<xsl:if test="@xml:lang">
							<xsl:attribute name="xml:lang">
								<xsl:value-of select="@xml:lang"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:value-of select="normalize-space(.)"/>
					</p>
				</xsl:for-each>
			</div>
			
		</div>
	</xsl:template>
	
	<xsl:template match="/">
		<xsl:apply-templates select="rdf:RDF/rdf:Description[rdf:type]"/>
	</xsl:template>
	<xsl:template match="rdf:Description[rdf:type]">
		<xsl:variable name="identifier">
			<xsl:value-of select="descendant::rdf:Description[rdf:type]/@rdf:about"/>
		</xsl:variable>
		<xsl:variable name="docid">
			<xsl:value-of select="substring-before(@rdf:about,'#')"/>
		</xsl:variable>
		<html>
			<head>
				<title>
					<xsl:value-of select="concat('KBO:', rdfs:label)"/>
				</title>
				<meta name="viewport"
					content="width=device-width, minimum-scale=1.0, maximum-scale=1.0"/>
				<link rel="alternate" type="application/rdf+xml" href="{$docid}.rdf"/>
				<link rel="alternate" type="text/turtle" href="{$docid}.ttl"/>
				<link rel="alternate" type="text/plain" href="{$docid}.nt"/>
				<link rel="alternate" type="application/ld+json" href="{$docid}.jsonld"/>
				<style type="text/css">
					@import url(/css/page.css);
					@import url(/css/resource.css);
					@import url(/css/responsive.css);
					@import url(/css/print.css);</style>
				<link rel="shortcut icon" href="/img/favicon.png"/>
			</head>
			<body>
				<div id="header">
					<div id="logo">
						<a href="/">
							<span>KBO DATA</span>
						</a>
					</div>
					<div id="lang-nav">
						<a href="#nl" data-lang="nl">NL</a>
						<a href="#fr" data-lang="fr">FR</a>
						<a href="#de" data-lang="de">DE</a>
					</div>
				</div>
				<div id="content">
					<div class="export-options">
						<a href="">HTML</a>
						<a href="{$docid}.jsonld">JSON-LD</a>
						<a href="{$docid}.ttl">TURTLE</a>
						<a href="{$docid}.nt">N-TRIPLES</a>
						<a href="{$docid}.rdf">XML</a>
					</div>
					<h1>
						<xsl:choose>
							<xsl:when test="rdfs:label[@xml:lang]">
								<xsl:for-each select="rdfs:label[@xml:lang]">
									<span xml:lang="{@xml:lang}">
										<xsl:value-of select="normalize-space(.)"/>
									</span>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="rdfs:label"/>
							</xsl:otherwise>
						</xsl:choose>
					</h1>
					<div class="properties">
						<xsl:for-each select="*[@xml:lang]">
							<xsl:call-template name="atomiclangrow">
								<xsl:with-param name="key" select="."/>
							</xsl:call-template>
						</xsl:for-each>
					</div>
					<div class="links outbound">
						<xsl:for-each-group select="*[@rdf:resource][not(contains(@rdf:resource,'uuid'))]" group-by="local-name(.)">
							<xsl:sort select="local-name(.)"/>
							<div class="predicate">
								<a class="label" href="{pt:q2uri(.[1])}">
									<xsl:value-of select="local-name(.[1])"/>
								</a>
								<div class="objects">
									<xsl:for-each select="current-group()">
											<a href="{@rdf:resource}">
												<xsl:value-of select="@rdf:resource"/>
											</a>
											
									</xsl:for-each>
								</div>
							</div>
						</xsl:for-each-group>
					</div>
					
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

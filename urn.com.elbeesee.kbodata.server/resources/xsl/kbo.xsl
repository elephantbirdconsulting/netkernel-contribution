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
	<xsl:key name="label" match="rdf:Description" use="@rdf:about"/>
	<xsl:function name="pt:q2uri">
		<xsl:param name="q"/>
		<xsl:sequence select="concat(namespace-uri($q),local-name($q))"/>
	</xsl:function>
	<xsl:template name="tablerow">
		<xsl:param name="key" as="node()"/>
		<xsl:param name="nace"/>
		<xsl:choose>
			<xsl:when test="$key[@rdf:resource]">
				<xsl:call-template name="pointertablerow">
					<xsl:with-param name="key" select="$key"/>
					<xsl:with-param name="nace" select="$nace"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$key[@xml:lang]">
				<xsl:call-template name="atomiclangrow">
					<xsl:with-param name="key" select="$key"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="atomictablerow">
					<xsl:with-param name="key" select="$key"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="atomictablerow">
		<xsl:param name="key" as="node()"/>
		<div class="predicate">
			<a class="label" href="{pt:q2uri($key)}">
				<xsl:value-of select="local-name($key)"/>
			</a>
			<div class="objects">
				<xsl:for-each select="$key">
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
	<xsl:template name="pointertablerow">
		<xsl:param name="key" as="node()"/>
		<xsl:param name="nace"/>
		<div class="predicate">
			<a class="label" href="{pt:q2uri($key)}">
				<xsl:value-of select="local-name($key)"/>
				<!--<xsl:if test="string-length($nace) > 1">
						<xsl:text> NACE </xsl:text>
						<xsl:value-of select="$nace"/>
					</xsl:if>-->
			</a>
			<xsl:for-each select="$key">
				<xsl:choose>
					<xsl:when
						test="//rdf:Description[@rdf:about = current()/@rdf:resource]/rdfs:label">
						<div class="objects">
							<xsl:for-each
								select="//rdf:Description[@rdf:about = current()/@rdf:resource]/rdfs:label">

								<a href="{../@rdf:about}">
									<xsl:if test="@xml:lang">
										<xsl:attribute name="xml:lang">
											<xsl:value-of select="@xml:lang"/>
										</xsl:attribute>
									</xsl:if>
									<xsl:value-of select="normalize-space(.)"/>
								</a>

							</xsl:for-each>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div class="objects">
							<a href="{@rdf:resource}">
								<xsl:value-of select="@rdf:resource"/>
							</a>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</div>
	</xsl:template>
	<xsl:template name="multiplepointerrow">
		<xsl:param name="key" as="node()+"/>
		<div class="predicate">
			<a class="label" href="{pt:q2uri($key[1])}">
				<xsl:value-of select="local-name($key[1])"/>
			</a>
			<div class="objects">
				<!--<xsl:for-each select="$key">
					<xsl:for-each
						select="//rdf:Description[@rdf:about = current()/@rdf:resource]/rdfs:label">
						<a href="{../@rdf:about}">
							<xsl:if test="@xml:lang">
								<xsl:attribute name="xml:lang">
									<xsl:value-of select="@xml:lang"/>
								</xsl:attribute>
							</xsl:if>
							<xsl:value-of select="normalize-space(.)"/>
						</a>
					</xsl:for-each>
				</xsl:for-each>-->
				<xsl:for-each
					select="//rdf:Description[@rdf:about = $key/@rdf:resource]/rdfs:label">
					<xsl:sort select="."/>
					<a href="{../@rdf:about}">
						<xsl:if test="@xml:lang">
							<xsl:attribute name="xml:lang">
								<xsl:value-of select="@xml:lang"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:value-of select="normalize-space(.)"/>
					</a>
				</xsl:for-each>
			</div>
		</div>
	</xsl:template>
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
					<xsl:choose>
						<xsl:when test="org:identifier">
							<xsl:value-of select="concat('KBO:', org:identifier)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat('KBO:', rdfs:label[1])"/>
						</xsl:otherwise>
					</xsl:choose>
					
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
						<a href="http://creativecommons.org/publicdomain/zero/1.0/">
							<img src="/img/cc-zero-80x15.png"
								alt="License: CC Public DOmain Zero 1.0"/>
						</a>
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
						<xsl:if test="skos:prefLabel">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="skos:prefLabel[1]"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="skos:altLabel">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="skos:altLabel[1]"/>
							</xsl:call-template>
						</xsl:if>

						<xsl:if test="org:identifier">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="org:identifier"/>
							</xsl:call-template>
						</xsl:if>


						<xsl:if test="schema:startDate">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="schema:startDate"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="vcard:hasURL">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="vcard:hasURL"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="vcard:hasTelephone">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="vcard:hasTelephone"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="vcard:hasEmail">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="vcard:hasEmail"/>
							</xsl:call-template>
						</xsl:if>

						<xsl:if test="locn:thoroughfare">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="locn:thoroughfare[1]"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="locn:locatorDesignator">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="locn:locatorDesignator"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="locn:postCode">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="locn:postCode"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="locn:postName">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="locn:postName[1]"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="locn:fullAddress">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="locn:fullAddress[1]"/>
							</xsl:call-template>
						</xsl:if>
					</div>
					<div class="links outbound">
						<xsl:if test="rdf:type">
							<xsl:for-each select="rdf:type">
								<xsl:call-template name="tablerow">
									<xsl:with-param name="key" select="."/>
								</xsl:call-template>
							</xsl:for-each>
						</xsl:if>
						<xsl:if test="skos:inScheme">
							<xsl:for-each select="skos:inScheme">
								<xsl:call-template name="tablerow">
									<xsl:with-param name="key" select="."/>
								</xsl:call-template>
							</xsl:for-each>
						</xsl:if>
						<xsl:if test="kbo:orgMainActivity">
							<xsl:call-template name="multiplepointerrow">
								<xsl:with-param name="key" select="kbo:orgMainActivity"/>
							</xsl:call-template>

						</xsl:if>
						<xsl:if test="rov:orgActivity">
							<xsl:call-template name="multiplepointerrow">
								<xsl:with-param name="key" select="rov:orgActivity"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="dcterms:type">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="dcterms:type"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="rov:orgType">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="rov:orgType"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="rov:orgStatus">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="rov:orgStatus"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="oslo:companyStatus">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="oslo:companyStatus"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="locn:address">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="locn:address"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="org:siteOf">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="org:siteOf"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="org:hasSite">
							<xsl:call-template name="multiplepointerrow">
								<xsl:with-param name="key" select="org:hasSite"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="dcterms:isPartOf">
							<xsl:call-template name="tablerow">
								<xsl:with-param name="key" select="dcterms:isPartOf"/>
							</xsl:call-template>
						</xsl:if>
					</div>
					<xsl:if test="/descendant::rdf:Description[not(rdf:type)]">
						<h2 class="links-heading">References from other resources</h2>
						<div class="links inbound">
							<xsl:for-each-group
								select="/descendant::rdf:Description[not(rdf:type)]/*[@rdf:resource and namespace-uri() != 'http://purl.org/dc/terms/']"
								group-by="node-name(.)">
								<!--<xsl:sort select="current-grouping-key()"/>-->
								<div class="predicate">
									<a class="label" href="{concat(namespace-uri(.),local-name(.))}">
										<xsl:value-of select="current-grouping-key()"/>
									</a>
									<div class="objects">
										<xsl:variable name="list" as="node()*">
											<xsl:for-each select="current-group()">
												<xsl:for-each select="preceding-sibling::rdfs:label">
												  <xsl:copy>
												  	<xsl:copy-of select="@*"/>
												  	<xsl:attribute name="parentid" select="parent::rdf:Description/@rdf:about"/>
												  	<xsl:attribute name="value" select="text()"/>
												  </xsl:copy>
												</xsl:for-each>
											</xsl:for-each>
										</xsl:variable>
										<xsl:for-each select="$list">
											<xsl:sort select="@value"/>
											<a href="{@parentid}" xml:lang="{@xml:lang}">
												<xsl:value-of select="@value"/>
											</a>
										</xsl:for-each>
									</div>
								</div>
							</xsl:for-each-group>
						</div>
					</xsl:if>
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

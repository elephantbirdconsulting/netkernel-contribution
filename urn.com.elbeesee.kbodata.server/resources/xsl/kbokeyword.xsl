<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:sp="http://www.w3.org/2005/sparql-results#"
	xmlns:nk="http://1060.org"
	exclude-result-prefixes="xs sp nk"
	version="2.0">
	<xsl:output indent="yes" method="xhtml" encoding="UTF-8"/>
	<xsl:param name="keyword" nk:class="java.lang.String" />

	<xsl:template match="sp:sparql">
		<html>
			<head>
				<title>
					<xsl:value-of select="$keyword"/>
				</title>
				<meta name="viewport"
					content="width=device-width, minimum-scale=1.0, maximum-scale=1.0"/>
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
				</div>
				<div id="content">
					<div class="export-options">
					</div>
					<h1><span>Search results for <xsl:value-of select="$keyword"/></span></h1>
					
					<table>
						<tr>
							<th>label</th>
							<th>identifier</th>
							<th>score</th>
						</tr>
						<xsl:for-each select="sp:results/sp:result">
							<xsl:variable name="id">
								<xsl:value-of select="normalize-space(sp:binding[@name='id'])"></xsl:value-of>
							</xsl:variable>
							<xsl:variable name="label">
								<xsl:value-of select="normalize-space(sp:binding[@name='label'])"/>
							</xsl:variable>
							<tr>
								<td><xsl:value-of select="$label"/></td>
								<td><a href="{$id}"><xsl:value-of select="$id"/></a></td>
								<td><xsl:value-of select="normalize-space(sp:binding[@name='score'])"/></td>
							</tr>
						</xsl:for-each>
					</table>
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
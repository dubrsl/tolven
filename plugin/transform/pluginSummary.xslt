<?xml version="1.0" encoding="UTF-8"?>
<!--
###################################################### 

############################################################## -->
<xsl:transform
	xmlns:p="urn:tolven-org:plugins:1.0"
	xmlns="http://www.w3.org/TR/xhtml1/strict"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="2.0">
	<xsl:output method="html"  indent="yes" encoding="UTF-8"/>
	<xsl:variable name="now" select="current-dateTime()"/>
	<xsl:param name="repositoryRuntime" />
	<xsl:template match="/">
		<html>
			<head>
				<title>
					Plugin RepositoryRuntime list
				</title>
				<link href="http://tolven.org/css/tolven.css" rel="stylesheet" type="text/css" />
				<style type="text/css"  media="screen">
					table thead tr td {	font-weight: bold;}
					h3 { font-size: 120%; font-weight: bold;}
				</style>
			</head>
			<body>
				<h3>Repository Runtime 
				<xsl:value-of select="format-dateTime($now, '[MNn] [D], [Y], [H]:[m]')"/></h3>
				<p>List of plugins actually selected for use in 
				<xsl:value-of select="$repositoryRuntime"/><br/></p>
				<table>
					<thead>
						<tr><td>Id</td><td>Version</td><td>Downloaded from</td></tr>
					</thead>
					<xsl:apply-templates select="p:plugins"/>
				</table>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="p:plugins">
		<xsl:apply-templates select="p:plugin">
			 <xsl:sort select="@id"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="p:plugin">
			<tr>
				<td><xsl:value-of select="@id"/></td>
				<td><xsl:value-of select="@useVersion"/></td>
				<td><xsl:value-of select="p:version/p:uri"/></td>
			</tr>
	</xsl:template>
</xsl:transform>
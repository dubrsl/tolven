<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="context"/>
<xsl:variable name="ctx" select="document('context')"/>
<xsl:variable name="pat" select="document($context)"/>
<xsl:variable name="meds" select="document('$ctx:medications:active')"/>
<xsl:variable name="allergies" select="document('$ctx:allergies:current')"/>
<xsl:template match="/">
	<foo>
		<xsl:value-of select="$allergies/results/rows"/>
	</foo>
		<!--<tbody> <xsl:apply-templates select="$allergies/results/rows"/>-->
		<!-- <xsl:template match="/row/Allergy">
			<tr ID="ALGSUMMARY_1"></tr>
			<tr ID="ALGSUMMARY_1">
				<td ID="ALGTYPE_1">Drug Allergy</td>
				<td ID="ALGSUB_1"><xsl:value-of select="."/></td>
				<td ID="ALGREACT_1">Hives</td>
				<td ID="ALGSTATUS_1">Active</td>
			</tr>
		</xsl:template> -->
	<!-- 	<tr ID="ALGSUMMARY_1">
			<td ID="ALGTYPE_1">Drug Allergy</td>
			<td ID="ALGSUB_1">Penicillin</td>
			<td ID="ALGREACT_1">Hives</td>
			<td ID="ALGSTATUS_1">Active</td>
		</tr>
		<tr ID="ALGSUMMARY_2">
			<td ID="ALGTYPE_2">Drug Intolerance</td>
			<td ID="ALGSUB_2">Aspirin</td>
			<td ID="ALGREACT_2">Wheezing</td>
			<td ID="ALGSTATUS_2">Active</td>
		</tr>
		<tr ID="ALGSUMMARY_3">
			<td ID="ALGTYPE_3">Drug Intolerance</td>
			<td ID="ALGSUB_3">Codeine</td>
			<td ID="ALGREACT_3">Nausea</td>
			<td ID="ALGSTATUS_3">Active</td>
		</tr> </tbody>-->
	
</xsl:template>
</xsl:stylesheet>
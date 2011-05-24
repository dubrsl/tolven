<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:n3="http://www.w3.org/1999/xhtml"
	xmlns:n1="urn:hl7-org:v3"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
>
<xsl:output method="xml" indent="yes" version="4.01" encoding="UTF-8"/>

<xsl:variable name="title">
    <xsl:choose>
         <xsl:when test="/n1:ClinicalDocument/n1:title">DEMO <xsl:value-of select="/n1:ClinicalDocument/n1:title"/></xsl:when>
         <xsl:otherwise>DEMO Clinical Document</xsl:otherwise>
    </xsl:choose>
</xsl:variable>

<xsl:template match="/">
    <xsl:comment>Below is the clinical document</xsl:comment>
    <xsl:apply-templates select="n1:ClinicalDocument"/>
</xsl:template>

<xsl:template match="n1:ClinicalDocument">
<trim:trim
	xmlns:trim="urn:tolven-org:trim:4.0"> 
	<act classCode="DOC" moodCode="EVN">
		<participation name="subject" typeCode="SBJ">
			<role classCode="PAT">
				<bind application="echr">
					<placeholder>
						<path>echr:patient</path>
					</placeholder>
				</bind>
				<bind application="ephr">
					<placeholder>
						<path>ephr:patient</path>
					</placeholder>
				</bind>
				<id>
					<II>
						<root>#{computeIDRoot(account)}</root>
						<extension>#{patient.path}</extension>
					</II>
					<II>
						<root>MRN</root>
            			<extension><xsl:value-of select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:id/@extension"/></extension>
					</II>
				</id>
				<player classCode="PSN" determinerCode="INSTANCE">
					<name>
						<label>Patient Name</label>
						<EN>
							<label>Legal Name</label>
							<use>L</use>
							<xsl:call-template name="getName">
                        		<xsl:with-param name="name" select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:name"/>
                    		</xsl:call-template>
						</EN>
					</name>
					<livingSubject>
						<administrativeGenderCode>
							<label>Geslacht</label>
			                <xsl:variable name="sex" select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:administrativeGenderCode/@code"/>
			                <xsl:choose>
			                    <xsl:when test="$sex='M'">
			                		<CE>
										<displayName>Male</displayName>
										<code>C0024554</code>
										<codeSystem>2.16.840.1.113883.6.56</codeSystem>
										<codeSystemVersion>2007AA</codeSystemVersion>
									</CE>
			                    </xsl:when>
			                    <xsl:when test="$sex='F'">
									<CE>
										<displayName>Female</displayName>
										<code>C0015780</code>
										<codeSystem>2.16.840.1.113883.6.56</codeSystem>
										<codeSystemVersion>2007AA</codeSystemVersion>
									</CE>
			                    </xsl:when>
			                </xsl:choose>
						</administrativeGenderCode>
						<birthTime>
							<label>Geboortedatum</label>
							<TS><value><xsl:value-of select="/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:birthTime/@value"/></value></TS>
						</birthTime>
					</livingSubject>
				</player>
			</role>
		</participation>
		<participation name="dataEnterer" typeCode="ENT">
			<role classCode="ROL">
				<id>
					<II>
						<root>#{computeIDRoot(account)}</root>
						<extension>#{user.ldapUID}</extension>
					</II>
				</id>
			</role>
		</participation>
	</act>
</trim:trim>
</xsl:template>

<xsl:template name="getName">
	<xsl:param name="name"/>
	<xsl:choose>
		<xsl:when test="$name/n1:given">
				<part>
					<label>First Name</label>
					<type>GIV</type>
					<ST><xsl:value-of select="$name/n1:given"/></ST>
				</part>
		</xsl:when>
		<xsl:when test="$name/n1:family">
				<part>
					<label>Last Name</label>
					<type>FAM</type>
					<ST><xsl:value-of select="$name/n1:family"/></ST>
				</part>
		</xsl:when>
	</xsl:choose>
</xsl:template>


</xsl:stylesheet>

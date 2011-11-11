<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:hl7-org:v3 http://xreg2.nist.gov:8080/hitspValidation/schema/cdar2c32/infrastructure/cda/C32_CDA.xsd">
<xsl:param name="context"/>
<xsl:variable name="ctx" select="document('context')"/>
<xsl:variable name="pat" select="document($context)"/>
<xsl:variable name="allergies_active"	select="document(concat($context,':allergies:active'))" />
<xsl:variable name="allergies_inactive"	select="document(concat($context,':allergies:inactive'))" />
<xsl:variable name="problems_active" select="document(concat($context,':problems:active'))"/>
<xsl:variable name="problems_inactive" select="document(concat($context,':problems:inactive'))"/>
<xsl:variable name="problems_resolved" select="document(concat($context,':problems:completed'))"/>
<xsl:variable name="meds" select="document(concat($context,':medications:active'))" />
<xsl:variable name="procedures" select="document(concat($context,':procedures:pxList'))"/>
<xsl:variable name="labresults" select="document(concat($context,':labresults:completed'))"/>

<xsl:variable name="totalActiveAllergyNodes" select="count($allergies_active/results/rows/child::node())" />
<xsl:variable name="totalActiveProblemNodes" select="count($problems_active/results/rows/child::node())" />
<xsl:variable name="totalMedicationNodes" select="count($meds/results/rows/child::node())" />
<xsl:variable name="totalProcedureNodes" select="count($procedures/results/rows/child::node())" />
<xsl:variable name="totalLabResultNodes" select="count($labresults/results/rows/child::node())" />

<xsl:template match="/">
<ClinicalDocument xmlns="urn:hl7-org:v3" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:hl7-org:v3 http://xreg2.nist.gov:8080/hitspValidation/schema/cdar2c32/infrastructure/cda/C32_CDA.xsd">
	<realmCode code="US"/>
	<typeId root="2.16.840.1.113883.1.3" extension="POCD_HD000040"/>
	<templateId root="2.16.840.1.113883.3.27.1776" assigningAuthorityName="CDA/R2"/>
	<templateId root="2.16.840.1.113883.10.20.3" assigningAuthorityName="HL7/CDT Header"/>
	<templateId root="1.3.6.1.4.1.19376.1.5.3.1.1.1" assigningAuthorityName="IHE/PCC"/>
	<templateId root="2.16.840.1.113883.3.88.11.32.1" assigningAuthorityName="HITSP/C32"/>
	<id root="2.16.840.1.113883.3.72" extension="MU_Rev2_HITSP_C32C83_4Sections_MeaningfulEntryContent_NoErrors" assigningAuthorityName="NIST Healthcare Project"/>	
	<code code="34133-9" displayName="Patient Clinical Summary" codeSystem="2.16.840.1.113883.6.1" codeSystemName="LOINC"/>
	<title/>
	<effectiveTime>
		<xsl:attribute name="value"><xsl:value-of select="substring-before(translate(translate(translate($ctx/context/@now, '-', ''), ':', ''), 'T', ''), '.')" /></xsl:attribute>
	</effectiveTime>
	<confidentialityCode/>
	<languageCode code="en-US"/>
	<recordTarget>
		<patientRole>
			<!-- <id root="ProviderID" extension="PatientID" assigningAuthorityName="Provider Name"/> -->
			 <xsl:for-each select="$pat/results/rows/row/placeholderIds/id">
			 	<id>
			 		<xsl:attribute name="root">
						<xsl:value-of select="@root"/>
					</xsl:attribute>
					<xsl:attribute name="extension">
						<xsl:value-of select="@extension"/>
					</xsl:attribute>
				</id>
			</xsl:for-each>			 
			<addr use="HP">
				<!--HITSP/C83 recommends that a patient have at least one address element with a use attribute of HP, i.e. home primary-->
<!-- None of the following address elements are specifically required by HITSP C32, so the address could be be just plain text at this location? Use of one or more of the following labeled address lines, with meaningful content, is recommended. -->
				<streetAddressLine><xsl:value-of select="$pat/results/rows/row/homeAddr1"/></streetAddressLine>
				<streetAddressLine><xsl:value-of select="$pat/results/rows/row/homeAddr2"/></streetAddressLine>
				<city><xsl:value-of select="$pat/results/rows/row/homeCity"/></city>
				<state><xsl:value-of select="$pat/results/rows/row/homeState"/></state>
				<postalCode><xsl:value-of select="$pat/results/rows/row/homeZip"/></postalCode>
				<country><xsl:value-of select="$pat/results/rows/row/homeCountry"/></country>
			</addr>
			<telecom>
				<xsl:attribute name="value"><xsl:value-of select="concat('tel:', $pat/results/rows/row/homeTelecom)"/></xsl:attribute>
				<xsl:attribute name="use">H</xsl:attribute>
				</telecom>
			<patient>
				<name>
					<given><xsl:value-of select="$pat/results/rows/row/firstName"/></given>
					<given><xsl:value-of select="$pat/results/rows/row/middleName"/></given>
					<!-- <family><xsl:value-of select="$pat/familyName"/></family> -->
					<family><xsl:value-of select="$pat/results/rows/row/lastName"/></family>
				</name>
				<!--HITSP/C83 requires patient administrative gender, e.g. M, F, I (indeterminate)-->
				<administrativeGenderCode codeSystem="2.16.840.1.113883.5.1" codeSystemName="HL7 AdministrativeGender">
					<xsl:attribute name="displayName">
		 				<xsl:value-of select="$pat/results/rows/row/gender" />
					</xsl:attribute>
					<xsl:attribute name="code">
						<xsl:value-of select="substring($pat/results/rows/row/gender, 1, 1)" />
					</xsl:attribute>
				</administrativeGenderCode>
				<birthTime>
					<xsl:attribute name="value">
		 				<xsl:value-of select="concat(substring($pat/results/rows/row/dob, 1,4), substring($pat/results/rows/row/dob, 6,2), substring($pat/results/rows/row/dob, 9,2))" />
					</xsl:attribute>				
				</birthTime>
				<!--HITSP/C83 requires patient marital status - if known, e.g. S, M, D-->
				<!--HITSP/C32 requires patient languages spoken - if known.-->
				<languageCommunication>
					<templateId root="2.16.840.1.113883.3.88.11.83.2" assigningAuthorityName="HITSP/C83"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.2.1" assigningAuthorityName="IHE/PCC"/>
					<languageCode code="en-US">
						<!-- <xsl:value-of select="$pat/results/rows/row/language" /> -->
					</languageCode>
				</languageCommunication>
			</patient>
		</patientRole>
	</recordTarget>
	<author>
		<time>
			<xsl:attribute name="value"><xsl:value-of select="substring-before(translate(translate(translate($ctx/context/@now, '-', ''), ':', ''), 'T', ''), '.')" /></xsl:attribute>
		</time>
		<assignedAuthor>
			<id/>
			<addr/>
			<telecom/>
			<assignedPerson>
				<name>Staff</name>
			</assignedPerson>
			<representedOrganization>
				<name>Tolven 2.1 Health Record</name>
				<telecom/>
				<addr/>
			</representedOrganization>
		</assignedAuthor>
	</author>
	<custodian>
		<assignedCustodian>
			<representedCustodianOrganization>
				<id/>
				<name/>
				<telecom/>
				<addr/>
			</representedCustodianOrganization>
		</assignedCustodian>
	</custodian>
	<!--HITSP/C32 requires one or more support modules (i.e. participant) - if known; if not known, the participant element(s) may be removed in their entirety. However, many medical facilities require recording of the Next-of-Kin (NOK). The following shows how to represent that information in the participant element.-->
	<participant typeCode="IND">
		<templateId root="2.16.840.1.113883.3.88.11.83.3" assigningAuthorityName="HITSP/C83"/>
		<templateId root="1.3.6.1.4.1.19376.1.5.3.1.2.4" assigningAuthorityName="IHE/PCC"/>
		<time value="2010"/>
		<associatedEntity classCode="NOK">
			<code code="MTH" displayName="Mother" codeSystem="2.16.840.1.113883.5.111" codeSystemName="HL7 RoleCode (Personal Relationship subset)"/>
			<addr/>
			<telecom/>
			<associatedPerson>
				<name>NextOfKin(NOK) Name</name>
			</associatedPerson>
		</associatedEntity>
	</participant>
	<component>
		<structuredBody>
			<component>
				<!--Allergies-->
				<section>
					<templateId root="2.16.840.1.113883.3.88.11.83.102" assigningAuthorityName="HITSP/C83"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.3.13" assigningAuthorityName="IHE PCC"/>
					<templateId root="2.16.840.1.113883.10.20.1.2" assigningAuthorityName="HL7 CCD"/>
					<!--Allergies/Reactions section template-->
					<code code="48765-2" codeSystem="2.16.840.1.113883.6.1" codeSystemName="LOINC" displayName="Allergies"/>
					<title>Allergies and Adverse Reactions</title>
					<text>
						<table border="1" width="100%">						
							<thead>
								<tr>
									<th>SNOMED Allergy Type Code</th>
									<th>Medication/Agent Allergy</th>
									<th>Reaction</th>
									<th>Adverse Event Date</th>
								</tr>
							</thead>
							<tbody>
								<xsl:if test="$totalActiveAllergyNodes > 0">
									<xsl:call-template name="AllergySummary">
										<xsl:with-param name="allergyNode" select="$allergies_active/results/rows"/>
										<xsl:with-param name="totalActiveAllergyNodes" select="$totalActiveAllergyNodes"/>
										<xsl:with-param name="allergyNodeCnt" select="1"/>
									</xsl:call-template>	
								</xsl:if>								
							</tbody>
						</table>
					</text>
					<xsl:if test="$totalActiveAllergyNodes > 0">
						<xsl:call-template name="AllergyDetail">
							<xsl:with-param name="allergyDetailNode" select="$allergies_active/results/rows"/>
							<xsl:with-param name="totalActiveAllergyNodes" select="$totalActiveAllergyNodes"/>
							<xsl:with-param name="allergyDetailNodeCnt" select="1"/>
						</xsl:call-template>	
					</xsl:if>					
				</section>
			</component>
			<component>
				<!--Problems-->
				<section>
					<templateId root="2.16.840.1.113883.3.88.11.83.103" assigningAuthorityName="HITSP/C83"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.3.6" assigningAuthorityName="IHE PCC"/>
					<templateId root="2.16.840.1.113883.10.20.1.11" assigningAuthorityName="HL7 CCD"/>
					<!--Problems section template-->
					<code code="11450-4" codeSystem="2.16.840.1.113883.6.1" codeSystemName="LOINC" displayName="Problem list"/>
					<title>Problems</title>
					<text>
						<table border="1" width="100%">
							<thead>
								<tr>
									<th>SNOMED Code</th>
									<th>SNOMED Description of Problem</th>									
									<th>Status</th>
									<th>Date Diagnosed</th>
								</tr>
							</thead>
							<tbody>
								<xsl:if test="$totalActiveProblemNodes > 0">
									<xsl:call-template name="ProblemSummary">
										<xsl:with-param name="problemNode" select="$problems_active/results/rows"/>
										<xsl:with-param name="totalActiveProblemNodes" select="$totalActiveProblemNodes"/>
										<xsl:with-param name="problemNodeCnt" select="1"/>
									</xsl:call-template>	
								</xsl:if>								
							</tbody>
						</table>
					</text>
					<xsl:if test="$totalActiveProblemNodes > 0">
						<xsl:call-template name="ProblemDetail">
							<xsl:with-param name="problemDetailNode" select="$problems_active/results/rows"/>
							<xsl:with-param name="totalActiveProblemNodes" select="$totalActiveProblemNodes"/>
							<xsl:with-param name="problemDetailNodeCnt" select="1"/>
						</xsl:call-template>	
					</xsl:if>					
				</section>
			</component>
			<component>
				<!--Medications-->
				<section>
					<templateId root="2.16.840.1.113883.3.88.11.83.112" assigningAuthorityName="HITSP/C83"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.3.19" assigningAuthorityName="IHE PCC"/>
					<templateId root="2.16.840.1.113883.10.20.1.8" assigningAuthorityName="HL7 CCD"/>
					<!--Medications section template-->
					<code code="10160-0" codeSystem="2.16.840.1.113883.6.1" codeSystemName="LOINC" displayName="History of medication use"/>
					<title>Medications</title>
					<text>
						<table border="1" width="100%">
							<thead>
								<tr>
									<th>RxNorm Code</th>
									<th>Medication Name</th>
									<th>Brand Name</th>
									<th>Strength/Dose/Form/Route</th>
									<th>Frequency</th>
									<th>Sig Text</th>
									<th>Date Started</th>
									<th>Status</th>
								</tr>
							</thead>
							<tbody>
								<xsl:if test="$totalMedicationNodes > 0">
									<xsl:call-template name="MedicationSummary">
										<xsl:with-param name="medNode" select="$meds/results/rows"/>
										<xsl:with-param name="totalMedicationNodes" select="$totalMedicationNodes"/>
										<xsl:with-param name="medNodeCnt" select="1"/>
									</xsl:call-template>	
								</xsl:if>							
							</tbody>
						</table>
					</text>
					<xsl:if test="$totalMedicationNodes > 0">
						<xsl:call-template name="MedicationDetail">
							<xsl:with-param name="medDetailNode" select="$meds/results/rows"/>
							<xsl:with-param name="totalMedicationNodes" select="$totalMedicationNodes"/>
							<xsl:with-param name="medDetailNodeCnt" select="1"/>
						</xsl:call-template>	
					</xsl:if>					
				</section>
			</component>
			<component>
				<!--Results-->
				<section>
					<templateId root="2.16.840.1.113883.3.88.11.83.122" assigningAuthorityName="HITSP/C83"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.3.28" assigningAuthorityName="IHE PCC"/>
					<!--Diagnostic Results section template-->
					<code code="30954-2" codeSystem="2.16.840.1.113883.6.1" codeSystemName="LOINC" displayName="Results"/>
					<title>Lab Test Results &amp; Procedures</title>
					<text>
						<table border="1" width="100%">
							<thead>								
								<tr>
									<th>LOINC Code</th>
									<th>Test</th>
									<th>Result</th>
									<th>Normal Range</th>
									<th>Abnormal Flag</th>
									<th>Date Performed</th>			
								</tr>
							</thead>
							<tbody>
								<xsl:if test="$totalLabResultNodes > 0">
									<xsl:call-template name="LabResultSummary">
										<xsl:with-param name="labResultNode" select="$labresults/results/rows"/>
										<xsl:with-param name="totalLabResultNodes" select="$totalLabResultNodes"/>
										<xsl:with-param name="labResultNodeCnt" select="1"/>
									</xsl:call-template>	
								</xsl:if>
							</tbody>
						</table>
						<table border="1" width="100%">
							<thead>
								<tr>
									<th>Procedure Code</th>
									<th>Procedure Description</th>
									<th>Status</th>
									<th>Date Performed</th>		
								</tr>
							</thead>
							<tbody>
								<xsl:if test="$totalProcedureNodes > 0">
									<xsl:call-template name="ProcedureSummary">
										<xsl:with-param name="procNode" select="$procedures/results/rows"/>
										<xsl:with-param name="totalProcedureNodes" select="$totalProcedureNodes"/>
										<xsl:with-param name="procNodeCnt" select="1"/>
									</xsl:call-template>	
								</xsl:if>
							</tbody>
						</table>
					</text>
					<!--HITSP/C83 requires Diagnostic Results to have both Procedure and Results, but gives no guidance as to how they should be related together. If Result requires a specimen, the Procedure to obtain the specimen could be under the specimen. This example simply groups a Procedure with the Results obtained from that Procedure. This is probably not the best way to do it, but satisfies the requirements of both IHE/PCC and HITSP/C83 for Diagnostic Results. -->
					<xsl:if test="$totalLabResultNodes > 0">
						<xsl:call-template name="LabResultDetail">
							<xsl:with-param name="labresultDetailNode" select="$labresults/results/rows"/>
							<xsl:with-param name="totalLabResultNodes" select="$totalLabResultNodes"/>
							<!-- <xsl:with-param name="totalProcedureNodes" select="$totalProcedureNodes"/> -->
							<xsl:with-param name="labresultDetailNodeCnt" select="1"/>
						</xsl:call-template>	
					</xsl:if>
				</section>
			</component>
		</structuredBody>
	</component>
</ClinicalDocument>
</xsl:template>

<xsl:template name="AllergySummary" xmlns="urn:hl7-org:v3">
	<xsl:param name="allergyNode"/>
	<xsl:param name="allergyNodeCnt" select="0"/>
	<xsl:param name="totalActiveAllergyNodes" select="0" />
	<xsl:variable name="allergyPath" select="document($allergyNode/child::node()[position()=$allergyNodeCnt]/drilldown)"/>
	<tr>
		<xsl:attribute name="ID">
			<xsl:value-of select="concat('ALGSUMMARY_', $allergyNodeCnt)" />
		</xsl:attribute>	
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('ALGTYPE_', $allergyNodeCnt)" />
			</xsl:attribute>	
			416098002 -- Drug Allergy (disorder)
		</td>
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('ALGSUB_', $allergyNodeCnt)" />
			</xsl:attribute>
			<!-- <xsl:value-of select="$allergyNode/Allergy" /> -->
			<xsl:value-of select="$allergyNode/child::node()[position()=$allergyNodeCnt]/Allergy" />
			
		</td>
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('ALGREACT_', $allergyNodeCnt)" />
			</xsl:attribute>						
			<xsl:value-of select="$allergyPath/results/rows/row/reactions"/>			
		</td>
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('ALGSTATUS_', $allergyNodeCnt)" />
			</xsl:attribute>			
			<xsl:variable name="allergyDate" select="$allergyPath/results/rows/row/effectiveTime"/>
			<!-- <xsl:value-of select="$allergyDate"/> -->
			<xsl:value-of select="concat(substring($allergyDate, 6,2), '/', substring($allergyDate, 9,2), '/', substring($allergyDate, 1,4))" />
		</td>
	</tr>
	 <xsl:if test="$totalActiveAllergyNodes > $allergyNodeCnt">
      <xsl:call-template name="AllergySummary">
      	<xsl:with-param name="allergyNode" select="$allergyNode"/>
        <xsl:with-param name="totalActiveAllergyNodes" select="$totalActiveAllergyNodes" />
        <xsl:with-param name="allergyNodeCnt" select="$allergyNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>


				
<xsl:template name="ProblemSummary" xmlns="urn:hl7-org:v3">
	<xsl:param name="problemNode"/>
	<xsl:param name="problemNodeCnt" select="0"/>
	<xsl:param name="totalActiveProblemNodes" select="0" />
	<xsl:variable name="problemPath" select="document($problemNode/child::node()[position()=$problemNodeCnt]/drilldown)"/>
	<tr>
		<xsl:attribute name="ID">
			<xsl:value-of select="concat('PROBSUMMARY_', $problemNodeCnt)" />
		</xsl:attribute>
		<td>
			<xsl:value-of select="$problemPath/results/rows/row/code" />
		</td>			
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('PROBKIND_', $problemNodeCnt)" />
			</xsl:attribute>	
			<xsl:value-of select="$problemPath/results/rows/row/title" />
		</td>
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('PROBSTATUS_', $problemNodeCnt)" />
			</xsl:attribute>
			<xsl:value-of select="$problemPath/results/rows/row/status" />
		</td>
		<td>
			<xsl:variable name="problemDate" select="$problemPath/results/rows/row/effectiveTimeLow"/>
			<xsl:value-of select="concat(substring($problemDate, 6,2), '/', substring($problemDate, 9,2), '/', substring($problemDate, 1,4))" />
		</td>		
	</tr>
	 <xsl:if test="$totalActiveProblemNodes > $problemNodeCnt">
      <xsl:call-template name="ProblemSummary">
      	<xsl:with-param name="problemNode" select="$problemNode"/>
        <xsl:with-param name="totalActiveProblemNodes" select="$totalActiveProblemNodes" />
        <xsl:with-param name="problemNodeCnt" select="$problemNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>				


<xsl:template name="MedicationSummary" xmlns="urn:hl7-org:v3">
	<xsl:param name="medNode"/>
	<xsl:param name="medNodeCnt" select="0"/>
	<xsl:param name="totalMedicationNodes" select="0" />
	<tr>
		<xsl:attribute name="ID">
			<xsl:value-of select="concat('MEDSUMMARY_', $medNodeCnt)" />
		</xsl:attribute>
		<td><xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/code" /></td>
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('MEDNAME_', $medNodeCnt)" />
			</xsl:attribute>	
			<xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/name" />
		</td>
		<td><xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/brand" /></td>
		<td><xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/route" /></td>
		<td><xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/frequency" /></td>
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('SIGTXT_', $medNodeCnt)" />
			</xsl:attribute>
			<xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/instructions" /></td>
		<td>
			<xsl:variable name="medStartDate" select="$medNode/child::node()[position()=$medNodeCnt]/start"/>
			<xsl:value-of select="concat(substring($medStartDate, 6,2), '/', substring($medStartDate, 9,2), '/', substring($medStartDate, 1,4))" />
			<!-- <xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/start" /> -->
			</td>
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('MEDSTATUS_', $medNodeCnt)" />
			</xsl:attribute>
			<xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/status" />
		</td>		
	</tr>
	 <xsl:if test="$totalMedicationNodes > $medNodeCnt">
      <xsl:call-template name="MedicationSummary">
      	<xsl:with-param name="medNode" select="$medNode"/>
        <xsl:with-param name="totalMedicationNodes" select="$totalMedicationNodes" />
        <xsl:with-param name="medNodeCnt" select="$medNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>


<xsl:template name="LabResultSummary" xmlns="urn:hl7-org:v3">
	<xsl:param name="labResultNode"/>
	<xsl:param name="labResultNodeCnt" select="0"/>
	<xsl:param name="totalLabResultNodes" select="0" />
<xsl:variable name="labresultPath" select="document($labResultNode/child::node()[position()=$labResultNodeCnt]/drilldown)"/>	
	<xsl:variable name="labresultDate" select="$labResultNode/child::node()[position()=$labResultNodeCnt]/Date"/>
	<!-- 	<tr>
			
			<th colspan="6">
			</th>
			<th><xsl:value-of select="substring-before($labResultNode/child::node()[position()=$labResultNodeCnt]/Date, ' ')" /></th>			
		</tr> -->
		<tr>			
			<td><xsl:value-of select="$labResultNode/child::node()[position()=$labResultNodeCnt]/code" /></td><!-- 2951-2 -->
			<td><xsl:value-of select="$labResultNode/child::node()[position()=$labResultNodeCnt]/title" /></td><!-- HGB (M 13-18 g/dl; F 12-16 g/dl) -->
			<td><xsl:value-of select="$labResultNode/child::node()[position()=$labResultNodeCnt]/Value" />
			<xsl:value-of select="$labResultNode/child::node()[position()=$labResultNodeCnt]/Units" /></td><!-- value 13.2 units -->			
			<td><xsl:value-of select="$labresultPath/results/rows/row/lowvalue" /><xsl:value-of select="$labresultPath/results/rows/row/lowunits" />-<xsl:value-of select="$labresultPath/results/rows/row/highvalue" /><xsl:value-of select="$labresultPath/results/rows/row/highunits" /></td><!-- normal range #{labresult.lowvalue}#{labresult.lowunits}-#{labresult.highvalue}#{labresult.highunits}-->
			<td><xsl:value-of select="$labresultPath/results/rows/row/interpretationCode.code" /></td><!-- interpretation code -->
			<td><xsl:value-of select="concat(substring($labresultDate, 6,2), '/', substring($labresultDate, 9,2), '/', substring($labresultDate, 1,4))" /></td>
		</tr>		
							
	 <xsl:if test="$totalLabResultNodes > $labResultNodeCnt">
      <xsl:call-template name="LabResultSummary">
      	<xsl:with-param name="labResultNode" select="$labResultNode"/>
        <xsl:with-param name="totalLabResultNodes" select="$totalLabResultNodes" />
        <xsl:with-param name="labResultNodeCnt" select="$labResultNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>

<xsl:template name="ProcedureSummary" xmlns="urn:hl7-org:v3">
	<xsl:param name="procNode"/>
	<xsl:param name="procNodeCnt" select="0"/>
	<xsl:param name="totalProcedureNodes" select="0" />
	<xsl:variable name="procedurePath" select="document($procNode/child::node()[position()=$procNodeCnt]/drilldown)"/>	
	
		<tr>
			<xsl:variable name="procDate" select="$procNode/child::node()[position()=$procNodeCnt]/Date"/>
			<td><xsl:value-of select="$procedurePath/results/rows/row/code" /></td>
			<td><xsl:value-of select="$procNode/child::node()[position()=$procNodeCnt]/Procedure" /></td>
			<td><xsl:value-of select="$procNode/child::node()[position()=$procNodeCnt]/Status" /></td>
			<td><xsl:value-of select="concat(substring($procDate, 6,2), '/', substring($procDate, 9,2), '/', substring($procDate, 1,4))" /></td>				
		</tr>		
							
	 <xsl:if test="$totalProcedureNodes > $procNodeCnt">
      <xsl:call-template name="ProcedureSummary">
      	<xsl:with-param name="procNode" select="$procNode"/>
        <xsl:with-param name="totalProcedureNodes" select="$totalProcedureNodes" />
        <xsl:with-param name="procNodeCnt" select="$procNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>

<xsl:template name="AllergyDetail" xmlns="urn:hl7-org:v3">
	<xsl:param name="allergyDetailNode"/>
	<xsl:param name="allergyDetailNodeCnt" select="0"/>
	<xsl:param name="totalActiveAllergyNodes" select="0" />
	<entry typeCode="DRIV">
		<act classCode="ACT" moodCode="EVN">		
			<templateId root="2.16.840.1.113883.3.88.11.83.6" assigningAuthorityName="HITSP C83"/>
			<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.3" assigningAuthorityName="IHE PCC"/>
			<id/>			
			<code nullFlavor='NA'/>
			<statusCode code="completed" />
			<effectiveTime>
				<low nullFlavor="UNK" />
				<high nullFlavor="UNK" />
			</effectiveTime>			
			<!--Allergy act template -->			
			<entryRelationship typeCode="SUBJ" inversionInd="false">
				<observation classCode="OBS" moodCode="EVN">
					<templateId root="2.16.840.1.113883.10.20.1.18" assigningAuthorityName="CCD"/>
					<templateId root="2.16.840.1.113883.10.20.1.28" assigningAuthorityName="CCD"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5" assigningAuthorityName="IHE PCC"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.6" assigningAuthorityName="IHE PCC"/>
					<templateId root="2.16.840.1.113883.10.20.1.18"/>
					<xsl:variable name="allergyPath" select="document($allergyDetailNode/child::node()[position()=$allergyDetailNodeCnt]/drilldown)"/>					
					<!--Allergy observation template. NOTE that the HITSP/C83 requirement for code (i.e. allergy type) differs from the IHE PCC recommendation for code.-->
					<xsl:for-each select="$allergyPath/results/rows/row/placeholderIds/id">
					 	<id>
					 		<xsl:attribute name="root">
								<xsl:value-of select="@root"/>
							</xsl:attribute>
							<xsl:attribute name="extension">
								<xsl:value-of select="@extension"/>
							</xsl:attribute>
						</id>
					</xsl:for-each>					
					
					<code code="416098002" codeSystem="2.16.840.1.113883.6.96" displayName="drug allergy" codeSystemName="SNOMED CT"/>
					<text>
						<reference>
							<xsl:attribute name="value">
								<xsl:value-of select="concat('#ALGSUMMARY_', $allergyDetailNodeCnt)" />
							</xsl:attribute>
						</reference>
					</text>
					<statusCode>
						<xsl:attribute name="code">completed</xsl:attribute>
							<!-- <xsl:attribute name="code"><xsl:value-of select="$allergyDetailNode/child::node()[position()=$allergyDetailNodeCnt]/actStatus" /></xsl:attribute>
							 MUST be completed-->						
					</statusCode>
					<effectiveTime>
						<low>
							<xsl:choose>
								<xsl:when test="string-length($allergyPath/results/rows/row/effectiveTime) > 0"> 
									<xsl:attribute name="value">
										<xsl:value-of select="translate(substring-before($allergyPath/results/rows/row/effectiveTime, ' '), '-', '')" />
									</xsl:attribute>	
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="nullFlavor">UNK</xsl:attribute>									
								</xsl:otherwise>
							</xsl:choose>							
						</low>												
					</effectiveTime>
					<!--Note that IHE/PCC and HITSP/C32 differ in how to represent the drug, substance, or food that one is allergic to. IHE/PCC expects to see that information in <value> and HITSP/C32 expects to see it in <participant>.-->
					<value codeSystem="2.16.840.1.113883.6.88">
						<xsl:attribute name="xsi:type">CD</xsl:attribute>						
							<xsl:attribute name="displayName">
								<xsl:value-of select="$allergyPath/results/rows/row/title"/>
							</xsl:attribute>
						<xsl:choose>
							<xsl:when test="$allergyPath/results/rows/row/title = 'Codeine'">
								<xsl:attribute name="code">371613</xsl:attribute>
								<xsl:attribute name="codeSystemName">RxNorm</xsl:attribute>
							</xsl:when>
							<xsl:when test="$allergyPath/results/rows/row/title = 'Ampicillin'">
								<xsl:attribute name="code">370903</xsl:attribute>
								<xsl:attribute name="codeSystemName">RxNorm</xsl:attribute>
							</xsl:when>
							<xsl:otherwise>
								<xsl:attribute name="code">
									<xsl:value-of select="$allergyPath/results/rows/row/code"/>
								</xsl:attribute>
								<xsl:attribute name="codeSystemName">
									<xsl:value-of select="$allergyPath/results/rows/row/codeSystemName"/>
								</xsl:attribute>								
							</xsl:otherwise>
						</xsl:choose>						
						<!-- <xsl:attribute name="codeSystem">
							<xsl:value-of select="$allergyPath/results/rows/row/codeSystemVersion"/>
						</xsl:attribute> -->
						<originalText>
							<reference>
								<xsl:attribute name="value">
									<xsl:value-of select="concat('#ALGSUB_', $allergyDetailNodeCnt)" />
								</xsl:attribute>
							</reference>
						</originalText>
					</value>
					<participant typeCode="CSM">
						<participantRole classCode="MANU">
							<addr/>
							<telecom/>
							<playingEntity classCode="MMAT">
								<code codeSystem="2.16.840.1.113883.6.88" codeSystemName="RxNorm">
									<xsl:attribute name="xsi:type">CE</xsl:attribute>
									<xsl:attribute name="displayName"><xsl:value-of select="$allergyPath/results/rows/row/title"/></xsl:attribute>									
									<xsl:choose>
										<xsl:when test="$allergyPath/results/rows/row/title = 'Codeine'">
											<xsl:attribute name="code">371613</xsl:attribute>
											<xsl:attribute name="codeSystemName">RxNorm</xsl:attribute>
										</xsl:when>
										<xsl:when test="$allergyPath/results/rows/row/title = 'Ampicillin'">
											<xsl:attribute name="code">370903</xsl:attribute>
											<xsl:attribute name="codeSystemName">RxNorm</xsl:attribute>
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="code">
												<xsl:value-of select="$allergyPath/results/rows/row/code"/>
											</xsl:attribute>
											<xsl:attribute name="codeSystemName">
												<xsl:value-of select="$allergyPath/results/rows/row/codeSystemName"/>
											</xsl:attribute>								
										</xsl:otherwise>
									</xsl:choose>	
									<originalText>
										<reference>
											<xsl:attribute name="value">
												<xsl:value-of select="concat('#ALGSUB_', $allergyDetailNodeCnt)" />
											</xsl:attribute>
										</reference>
									</originalText>
								</code>
								<name><xsl:value-of select="$allergyPath/results/rows/row/title"/></name>
							</playingEntity>
						</participantRole>
					</participant>
				</observation>
			</entryRelationship>
		</act>
	</entry>
	<xsl:if test="$totalActiveAllergyNodes > $allergyDetailNodeCnt">
      <xsl:call-template name="AllergyDetail">
      	<xsl:with-param name="allergyDetailNode" select="$allergyDetailNode"/>
        <xsl:with-param name="totalActiveAllergyNodes" select="$totalActiveAllergyNodes" />
        <xsl:with-param name="allergyDetailNodeCnt" select="$allergyDetailNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>

<xsl:template name="ProblemDetail" xmlns="urn:hl7-org:v3">
		<xsl:param name="problemDetailNode"/>
		<xsl:param name="problemDetailNodeCnt" select="0"/>
		<xsl:param name="totalActiveProblemNodes" select="0" />
	<entry typeCode="DRIV">
		<act classCode="ACT" moodCode="EVN">
			<templateId root="2.16.840.1.113883.3.88.11.83.7" assigningAuthorityName="HITSP C83"/>
			<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.2" assigningAuthorityName="IHE PCC"/>
			<id/>
			<code nullFlavor='NA'/>
			<statusCode code="completed" />
			<effectiveTime>
				<low nullFlavor="UNK" />
				<high nullFlavor="UNK" />
			</effectiveTime>
			<!-- Problem act template -->
			<entryRelationship typeCode="SUBJ" inversionInd="false">
				<observation classCode="OBS" moodCode="EVN">
					<templateId root="2.16.840.1.113883.10.20.1.28" assigningAuthorityName="CCD"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5" assigningAuthorityName="IHE PCC"/>
					<!--Problem observation template - NOT episode template-->
					<xsl:variable name="problemPath" select="document($problemDetailNode/child::node()[position()=$problemDetailNodeCnt]/drilldown)"/>
					<xsl:for-each select="$problemPath/results/rows/row/placeholderIds/id">
					 	<id>
					 		<xsl:attribute name="root">
								<xsl:value-of select="@root"/>
							</xsl:attribute>
							<xsl:attribute name="extension">
								<xsl:value-of select="@extension"/>
							</xsl:attribute>
						</id>
					</xsl:for-each>					
					<code code="64572001" displayName="Condition" codeSystem="2.16.840.1.113883.6.96" codeSystemName="SNOMED-CT"/>
					<text>
						<reference>
							<xsl:attribute name="value">
								<xsl:value-of select="concat('#PROBSUMMARY_', $problemDetailNodeCnt)" />
							</xsl:attribute>
						</reference>
					</text>
					<statusCode>
						<!-- <xsl:attribute name="code">
							<xsl:value-of select="$problemDetailNode/child::node()[position()=$problemDetailNodeCnt]/Status" />
						</xsl:attribute>MUST be completed-->
						<xsl:attribute name="code">completed</xsl:attribute> 
					</statusCode>
					<effectiveTime>
						<low>
							<xsl:attribute name="value">
								<xsl:value-of select="translate(substring-before($problemDetailNode/child::node()[position()=$problemDetailNodeCnt]/Date, ' '), '-', '')" />
							</xsl:attribute>
						</low>
					</effectiveTime>
					<!-- <value xsi:type="CD" displayName="Asthma" code="195967001" codeSystemName="SNOMED" codeSystem="2.16.840.1.113883.6.96"/> -->
					<value codeSystemName="SNOMED" codeSystem="2.16.840.1.113883.6.96">
						<xsl:attribute name="xsi:type">CD</xsl:attribute>
						<xsl:attribute name="displayName"><xsl:value-of select="$problemDetailNode/child::node()[position()=$problemDetailNodeCnt]/Problem" /></xsl:attribute>
						<xsl:attribute name="code"><xsl:value-of select="$problemDetailNode/child::node()[position()=$problemDetailNodeCnt]/Code" /></xsl:attribute>
					</value>
				</observation>
			</entryRelationship>
		</act>
	</entry>
	<xsl:if test="$totalActiveProblemNodes > $problemDetailNodeCnt">
      <xsl:call-template name="ProblemDetail">
      	<xsl:with-param name="problemDetailNode" select="$problemDetailNode"/>
        <xsl:with-param name="totalActiveProblemNodes" select="$totalActiveProblemNodes" />
        <xsl:with-param name="problemDetailNodeCnt" select="$problemDetailNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>
	
<xsl:template name="MedicationDetail" xmlns="urn:hl7-org:v3">
	<xsl:param name="medDetailNode"/>
	<xsl:param name="medDetailNodeCnt" select="0"/>
	<xsl:param name="totalMedicationNodes" select="0" />
	<entry typeCode="DRIV">
		<substanceAdministration classCode="SBADM" moodCode="EVN">
			<templateId root="2.16.840.1.113883.3.88.11.83.8" assigningAuthorityName="HITSP C83"/>
			<templateId root="2.16.840.1.113883.10.20.1.24" assigningAuthorityName="CCD"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7" assigningAuthorityName="IHE PCC"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7.1" assigningAuthorityName="IHE PCC"/>
			<!--Medication activity template -->
			<xsl:variable name="medPath" select="document($medDetailNode/child::node()[position()=$medDetailNodeCnt]/drilldown)"/>
			<xsl:for-each select="$medPath/results/rows/row/placeholderIds/id">
			 	<id>
			 		<xsl:attribute name="root">
						<xsl:value-of select="@root"/>
					</xsl:attribute>
					<xsl:attribute name="extension">
						<xsl:value-of select="@extension"/>
					</xsl:attribute>
				</id>
			</xsl:for-each>			
			<text>				
				<reference>
					<xsl:attribute name="value">
						<xsl:value-of select="concat('#SIGTEXT_', $medDetailNodeCnt)" />
					</xsl:attribute>
				</reference>
			</text>
			<statusCode>
				<!-- <xsl:attribute name="code"><xsl:value-of select="$medDetailNode/child::node()[position()=$medDetailNodeCnt]/actStatus" /></xsl:attribute>MUST be completed-->
				<xsl:attribute name="code">completed</xsl:attribute>
			</statusCode>
			<effectiveTime>
				<xsl:attribute name="xsi:type">IVL_TS</xsl:attribute>
				<low>
					<xsl:attribute name="value">
						<xsl:value-of select="translate(substring-before($medDetailNode/child::node()[position()=$medDetailNodeCnt]/start, ' '), '-', '')" />
					</xsl:attribute>
				</low>
				<high nullFlavor="UNK"/>
			</effectiveTime>
			<effectiveTime>
				<xsl:attribute name="xsi:type">PIVL_TS</xsl:attribute>
				<period>
					<xsl:choose>
						<xsl:when test="string-length($medDetailNode/child::node()[position()=$medDetailNodeCnt]/frequency) > 0"> 
							<xsl:attribute name="value"><xsl:value-of select="substring($medDetailNode/child::node()[position()=$medDetailNodeCnt]/frequency,1,1)" /></xsl:attribute>
							<xsl:attribute name="unit"><xsl:value-of select="substring($medDetailNode/child::node()[position()=$medDetailNodeCnt]/frequency,2,1)" /></xsl:attribute>	
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="nullFlavor">UNK</xsl:attribute>									
						</xsl:otherwise>
					</xsl:choose>				
				</period>
			</effectiveTime>
			<!--The following route, dose and administrationUnit elements are HITSP/C83 Sig Components that are optional elements in a HITSP/C83 document. The dose and administrationUnit information is often inferable from the code (e.g. RxNorm code) of the consumable/manufacturedProduct. The route is often inferable from the administrativeUnit (e.g tablet implies oral route). -->
			<routeCode>
				<originalText><xsl:value-of select="$medDetailNode/child::node()[position()=$medDetailNodeCnt]/route" /></originalText>
			</routeCode>
			<doseQuantity>
				<xsl:attribute name="value"><xsl:value-of select="$medPath/results/rows/row/quantityValue" /></xsl:attribute><!-- substring-before($medDetailNode/child::node()[position()=$medDetailNodeCnt]/route, '=') -->
				<xsl:attribute name="unit"><xsl:value-of select="$medPath/results/rows/row/quantityUnit" /></xsl:attribute><!-- substring-after($medDetailNode/child::node()[position()=$medDetailNodeCnt]/route, '=') -->
			</doseQuantity>
			<administrationUnitCode>
				<originalText><xsl:value-of select="$medDetailNode/child::node()[position()=$medDetailNodeCnt]/instructions" /></originalText>
			</administrationUnitCode>
			<consumable>
				<manufacturedProduct>
					<templateId root="2.16.840.1.113883.3.88.11.83.8.2" assigningAuthorityName="HITSP C83"/>
					<templateId root="2.16.840.1.113883.10.20.1.53" assigningAuthorityName="CCD"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7.2" assigningAuthorityName="IHE PCC"/>
					<!-- Product template -->
					<manufacturedMaterial>
						<code codeSystem="2.16.840.1.113883.6.88" codeSystemName="RxNorm">
							<xsl:attribute name="code"><xsl:value-of select="$medDetailNode/child::node()[position()=$medDetailNodeCnt]/code" /></xsl:attribute>
							<xsl:attribute name="displayName"><xsl:value-of select="$medDetailNode/child::node()[position()=$medDetailNodeCnt]/name" /></xsl:attribute>
							<originalText>
								<xsl:value-of select="$medDetailNode/child::node()[position()=$medDetailNodeCnt]/name" /><reference/>
							</originalText>
						</code>
					</manufacturedMaterial>
				</manufacturedProduct>
			</consumable>
		</substanceAdministration>
	</entry>	
	<xsl:if test="$totalMedicationNodes > $medDetailNodeCnt">
      <xsl:call-template name="MedicationDetail">
      	<xsl:with-param name="medDetailNode" select="$medDetailNode"/>
        <xsl:with-param name="totalMedicationNodes" select="$totalMedicationNodes" />
        <xsl:with-param name="medDetailNodeCnt" select="$medDetailNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>	

<xsl:template name="ProcedureDetail" xmlns="urn:hl7-org:v3">
	<xsl:param name="procDetailNode"/>
	<xsl:param name="procDetailNodeCnt" select="0"/>
	<xsl:param name="totalProcedureNodes" select="0"/>
	<xsl:variable name="procDetailPath" select="document($procDetailNode/child::node()[position()=$procDetailNodeCnt]/drilldown)"/>
	
	<procedure classCode="PROC" moodCode="EVN">
		<templateId root="2.16.840.1.113883.3.88.11.83.17" assigningAuthorityName="HITSP C83"/>
		<templateId root="2.16.840.1.113883.10.20.1.29" assigningAuthorityName="CCD"/>
		<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.19" assigningAuthorityName="IHE PCC"/>
		<xsl:for-each select="$procDetailPath/results/rows/row/placeholderIds/id">
		 	<id>
		 		<xsl:attribute name="root">
					<xsl:value-of select="@root"/>
				</xsl:attribute>
				<xsl:attribute name="extension">
					<xsl:value-of select="@extension"/>
				</xsl:attribute>
			</id>
		</xsl:for-each>
		<code code="20109005" codeSystem="2.16.840.1.113883.6.96" codeSystemName="SNOMED CT">
			<xsl:attribute name="displayName">	
				<xsl:value-of select="$procDetailNode/child::node()[position()=$procDetailNodeCnt]/Procedure"/>
			</xsl:attribute>
			<originalText><xsl:value-of select="$procDetailNode/child::node()[position()=$procDetailNodeCnt]/Procedure"/>
				<reference value="Ptr to text  in parent Section"/>
			</originalText>
		</code>
		<text>
			<xsl:value-of select="$procDetailNode/child::node()[position()=$procDetailNodeCnt]/Procedure"/>						
			<reference value="Ptr to text  in parent Section"/>
		</text>
		<statusCode>
			<xsl:attribute name="code">	
				<xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'"/>
				<xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
				<xsl:value-of select="translate($procDetailNode/child::node()[position()=$procDetailNodeCnt]/Status, $uppercase, $smallcase)"/>
			</xsl:attribute>
		</statusCode>
		<effectiveTime>
			<xsl:attribute name="value">	
				<xsl:variable name="procDetailNodeDate" select="$procDetailNode/child::node()[position()=$procDetailNodeCnt]/Date"/>							
				<xsl:value-of select="translate(substring-before(translate(translate(translate($procDetailNodeDate, '-', ''), ':', ''), 'T', ''), '.'), ' ', '')"/>
			</xsl:attribute>
		</effectiveTime>
	</procedure>
	<xsl:if test="totalProcedureNodes > $procDetailNodeCnt">
      <xsl:call-template name="ProcedureDetail">
      	<xsl:with-param name="procDetailNode" select="$procDetailNode"/>
        <xsl:with-param name="totalProcedureNodes" select="$totalProcedureNodes" />
        <xsl:with-param name="procDetailNodeCnt" select="$procDetailNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>

<xsl:template name="LabResultDetail" xmlns="urn:hl7-org:v3">
		<xsl:param name="labresultDetailNode"/>
		<xsl:param name="labresultDetailNodeCnt" select="0"/>
		<xsl:param name="totalLabResultNodes" select="0" />
		<!-- <xsl:param name="totalProcedureNodes" select="0" /> -->
		<xsl:variable name="labresultDetailPath" select="document($labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/drilldown)"/>		
	<entry typeCode="DRIV">
	<!-- REPEAT SECTION FOR EACH BATTERY/EVN -->
		<organizer classCode="BATTERY" moodCode="EVN">
			<templateId root="2.16.840.1.113883.10.20.1.32"/>
			<!-- Result organizer template -->			
			<id/>
				<!-- <xsl:attribute name="root">
	 				<xsl:value-of select="$ctx/context/accountIdRoot/@id" />
				</xsl:attribute>			
				<xsl:attribute name="extension">
					<xsl:value-of select="$labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/path" />
				</xsl:attribute>
			</id> -->	
			<!-- <code code="20109005" codeSystem="2.16.840.1.113883.6.96" codeSystemName="SNOMED CT">
				<xsl:attribute name="displayName">	
					<xsl:value-of select="$labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/name"/>
				</xsl:attribute>
			</code> -->
			<code nullFlavor='NA'/>
			<statusCode code="completed" />
			<effectiveTime>
				<low nullFlavor="UNK" />
				<high nullFlavor="UNK" />
			</effectiveTime>
			<component>
			<!-- REPEAT SECTION FOR EACH PROC/EVN-->  
				<xsl:if test="$totalProcedureNodes > 0">
					<xsl:call-template name="ProcedureDetail">
						<xsl:with-param name="procDetailNode" select="$procedures/results/rows"/>
						<xsl:with-param name="totalProcedureNodes" select="$totalProcedureNodes"/>
						<xsl:with-param name="procDetailNodeCnt" select="1"/>
					</xsl:call-template>	
				</xsl:if>
				<!-- <procedure classCode="PROC" moodCode="EVN">
					<templateId root="2.16.840.1.113883.3.88.11.83.17" assigningAuthorityName="HITSP C83"/>
					<templateId root="2.16.840.1.113883.10.20.1.29" assigningAuthorityName="CCD"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.19" assigningAuthorityName="IHE PCC"/>
					<id>
						<xsl:attribute name="root">
			 				<xsl:value-of select="$ctx/context/accountIdRoot/@id" />
						</xsl:attribute>			
						<xsl:attribute name="extension">
							<xsl:value-of select="$labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/path" />
						</xsl:attribute>
					</id>
					<code code="20109005" codeSystem="2.16.840.1.113883.6.96" codeSystemName="SNOMED CT">
						<xsl:attribute name="displayName">	
							<xsl:value-of select="$labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/name"/>
						</xsl:attribute>
						<originalText><xsl:value-of select="$labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/name"/>
							<reference value="Ptr to text  in parent Section"/>
						</originalText>
					</code>
					<text>
						<xsl:value-of select="$labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/name"/>						
						<reference value="Ptr to text  in parent Section"/>
					</text>
					<statusCode>
						<xsl:attribute name="code">	
							<xsl:value-of select="$labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/actStatus"/>
						</xsl:attribute>
					</statusCode>
					<effectiveTime>
						<xsl:attribute name="value">	
							<xsl:value-of select="$labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/Date"/>
						</xsl:attribute>
					</effectiveTime>
				</procedure> -->
			</component>
			<component>
				<!-- REPEAT SECTION FOR EACH OBS/EVN -->
				<observation classCode="OBS" moodCode="EVN">
					<templateId root="2.16.840.1.113883.3.88.11.83.15.1" assigningAuthorityName="HITSP C83"/>
					<templateId root="2.16.840.1.113883.10.20.1.31" assigningAuthorityName="CCD"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.13" assigningAuthorityName="IHE PCC"/>
					<!-- Result observation template -->
					<xsl:variable name="labresultPath" select="document($labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/drilldown)"/>
					<xsl:for-each select="$labresultPath/results/rows/row/placeholderIds/id">
					 	<id>
					 		<xsl:attribute name="root">
								<xsl:value-of select="@root"/>
							</xsl:attribute>
							<xsl:attribute name="extension">
								<xsl:value-of select="@extension"/>
							</xsl:attribute>
						</id>
					</xsl:for-each>
					<code codeSystem="2.16.840.1.113883.6.1">
						<xsl:attribute name="displayName">	
							<xsl:value-of select="$labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/title"/><!-- displayName="NA"  -->
						</xsl:attribute>
						<xsl:attribute name="code">	
							<xsl:value-of select="$labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/code"/><!-- 2951-2  -->
						</xsl:attribute>
					</code>
					<text>
						<reference value="PtrToValueInsectionText...FIXME"/>
					</text>
					<statusCode>
						<!--<xsl:attribute name="code">	
							<xsl:value-of select="$labresultDetailPath/results/rows/row/actStatus"/>
						</xsl:attribute>MUST be completed-->
						<xsl:attribute name="code">completed</xsl:attribute>
					</statusCode>
					<effectiveTime>
						<xsl:attribute name="value">
							<xsl:variable name="labDetailNodeDate" select="$labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/Date"/>							
							<xsl:value-of select="translate(substring-before(translate(translate(translate($labDetailNodeDate, '-', ''), ':', ''), 'T', ''), '.'), ' ', '')"/><!-- value="200004061300"-->
						</xsl:attribute>
					</effectiveTime>
					<value>
						<xsl:attribute name="xsi:type">PQ</xsl:attribute>
							<!-- <xsl:value-of select="$labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/type"/> --><!-- PQ-->						
						<xsl:attribute name="value">	
							<xsl:value-of select="$labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/Value"/><!-- 140-->
						</xsl:attribute>
						<xsl:attribute name="unit">	
							<xsl:value-of select="$labresultDetailNode/child::node()[position()=$labresultDetailNodeCnt]/Units"/><!-- meq/l-->
						</xsl:attribute>
					</value>
					<referenceRange>
						<observationRange>
							<interpretationCode codeSystem="2.16.840.1.113883.5.83">
								<xsl:attribute name="code">	
									<xsl:choose>
										<xsl:when test="string-length($labresultDetailPath/results/rows/row/interpretationCode.code) > 0">
											<xsl:value-of select="$labresultDetailPath/results/rows/row/interpretationCode.code"/>
										</xsl:when>
										<xsl:otherwise>N</xsl:otherwise>
										</xsl:choose>									
								</xsl:attribute>					
							</interpretationCode>
							<!-- <low>
								<xsl:attribute name="value"><xsl:value-of select="$labresultDetailPath/results/rows/row/lowvalue" /></xsl:attribute>
								<xsl:attribute name="unit"><xsl:value-of select="$labresultDetailPath/results/rows/row/lowunits" /></xsl:attribute>
							</low>
							<high>
								<xsl:attribute name="value"><xsl:value-of select="$labresultDetailPath/results/rows/row/highvalue" /></xsl:attribute>
								<xsl:attribute name="unit"><xsl:value-of select="$labresultDetailPath/results/rows/row/highunits" /></xsl:attribute>
							</high>	 -->			
						</observationRange>		
					</referenceRange>
					
				</observation>
			</component>
		</organizer>
	</entry>
			
	<xsl:if test="$totalLabResultNodes > $labresultDetailNodeCnt">
      <xsl:call-template name="LabResultDetail">
      	<xsl:with-param name="labresultDetailNode" select="$labresultDetailNode"/>
        <xsl:with-param name="totalLabResultNodes" select="$totalLabResultNodes" />
        <xsl:with-param name="labresultDetailNodeCnt" select="$labresultDetailNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>
											
</xsl:stylesheet>
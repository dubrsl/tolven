<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:hl7-org:v3 http://xreg2.nist.gov:8080/hitspValidation/schema/cdar2c32/infrastructure/cda/C32_CDA.xsd">
<xsl:param name="context"/>
<xsl:variable name="ctx" select="document('context')"/>
<xsl:variable name="pat" select="document($context)"/>
<xsl:variable name="allergies"	select="document(concat($context,':allergies:current'))" />
<xsl:variable name="problems" select="document(concat($context,':problems:active'))"/>
<xsl:variable name="meds"	select="document(concat($context,':medications:active'))" />
<xsl:variable name="procedures" select="document(concat($context,':procedures:pxList'))"/>
<xsl:variable name="labtests" select="document(concat($context,':results:lab'))"/>

<xsl:variable name="totalAllergyNodes" select="count($allergies/results/rows/child::node())" />
<xsl:variable name="totalProblemNodes" select="count($problems/results/rows/child::node())" />
<xsl:variable name="totalMedicationNodes" select="count($meds/results/rows/child::node())" />
<xsl:variable name="totalProcedureNodes" select="count($procedures/results/rows/child::node())" />
<xsl:variable name="totalLabResultNodes" select="count($labtests/results/rows/child::node())" />

<xsl:template match="/">
<ClinicalDocument xmlns="urn:hl7-org:v3" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:hl7-org:v3 http://xreg2.nist.gov:8080/hitspValidation/schema/cdar2c32/infrastructure/cda/C32_CDA.xsd">
	<realmCode code="US"/>
	<typeId root="2.16.840.1.113883.1.3" extension="POCD_HD000040"/>
	<templateId root="2.16.840.1.113883.3.27.1776" assigningAuthorityName="CDA/R2"/>
	<templateId root="2.16.840.1.113883.10.20.3" assigningAuthorityName="HL7/CDT Header"/>
	<templateId root="1.3.6.1.4.1.19376.1.5.3.1.1.1" assigningAuthorityName="IHE/PCC"/>
	<templateId root="2.16.840.1.113883.3.88.11.32.1" assigningAuthorityName="HITSP/C32"/>
	<id root="2.16.840.1.113883.3.72" extension="MU_Rev2_HITSP_C32C83_4Sections_MeaningfulEntryContent_NoErrors" assigningAuthorityName="NIST Healthcare Project"/>	
	<code code="34133-9" displayName="Summarization of episode note" codeSystem="2.16.840.1.113883.6.1" codeSystemName="LOINC"/>
	<title/>
	<effectiveTime>
		<xsl:attribute name="value"><xsl:value-of select="$ctx/context/@now" /></xsl:attribute>
	</effectiveTime>
	<confidentialityCode/>
	<languageCode code="en-US"/>
	<recordTarget>
		<patientRole>
			<!-- <id root="ProviderID" extension="PatientID" assigningAuthorityName="Provider Name"/> -->
			<id assigningAuthorityName="Provider Name">
				<xsl:attribute name="root">
	 				<xsl:value-of select="$ctx/context/accountIdRoot/@id" />
				</xsl:attribute>			
				<xsl:attribute name="extension">
					<xsl:value-of select="$context" />
				</xsl:attribute>
			</id>			
			<addr use="HP">
				<!--HITSP/C83 recommends that a patient have at least one address element with a use attribute of HP, i.e. home primary-->
None of the following address elements are specifically required by HITSP C32, so the address could be be just plain text at this location? Use of one or more of the following labeled address lines, with meaningful content, is recommended.
				<streetAddressLine><xsl:value-of select="$pat/results/rows/row/homeAddr1"/></streetAddressLine>
				<streetAddressLine><xsl:value-of select="$pat/results/rows/row/homeAddr2"/></streetAddressLine>
				<city><xsl:value-of select="$pat/results/rows/row/homeCity"/></city>
				<state><xsl:value-of select="$pat/results/rows/row/homeState"/></state>
				<postalCode><xsl:value-of select="$pat/results/rows/row/homeZip"/></postalCode>
				<country><xsl:value-of select="$pat/results/rows/row/homeCountry"/></country>
			</addr>
			<telecom><xsl:value-of select="$pat/results/rows/row/homeTelecom"/></telecom>
			<patient>
				<name>
					<given><xsl:value-of select="$pat/results/rows/row/firstName"/></given>
					<given><xsl:value-of select="$pat/results/rows/row/middleName"/></given>
					<!-- <family><xsl:value-of select="$pat/familyName"/></family> -->
					<family><xsl:value-of select="$pat/results/rows/row/lastName"/></family>
				</name>
				<!--HITSP/C83 requires patient administrative gender, e.g. M, F, I (indeterminate)-->
				<!-- <administrativeGenderCode code="F" displayName="Female" codeSystem="2.16.840.1.113883.5.1" codeSystemName="HL7 AdministrativeGender"/> -->
				<administrativeGenderCode codeSystem="2.16.840.1.113883.5.1" codeSystemName="HL7 AdministrativeGender">
					<xsl:attribute name="displayName">
		 				<xsl:value-of select="$pat/results/rows/row/gender" />
					</xsl:attribute>
					<xsl:attribute name="code">
						<xsl:value-of select="substring($pat/results/rows/row/gender, 1, 1)" />
					</xsl:attribute>
				</administrativeGenderCode>
				<!-- <birthTime value="19840704"/> -->
				<birthTime>
					<xsl:attribute name="value">
		 				<xsl:value-of select="concat(substring($pat/results/rows/row/dob, 1,4), substring($pat/results/rows/row/dob, 6,2), substring($pat/results/rows/row/dob, 9,2))" />
					</xsl:attribute>				
				</birthTime>
				<!--HITSP/C83 requires patient marital status - if known, e.g. S, M, D-->
				<!-- <maritalStatusCode code="S" displayName="Single" codeSystem="2.16.840.1.113883.5.2" codeSystemName="HL7 Marital status"/> -->
				<!--HITSP/C32 requires patient languages spoken - if known.-->
				<languageCommunication>
					<templateId root="2.16.840.1.113883.3.88.11.83.2" assigningAuthorityName="HITSP/C83"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.2.1" assigningAuthorityName="IHE/PCC"/>
					<languageCode code="en-US">
						<xsl:value-of select="$pat/results/rows/row/language" />
					</languageCode>
				</languageCommunication>
			</patient>
		</patientRole>
	</recordTarget>
	<author>
		<time>
			<xsl:attribute name="value"><xsl:value-of select="$ctx/context/@now" /></xsl:attribute>
		</time>
		<assignedAuthor>
			<id/>
			<addr/>
			<telecom/>
			<assignedPerson>
				<name>Staff</name>
			</assignedPerson>
			<representedOrganization>
				<name>NIST Healthcare Testing Laboratory</name>
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
									<th>Type</th>
									<th>Substance</th>
									<th>Reaction</th>
									<th>Status</th>
								</tr>
							</thead>
							<tbody>
								<xsl:if test="$totalAllergyNodes > 0">
									<xsl:call-template name="AllergySummary">
										<xsl:with-param name="allergyNode" select="$allergies/results/rows"/>
										<xsl:with-param name="totalAllergyNodes" select="$totalAllergyNodes"/>
										<xsl:with-param name="allergyNodeCnt" select="1"/>
									</xsl:call-template>	
								</xsl:if>
								<!-- <xsl:apply-templates select="$allergies/results/rows" mode="Allergy"/> -->
								<!-- <tr ID="ALGSUMMARY_1">
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
								</tr> -->
							</tbody>
						</table>
					</text>
					<!-- <xsl:apply-templates select="$allergies/results/rows" mode="AllergyDetail" /> -->
					<xsl:if test="$totalAllergyNodes > 0">
						<xsl:call-template name="AllergyDetail">
							<xsl:with-param name="allergyDetailNode" select="$allergies/results/rows"/>
							<xsl:with-param name="totalAllergyNodes" select="$totalAllergyNodes"/>
							<xsl:with-param name="allergyDetailNodeCnt" select="1"/>
						</xsl:call-template>	
					</xsl:if>
					<!-- <entry typeCode="DRIV">
						<act classCode="ACT" moodCode="EVN">
							<templateId root="2.16.840.1.113883.3.88.11.83.6" assigningAuthorityName="HITSP C83"/>
							<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.3" assigningAuthorityName="IHE PCC"/>
							Allergy act template
							<id root="36e3e930-7b14-11db-9fe1-0800200c9a66"/>
							<code nullFlavor="NA"/>
							<statusCode code="completed"/>
							<effectiveTime>
								<low nullFlavor="UNK"/>
								<high nullFlavor="UNK"/>
							</effectiveTime>
							<entryRelationship typeCode="SUBJ" inversionInd="false">
								<observation classCode="OBS" moodCode="EVN">
									<templateId root="2.16.840.1.113883.10.20.1.18" assigningAuthorityName="CCD"/>
									<templateId root="2.16.840.1.113883.10.20.1.28" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5" assigningAuthorityName="IHE PCC"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.6" assigningAuthorityName="IHE PCC"/>
									<templateId root="2.16.840.1.113883.10.20.1.18"/>
									Allergy observation template. NOTE that the HITSP/C83 requirement for code (i.e. allergy type) differs from the IHE PCC recommendation for code.
									<id root="4adc1020-7b14-11db-9fe1-0800200c9a66" extension=""/>
									<code code="416098002" codeSystem="2.16.840.1.113883.6.96" displayName="drug allergy" codeSystemName="SNOMED CT"/>
									<text>
										<reference value="#ALGSUMMARY_1"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime>
										<low nullFlavor="UNK"/>
									</effectiveTime>
									Note that IHE/PCC and HITSP/C32 differ in how to represent the drug, substance, or food that one is allergic to. IHE/PCC expects to see that information in <value> and HITSP/C32 expects to see it in <participant>.
									<value xsi:type="CD" code="70618" codeSystem="2.16.840.1.113883.6.88" displayName="Penicillin" codeSystemName="RxNorm">
										<originalText>
											<reference value="#ALGSUB_1"/>
										</originalText>
									</value>
									<participant typeCode="CSM">
										<participantRole classCode="MANU">
											<addr/>
											<telecom/>
											<playingEntity classCode="MMAT">
												<code code="70618" codeSystem="2.16.840.1.113883.6.88" displayName="Penicillin" codeSystemName="RxNorm">
													<originalText>
														<reference value="#ALGSUB_1"/>
													</originalText>
												</code>
												<name>Penicillin</name>
											</playingEntity>
										</participantRole>
									</participant>
								</observation>
							</entryRelationship>
						</act>
					</entry>
					<entry typeCode="DRIV">
						<act classCode="ACT" moodCode="EVN">
							<templateId root="2.16.840.1.113883.3.88.11.83.6" assigningAuthorityName="HITSP C83"/>
							<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.3" assigningAuthorityName="IHE PCC"/>
							Allergy act template
							<id root="eb936010-7b17-11db-9fe1-0800200c9a66"/>
							<code nullFlavor="NA"/>
							<statusCode code="active"/>
							<effectiveTime>
								<low nullFlavor="UNK"/>
							</effectiveTime>
							<entryRelationship typeCode="SUBJ" inversionInd="false">
								<observation classCode="OBS" moodCode="EVN">
									<templateId root="2.16.840.1.113883.10.20.1.18" assigningAuthorityName="CCD"/>
									<templateId root="2.16.840.1.113883.10.20.1.28" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5" assigningAuthorityName="IHE PCC"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.6" assigningAuthorityName="IHE PCC"/>
									Allergy observation template. NOTE that the HITSP/C83 requirement for code (i.e. allergy type) differs from the IHE PCC recommendation for code.
									<id root="eb936011-7b17-11db-9fe1-0800200c9a66"/>
									<code displayName="propensity to adverse reactions to drug" code="419511003" codeSystemName="SNOMED CT" codeSystem="2.16.840.1.113883.6.96"/>
									<text>
										<reference value="#ALGSUMMARY_2"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime>
										<low nullFlavor="UNK"/>
									</effectiveTime>
									Note that IHE/PCC and HITSP/C32 differ in how to represent the brug, substance, or food that one is allergic to. IHE/PCC expects to see that information in <value> and HITSP/C32 expects to see it in <participant>.
									<value xsi:type="CD" code="1191" codeSystem="2.16.840.1.113883.6.88" displayName="Aspirin" codeSystemName="RxNorm">
										<originalText>
											<reference value="#ALGSUB_2"/>
										</originalText>
									</value>
									<participant typeCode="CSM">
										<participantRole classCode="MANU">
											<addr/>
											<telecom/>
											<playingEntity classCode="MMAT">
												<code code="1191" codeSystem="2.16.840.1.113883.6.88" displayName="Aspirin" codeSystemName="RxNorm">
													<originalText>
														<reference value="#ALGSUB_2"/>
													</originalText>
												</code>
												<name>Aspirin</name>
											</playingEntity>
										</participantRole>
									</participant>
								</observation>
							</entryRelationship>
						</act>
					</entry>
					<entry typeCode="DRIV">
						<act classCode="ACT" moodCode="EVN">
							<templateId root="2.16.840.1.113883.3.88.11.83.6" assigningAuthorityName="HITSP C83"/>
							<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.3" assigningAuthorityName="IHE PCC"/>
							Allergy act template
							<id root="c3df3b61-7b18-11db-9fe1-0800200c9a66"/>
							<code nullFlavor="NA"/>
							<statusCode code="active"/>
							<effectiveTime>
								<low nullFlavor="UNK"/>
							</effectiveTime>
							<entryRelationship typeCode="SUBJ" inversionInd="false">
								<observation classCode="OBS" moodCode="EVN">
									<templateId root="2.16.840.1.113883.10.20.1.18"/>
									<templateId root="2.16.840.1.113883.10.20.1.28" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5" assigningAuthorityName="IHE PCC"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.6" assigningAuthorityName="IHE PCC"/>
									Allergy observation template. NOTE that the HITSP/C83 requirement for code (i.e. allergy or adverse reaction type) differs from the IHE PCC recommendation for code.
									<id root="c3df3b60-7b18-11db-9fe1-0800200c9a66"/>
									<code code="59037007" codeSystem="2.16.840.1.113883.6.96" displayName="drug intolerance" codeSystemName="SNOMED CT"/>
									<text>
										<reference value="#ALGSUMMARY_3"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime>
										<low value="200512"/>
										<high value="200601"/>
									</effectiveTime>
									Note that IHE/PCC and HITSP/C32 differ in how to represent the drug, substance, or food that one is allergic to. IHE/PCC expects to see that information in <value> and HITSP/C32 expects to see it in <participant>.
									<value xsi:type="CD" code="2670" codeSystem="2.16.840.1.113883.6.88" displayName="Codeine" codeSystemName="RxNorm">
										<originalText>
											<reference value="#ALGSUB_3"/>
										</originalText>
									</value>
									<participant typeCode="CSM">
										<participantRole classCode="MANU">
											<addr/>
											<telecom/>
											<playingEntity classCode="MMAT">
												<code code="2670" codeSystem="2.16.840.1.113883.6.88" displayName="Codeine" codeSystemName="RxNorm">
													<originalText>
														<reference value="#ALGSUB_3"/>
													</originalText>
												</code>
												<name>Codeine</name>
											</playingEntity>
										</participantRole>
									</participant>
								</observation>
							</entryRelationship>
						</act>
					</entry> -->
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
									<th>Problem</th>
									<th>Effective Dates</th>
									<th>Problem Status</th>
								</tr>
							</thead>
							<tbody>
								<xsl:if test="$totalProblemNodes > 0">
									<xsl:call-template name="ProblemSummary">
										<xsl:with-param name="problemNode" select="$problems/results/rows"/>
										<xsl:with-param name="totalProblemNodes" select="$totalProblemNodes"/>
										<xsl:with-param name="problemNodeCnt" select="1"/>
									</xsl:call-template>	
								</xsl:if>
								<!-- <xsl:apply-templates select="$problems/results/rows" mode="Problem"/> -->
								<!-- <tr ID="PROBSUMMARY_1">
									<td ID="PROBKIND_1">Asthma</td>
									<td>1950</td>
									<td ID="PROBSTATUS_1">Active</td>
								</tr>
								<tr ID="PROBSUMMARY_2">
									<td ID="PROBKIND_2">Pneumonia</td>
									<td>Jan 1997</td>
									<td ID="PROBSTATUS_2">Resolved</td>
								</tr>
								<tr ID="PROBSUMMARY_3">
									<td ID="PROBKIND_3">Pneumonia</td>
									<td>Mar 1999</td>
									<td ID="PROBSTATUS_3">Resolved</td>
								</tr>
								<tr ID="PROBSUMMARY_4">
									<td ID="PROBKIND_4">Myocardial Infarction</td>
									<td>Jan 1997</td>
									<td ID="PROBSTATUS_4">Resolved</td>
								</tr>
								<tr ID="PROBSUMMARY_5">
									<td ID="PROBKIND_5">Pregnancy</td>
									<td>Oct 26, 2010</td>
									<td ID="PROBSTATUS_5">NOT currently pregnant</td>
								</tr> -->
							</tbody>
						</table>
					</text>
					<xsl:if test="$totalProblemNodes > 0">
						<xsl:call-template name="ProblemDetail">
							<xsl:with-param name="problemDetailNode" select="$problems/results/rows"/>
							<xsl:with-param name="totalProblemNodes" select="$totalProblemNodes"/>
							<xsl:with-param name="problemDetailNodeCnt" select="1"/>
						</xsl:call-template>	
					</xsl:if>
					<!-- <xsl:apply-templates select="$problems/results/rows" mode="ProblemDetail" /> -->
					<!-- <entry typeCode="DRIV">
						<act classCode="ACT" moodCode="EVN">
							<templateId root="2.16.840.1.113883.3.88.11.83.7" assigningAuthorityName="HITSP C83"/>
							<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.2" assigningAuthorityName="IHE PCC"/>
							Problem act template
							<id root="6a2fa88d-4174-4909-aece-db44b60a3abb"/>
							<code nullFlavor="NA"/>
							<statusCode code="completed"/>
							<effectiveTime>
								<low value="1950"/>
								<high nullFlavor="UNK"/>
							</effectiveTime>
							<entryRelationship typeCode="SUBJ" inversionInd="false">
								<observation classCode="OBS" moodCode="EVN">
									<templateId root="2.16.840.1.113883.10.20.1.28" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5" assigningAuthorityName="IHE PCC"/>
									Problem observation template - NOT episode template
									<id root="d11275e7-67ae-11db-bd13-0800200c9a66"/>
									<code code="64572001" displayName="Condition" codeSystem="2.16.840.1.113883.6.96" codeSystemName="SNOMED-CT"/>
									<text>
										<reference value="#PROBSUMMARY_1"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime>
										<low value="1950"/>
									</effectiveTime>
									<value xsi:type="CD" displayName="Asthma" code="195967001" codeSystemName="SNOMED" codeSystem="2.16.840.1.113883.6.96"/>
								</observation>
							</entryRelationship>
						</act>
					</entry>
					<entry typeCode="DRIV">
						<act classCode="ACT" moodCode="EVN">
							<templateId root="2.16.840.1.113883.3.88.11.83.7" assigningAuthorityName="HITSP C83"/>
							<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.2" assigningAuthorityName="IHE PCC"/>
							Problem act template
							<id root="ec8a6ff8-ed4b-4f7e-82c3-e98e58b45de7"/>
							<code nullFlavor="NA"/>
							<statusCode code="active"/>
							<effectiveTime>
								<low nullFlavor="UNK"/>
							</effectiveTime>
							<entryRelationship typeCode="SUBJ" inversionInd="false">
								<observation classCode="OBS" moodCode="EVN">
									<templateId root="2.16.840.1.113883.10.20.1.28" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5" assigningAuthorityName="IHE PCC"/>
									Problem observation template
									<id root="ab1791b0-5c71-11db-b0de-0800200c9a66"/>
									<code displayName="Condition" code="64572001" codeSystemName="SNOMED-CT" codeSystem="2.16.840.1.113883.6.96"/>
									<text>
										<reference value="#PROBSUMMARY_2"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime>
										<low value="199701"/>
										<high nullFlavor="UNK"/>
									</effectiveTime>
									<value xsi:type="CD" displayName="Pneumonia" code="233604007" codeSystemName="SNOMED" codeSystem="2.16.840.1.113883.6.96"/>
								</observation>
							</entryRelationship>
						</act>
					</entry>
					<entry typeCode="DRIV">
						<act classCode="ACT" moodCode="EVN">
							<templateId root="2.16.840.1.113883.3.88.11.83.7" assigningAuthorityName="HITSP C83"/>
							<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.2" assigningAuthorityName="IHE PCC"/>
							Problem act template
							<id root="d11275e9-67ae-11db-bd13-0800200c9a66"/>
							<code nullFlavor="NA"/>
							<statusCode code="active"/>
							<effectiveTime>
								<low nullFlavor="UNK"/>
							</effectiveTime>
							<entryRelationship typeCode="SUBJ" inversionInd="false">
								<observation classCode="OBS" moodCode="EVN">
									<templateId root="2.16.840.1.113883.10.20.1.28" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5" assigningAuthorityName="IHE PCC"/>
									Problem observation template
									<id root="9d3d416d-45ab-4da1-912f-4583e0632000"/>
									<code displayName="Condition" code="64572001" codeSystemName="SNOMED-CT" codeSystem="2.16.840.1.113883.6.96"/>
									<text>
										<reference value="#PROBSUMMARY_3"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime>
										<low value="199903"/>
										<high nullFlavor="UNK"/>
									</effectiveTime>
									<value xsi:type="CD" displayName="Pneumonia" code="233604007" codeSystemName="SNOMED" codeSystem="2.16.840.1.113883.6.96"/>
								</observation>
							</entryRelationship>
						</act>
					</entry>
					<entry typeCode="DRIV">
						<act classCode="ACT" moodCode="EVN">
							<templateId root="2.16.840.1.113883.3.88.11.83.7" assigningAuthorityName="HITSP C83"/>
							<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.2" assigningAuthorityName="IHE PCC"/>
							Problem act template
							<id root="5a2c903c-bd77-4bd1-ad9d-452383fbfefa"/>
							<code nullFlavor="NA"/>
							<statusCode code="active"/>
							<effectiveTime>
								<low nullFlavor="UNK"/>
							</effectiveTime>
							<entryRelationship typeCode="SUBJ" inversionInd="false">
								<observation classCode="OBS" moodCode="EVN">
									<templateId root="2.16.840.1.113883.10.20.1.28" assigningAuthorityName="HL7 CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5" assigningAuthorityName="IHE PCC"/>
									Problem observation template
									<id/>
									<code displayName="Condition" code="64572001" codeSystemName="SNOMED-CT" codeSystem="2.16.840.1.113883.6.96"/>
									<text>
										<reference value="#PROBSUMMARY_4"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime>
										<low value="199701"/>
										<high nullFlavor="UNK"/>
									</effectiveTime>
									<value xsi:type="CD" displayName="Myocardial infarction" code="22298006" codeSystemName="SNOMED" codeSystem="2.16.840.1.113883.6.96"/>
								</observation>
							</entryRelationship>
						</act>
					</entry>
					<entry typeCode="DRIV">
						<act classCode="ACT" moodCode="EVN">
							<templateId root="2.16.840.1.113883.3.88.11.83.7" assigningAuthorityName="HITSP C83"/>
							<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.2" assigningAuthorityName="IHE PCC"/>
							Problem act template
							<id root="5a2c903c-bd77-4bd1-ad9d-452383fbfefa"/>
							<code nullFlavor="NA"/>
							<statusCode code="active"/>
							<effectiveTime>
								<low nullFlavor="UNK"/>
							</effectiveTime>
							<entryRelationship typeCode="SUBJ" inversionInd="false">
								<observation classCode="OBS" moodCode="EVN" negationInd="true">
									<templateId root="2.16.840.1.113883.10.20.1.28" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5" assigningAuthorityName="IHE PCC"/>
									Problem observation template
									<id/>
									<code displayName="Symptom" code="418799008" codeSystemName="SNOMED-CT" codeSystem="2.16.840.1.113883.6.96"/>
									<text>
										<reference value="#PROBSUMMARY_5"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime>
										<low nullFlavor="UNK"/>
										<high value="20101026"/>
									</effectiveTime>
									<value xsi:type="CD" displayName="Patient currently pregnant" code="77386006" codeSystemName="SNOMED" codeSystem="2.16.840.1.113883.6.96"/>
								</observation>
							</entryRelationship>
						</act>
					</entry> -->
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
									<th>Medication</th>
									<th>Dose</th>
									<th>Form</th>
									<th>Route</th>
									<th>Sig Text</th>
									<th>Dates</th>
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
							<!-- <xsl:apply-templates select="$meds/results/rows" mode="Medication"/> -->
								<!-- <tr ID="MEDSUMMARY_1">
									<td ID="MEDNAME_1">Albuterol inhalant</td>
									<td>2 puffs</td>
									<td>inhaler</td>
									<td>inhale</td>
									<td ID="SIGTXT_1">2 puffs QID PRN (as needed for wheezing)</td>
									<td>July 2005+</td>
									<td ID="MEDSTATUS_1">Active</td>
								</tr>
								<tr ID="MEDSUMMARY_2">
									<td ID="MEDNAME_2">clopidogrel (Plavix)</td>
									<td>75 mg</td>
									<td>tablet</td>
									<td>oral</td>
									<td ID="SIGTXT_2">75mg PO daily</td>
									<td>unknown</td>
									<td ID="MEDSTATUS_2">Active</td>
								</tr>
								<tr ID="MEDSUMMARY_3">
									<td ID="MEDNAME_3">Metoprolol</td>
									<td>25 mg</td>
									<td>tablet</td>
									<td>oral</td>
									<td ID="SIGTXT_3">25mg PO BID</td>
									<td>Nov 2007+</td>
									<td ID="MEDSTATUS_3">Active</td>
								</tr>
								<tr ID="MEDSUMMARY_4">
									<td ID="MEDNAME_4">prednisone (Deltasone)</td>
									<td>20 mg</td>
									<td>tablet</td>
									<td>oral</td>
									<td ID="SIGTXT_4">20mg PO daily</td>
									<td>Mar 28, 2000+</td>
									<td ID="MEDSTATUS_4">Active</td>
								</tr>
								<tr ID="MEDSUMMARY_5">
									<td ID="MEDNAME_5">cephalexin (Keflex)</td>
									<td>500 mg</td>
									<td>tablet</td>
									<td>oral</td>
									<td ID="SIGTXT_5">500mg PO QID x 7 days (for bronchitis)</td>
									<td>Mar-Apr 2000</td>
									<td ID="MEDSTATUS_5">Completed</td>
								</tr> -->
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
					<!-- <xsl:apply-templates select="$meds/results/rows" mode="MedicationDetail" /> -->
					<!-- <entry typeCode="DRIV">
						<substanceAdministration classCode="SBADM" moodCode="EVN">
							<templateId root="2.16.840.1.113883.3.88.11.83.8" assigningAuthorityName="HITSP C83"/>
							<templateId root="2.16.840.1.113883.10.20.1.24" assigningAuthorityName="CCD"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7" assigningAuthorityName="IHE PCC"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7.1" assigningAuthorityName="IHE PCC"/>
							Medication activity template
							<id root="cdbd33f0-6cde-11db-9fe1-0800200c9a66"/>
							<text>
								<reference value="#SIGTEXT_1"/>
							</text>
							<statusCode code="completed"/>
							<effectiveTime xsi:type="IVL_TS">
								<low value="200507"/>
								<high nullFlavor="UNK"/>
							</effectiveTime>
							<effectiveTime xsi:type="PIVL_TS">
								<period value="6" unit="h"/>
							</effectiveTime>
							The following route, dose and administrationUnit elements are HITSP/C83 Sig Components that are optional elements in a HITSP/C83 document. The dose and administrationUnit information is often inferable from the code (e.g. RxNorm code) of the consumable/manufacturedProduct. The route is often inferable from the administrativeUnit (e.g tablet implies oral route).
							<routeCode>
								<originalText>inhallation</originalText>
							</routeCode>
							<doseQuantity value="2" unit="puffs"/>
							<administrationUnitCode>
								<originalText>inhaler</originalText>
							</administrationUnitCode>
							<consumable>
								<manufacturedProduct>
									<templateId root="2.16.840.1.113883.3.88.11.83.8.2" assigningAuthorityName="HITSP C83"/>
									<templateId root="2.16.840.1.113883.10.20.1.53" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7.2" assigningAuthorityName="IHE PCC"/>
									Product template
									<manufacturedMaterial>
										<code code="307782" codeSystem="2.16.840.1.113883.6.88" displayName="Albuterol 0.09 MG/ACTUAT inhalant solution" codeSystemName="RxNorm">
											<originalText>Albuterol inhalant<reference/>
											</originalText>
										</code>
									</manufacturedMaterial>
								</manufacturedProduct>
							</consumable>
						</substanceAdministration>
					</entry>
					<entry typeCode="DRIV">
						<substanceAdministration classCode="SBADM" moodCode="EVN">
							<templateId root="2.16.840.1.113883.3.88.11.83.8" assigningAuthorityName="HITSP C83"/>
							<templateId root="2.16.840.1.113883.10.20.1.24" assigningAuthorityName="CCD"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7" assigningAuthorityName="IHE PCC"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7.1" assigningAuthorityName="IHE PCC"/>
							Medication activity template
							<id root="cdbd5b05-6cde-11db-9fe1-0800200c9a66"/>
							<text>
								<reference value="#SIGTEXT_2"/>
							</text>
							<statusCode code="completed"/>
							<effectiveTime xsi:type="IVL_TS">
								<low nullFlavor="UNK"/>
								<high nullFlavor="UNK"/>
							</effectiveTime>
							<effectiveTime xsi:type="PIVL_TS" institutionSpecified="false" operator="A">
								<period value="24" unit="h"/>
							</effectiveTime>
							The following route, dose and administrationUnit elements are HITSP/C83 Sig Components that are optional elements in a HITSP/C83 document. The dose and administrationUnit information is often inferable from the code (e.g. RxNorm code) of the consumable/manufacturedProduct. The route is often inferable from the administrativeUnit (e.g tablet implies oral route).
							<routeCode>
								<originalText>oral</originalText>
							</routeCode>
							<doseQuantity value="75" unit="mg"/>
							<administrationUnitCode>
								<originalText>tablet</originalText>
							</administrationUnitCode>
							<consumable>
								<manufacturedProduct>
									<templateId root="2.16.840.1.113883.3.88.11.83.8.2" assigningAuthorityName="HITSP C83"/>
									<templateId root="2.16.840.1.113883.10.20.1.53" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7.2" assigningAuthorityName="IHE PCC"/>
									Product template
									<manufacturedMaterial>
										<code code="309362" codeSystem="2.16.840.1.113883.6.88" displayName="Clopidogrel 75 MG oral tablet" codeSystemName="RxNorm">
											<originalText>Clopidogrel<reference/>
											</originalText>
											<translation code="174742" codeSystem="2.16.840.1.113883.6.88" displayName="Plavix" codeSystemName="RxNorm"/>
										</code>
										<name>Plavix</name>
									</manufacturedMaterial>
								</manufacturedProduct>
							</consumable>
						</substanceAdministration>
					</entry>
					<entry typeCode="DRIV">
						<substanceAdministration classCode="SBADM" moodCode="EVN">
							<templateId root="2.16.840.1.113883.3.88.11.83.8" assigningAuthorityName="HITSP C83"/>
							<templateId root="2.16.840.1.113883.10.20.1.24" assigningAuthorityName="CCD"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7" assigningAuthorityName="IHE PCC"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7.1" assigningAuthorityName="IHE PCC"/>
							Medication activity template
							<id root="cdbd5b01-6cde-11db-9fe1-0800200c9a66"/>
							<text>
								<reference value="#SIGTEXT_3"/>
							</text>
							<statusCode code="completed"/>
							<effectiveTime xsi:type="IVL_TS">
								<low value="20071121"/>
								<high nullFlavor="UNK"/>
							</effectiveTime>
							<effectiveTime xsi:type="PIVL_TS" institutionSpecified="false" operator="A">
								<period value="12" unit="h"/>
							</effectiveTime>
							The following route, dose and administrationUnit elements are HITSP/C83 Sig Components that are optional elements in a HITSP/C83 document. The dose and administrationUnit information is often inferable from the code (e.g. RxNorm code) of the consumable/manufacturedProduct. The route is often inferable from the administrativeUnit (e.g tablet implies oral route).
							<routeCode>
								<originalText>oral</originalText>
							</routeCode>
							<doseQuantity value="25" unit="mg"/>
							<administrationUnitCode>
								<originalText>tablet</originalText>
							</administrationUnitCode>
							<consumable>
								<manufacturedProduct>
									<templateId root="2.16.840.1.113883.3.88.11.83.8.2" assigningAuthorityName="HITSP C83"/>
									<templateId root="2.16.840.1.113883.10.20.1.53" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7.2" assigningAuthorityName="IHE PCC"/>
									Product template
									<manufacturedMaterial>
										<code code="430618" codeSystem="2.16.840.1.113883.6.88" displayName="Metoprolol 25 MG oral tablet">
											<originalText>Metoprolol<reference value="Pntr to Section text"/>
											</originalText>
										</code>
										<name>Generic Brand</name>
									</manufacturedMaterial>
								</manufacturedProduct>
							</consumable>
						</substanceAdministration>
					</entry>
					<entry typeCode="DRIV">
						<substanceAdministration classCode="SBADM" moodCode="EVN">
							<templateId root="2.16.840.1.113883.3.88.11.83.8" assigningAuthorityName="HITSP C83"/>
							<templateId root="2.16.840.1.113883.10.20.1.24" assigningAuthorityName="CCD"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7" assigningAuthorityName="IHE PCC"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7.1" assigningAuthorityName="IHE PCC"/>
							Medication activity template
							<id root="cdbd5b03-6cde-11db-9fe1-0800200c9a66"/>
							<text>
								<reference value="#SIGTEXT_4"/>
							</text>
							<statusCode code="completed"/>
							<effectiveTime xsi:type="IVL_TS">
								<low value="20000328"/>
								<high nullFlavor="UNK"/>
							</effectiveTime>
							<effectiveTime xsi:type="PIVL_TS" operator="A" institutionSpecified="false">
								<period value="24" unit="h"/>
							</effectiveTime>
							The following route, dose and administrationUnit elements are HITSP/C83 Sig Components that are optional elements in a HITSP/C83 document. The dose and administrationUnit information is often inferable from the code (e.g. RxNorm code) of the consumable/manufacturedProduct. The route is often inferable from the administrativeUnit (e.g tablet implies oral route).
							<routeCode>
								<originalText>oral</originalText>
							</routeCode>
							<doseQuantity value="20" unit="mg"/>
							<administrationUnitCode>
								<originalText>tablet</originalText>
							</administrationUnitCode>
							<consumable>
								<manufacturedProduct>
									<templateId root="2.16.840.1.113883.3.88.11.83.8.2" assigningAuthorityName="HITSP C83"/>
									<templateId root="2.16.840.1.113883.10.20.1.53" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7.2" assigningAuthorityName="IHE PCC"/>
									Product template
									<manufacturedMaterial>
										<code code="312615" codeSystem="2.16.840.1.113883.6.88" displayName="Prednisone 20 MG oral tablet">
											<originalText>Prednisone<reference/>
											</originalText>
											<translation code="227730" codeSystem="2.16.840.1.113883.6.88" displayName="Deltasone"/>
										</code>
										<name/>
									</manufacturedMaterial>
								</manufacturedProduct>
							</consumable>
						</substanceAdministration>
					</entry>
					<entry typeCode="DRIV">
						<substanceAdministration classCode="SBADM" moodCode="EVN">
							<templateId root="2.16.840.1.113883.3.88.11.83.8" assigningAuthorityName="HITSP C83"/>
							<templateId root="2.16.840.1.113883.10.20.1.24" assigningAuthorityName="CCD"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7" assigningAuthorityName="IHE PCC"/>
							<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7.1" assigningAuthorityName="IHE PCC"/>
							Medication activity template
							<id root="cdbd5b07-6cde-11db-9fe1-0800200c9a66"/>
							<text>
								<reference value="#SIGTEXT_5"/>
							</text>
							<statusCode code="completed"/>
							<effectiveTime xsi:type="IVL_TS">
								<low value="20000328"/>
								<high value="20000404"/>
							</effectiveTime>
							<effectiveTime xsi:type="PIVL_TS" operator="A" institutionSpecified="false">
								<period value="6" unit="h"/>
							</effectiveTime>
							The following route, dose and administrationUnit elements are HITSP/C83 Sig Components that are optional elements in a HITSP/C83 document. The dose and administrationUnit information is often inferable from the code (e.g. RxNorm code) of the consumable/manufacturedProduct. The route is often inferable from the administrativeUnit (e.g tablet implies oral route).
							<routeCode>
								<originalText>oral</originalText>
							</routeCode>
							<doseQuantity value="500" unit="mg"/>
							<administrationUnitCode>
								<originalText>tablet</originalText>
							</administrationUnitCode>
							<consumable>
								<manufacturedProduct>
									<templateId root="2.16.840.1.113883.3.88.11.83.8.2" assigningAuthorityName="HITSP C83"/>
									<templateId root="2.16.840.1.113883.10.20.1.53" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7.2" assigningAuthorityName="IHE PCC"/>
									Product template
									<manufacturedMaterial>
										<code code="197454" codeSystem="2.16.840.1.113883.6.88" displayName="Cephalexin 500 MG oral tablet">
											<originalText>Cephalexin<reference/>
											</originalText>
											<translation code="203167" codeSystem="2.16.840.1.113883.6.88" displayName="Keflex" codeSystemName="RxNorm"/>
										</code>
										<name>Keflex</name>
									</manufacturedMaterial>
								</manufacturedProduct>
							</consumable>
						</substanceAdministration>
					</entry> -->
				</section>
			</component>
			<component>
				<!--Results-->
				<section>
					<templateId root="2.16.840.1.113883.3.88.11.83.122" assigningAuthorityName="HITSP/C83"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.3.28" assigningAuthorityName="IHE PCC"/>
					<!--Diagnostic Results section template-->
					<code code="30954-2" codeSystem="2.16.840.1.113883.6.1" codeSystemName="LOINC" displayName="Results"/>
					<title>Diagnostic Results</title>
					<text>
						<table border="1" width="100%">
							<xsl:if test="$totalLabResultNodes > 0">
								<xsl:call-template name="LabResultSummary">
									<xsl:with-param name="labResultNode" select="$labtests/results/rows"/>
									<xsl:with-param name="totalLabResultNodes" select="$totalLabResultNodes"/>
									<xsl:with-param name="labResultNodeCnt" select="1"/>
								</xsl:call-template>	
							</xsl:if>
							<!-- <thead>
								<tr>
									<th>&#160;</th>
									<th>March 23, 2000</th>
									<th>April 06, 2000</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td colspan="3">
										<content styleCode="BoldItalics">Hematology</content>
									</td>
								</tr>
								<tr>
									<td>HGB (M 13-18 g/dl; F 12-16 g/dl)</td>
									<td>13.2</td>
									<td>&#160;</td>
								</tr>
								<tr>
									<td>WBC (4.3-10.8 10+3/ul)</td>
									<td>6.7</td>
									<td>&#160;</td>
								</tr>
								<tr>
									<td>PLT (135-145 meq/l)</td>
									<td>123 (L)</td>
									<td>&#160;</td>
								</tr>
								<tr>
									<td colspan="3">
										<content styleCode="BoldItalics">Chemistry</content>
									</td>
								</tr>
								<tr>
									<td>NA (135-145meq/l)</td>
									<td>&#160;</td>
									<td>140</td>
								</tr>
								<tr>
									<td>K (3.5-5.0 meq/l)</td>
									<td>&#160;</td>
									<td>4.0</td>
								</tr>
								<tr>
									<td>CL (98-106 meq/l)</td>
									<td>&#160;</td>
									<td>102</td>
								</tr>
								<tr>
									<td>HCO3 (18-23 meq/l)</td>
									<td>&#160;</td>
									<td>35 (H)</td>
								</tr>
							</tbody> -->
						</table>
					</text>
					<!--HITSP/C83 requires Diagnostic Results to have both Procedure and Results, but gives no guidance as to how they should be related together. If Result requires a specimen, the Procedure to obtain the specimen could be under the specimen. This example simply groups a Procedure with the Results obtained from that Procedure. This is probably not the best way to do it, but satisfies the requirements of both IHE/PCC and HITSP/C83 for Diagnostic Results. -->
					<xsl:if test="$totalLabResultNodes > 0">
						<xsl:call-template name="LabResultDetail">
							<xsl:with-param name="labtestDetailNode" select="$labtests/results/rows"/>
							<xsl:with-param name="totalLabResultNodes" select="$totalLabResultNodes"/>
							<xsl:with-param name="labtestDetailNodeCnt" select="1"/>
						</xsl:call-template>	
					</xsl:if>
					<!-- <entry typeCode="DRIV">
						<organizer classCode="BATTERY" moodCode="EVN">
							<templateId root="2.16.840.1.113883.10.20.1.32"/>
							Result organizer template
							<id root="7d5a02b0-67a4-11db-bd13-0800200c9a66"/>
							<code code="43789009" codeSystem="2.16.840.1.113883.6.96" displayName="CBC WO DIFFERENTIAL"/>
							<statusCode code="completed"/>
							<effectiveTime value="200003231430"/>
							<component>
								<procedure classCode="PROC" moodCode="EVN">
									<templateId root="2.16.840.1.113883.3.88.11.83.17" assigningAuthorityName="HITSP C83"/>
									<templateId root="2.16.840.1.113883.10.20.1.29" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.19" assigningAuthorityName="IHE PCC"/>
									<id/>
									<code code="43789009" codeSystem="2.16.840.1.113883.6.96" displayName="CBC WO DIFFERENTIAL">
										<originalText>Extract blood for CBC test<reference value="Ptr to text  in parent Section"/>
										</originalText>
									</code>
									<text>Extract blood for CBC test. Note that IHE rules require description and reference to go here rather than in originalText of code.<reference value="Ptr to text  in parent Section"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime value="200003231430"/>
								</procedure>
							</component>
							<component>
								<observation classCode="OBS" moodCode="EVN">
									<templateId root="2.16.840.1.113883.3.88.11.83.15.1" assigningAuthorityName="HITSP C83"/>
									<templateId root="2.16.840.1.113883.10.20.1.31" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.13" assigningAuthorityName="IHE PCC"/>
									Result observation template
									<id root="107c2dc0-67a5-11db-bd13-0800200c9a66"/>
									<code code="30313-1" codeSystem="2.16.840.1.113883.6.1" displayName="HGB"/>
									<text>
										<reference value="PtrToValueInsectionText"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime value="200003231430"/>
									<value xsi:type="PQ" value="13.2" unit="g/dl"/>
									<interpretationCode code="N" codeSystem="2.16.840.1.113883.5.83"/>
								</observation>
							</component>
							<component>
								<observation classCode="OBS" moodCode="EVN">
									<templateId root="2.16.840.1.113883.3.88.11.83.15.1" assigningAuthorityName="HITSP C83"/>
									<templateId root="2.16.840.1.113883.10.20.1.31" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.13" assigningAuthorityName="IHE PCC"/>
									Result observation template
									<id root="8b3fa370-67a5-11db-bd13-0800200c9a66"/>
									<code code="33765-9" codeSystem="2.16.840.1.113883.6.1" displayName="WBC"/>
									<text>
										<reference value="PtrToValueInsectionText"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime value="200003231430"/>
									<value xsi:type="PQ" value="6.7" unit="10+3/ul"/>
									<interpretationCode code="N" codeSystem="2.16.840.1.113883.5.83"/>
								</observation>
							</component>
							<component>
								<observation classCode="OBS" moodCode="EVN">
									<templateId root="2.16.840.1.113883.10.20.1.31" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.13" assigningAuthorityName="IHE PCC"/>
									Result observation template
									<id root="80a6c740-67a5-11db-bd13-0800200c9a66"/>
									<code code="26515-7" codeSystem="2.16.840.1.113883.6.1" displayName="PLT"/>
									<text>
										<reference value="PtrToValueInsectionText"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime value="200003231430"/>
									<value xsi:type="PQ" value="123" unit="10+3/ul"/>
									<interpretationCode code="L" codeSystem="2.16.840.1.113883.5.83"/>
								</observation>
							</component>
						</organizer>
					</entry>
					<entry typeCode="DRIV">
						<organizer classCode="BATTERY" moodCode="EVN">
							<templateId root="2.16.840.1.113883.10.20.1.32"/>
							Result organizer template
							<id root="a40027e0-67a5-11db-bd13-0800200c9a66"/>
							<code code="20109005" codeSystem="2.16.840.1.113883.6.96" displayName="LYTES" codeSystemName="SNOMED CT"/>
							<statusCode code="completed"/>
							<effectiveTime value="200004061300"/>
							<component>
								<procedure classCode="PROC" moodCode="EVN">
									<templateId root="2.16.840.1.113883.3.88.11.83.17" assigningAuthorityName="HITSP C83"/>
									<templateId root="2.16.840.1.113883.10.20.1.29" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.19" assigningAuthorityName="IHE PCC"/>
									<id/>
									<code code="20109005" codeSystem="2.16.840.1.113883.6.96" displayName="LYTES" codeSystemName="SNOMED CT">
										<originalText>Extract blood for electrolytes tests<reference value="Ptr to text  in parent Section"/>
										</originalText>
									</code>
									<text>Extract blood for electrolytes tests. IHE rules require description and reference to go here rather than in originalText of code.<reference value="Ptr to text  in parent Section"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime value="200004061300"/>
								</procedure>
							</component>
							<component>
								<observation classCode="OBS" moodCode="EVN">
									<templateId root="2.16.840.1.113883.3.88.11.83.15.1" assigningAuthorityName="HITSP C83"/>
									<templateId root="2.16.840.1.113883.10.20.1.31" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.13" assigningAuthorityName="IHE PCC"/>
									Result observation template
									<id root="a40027e1-67a5-11db-bd13-0800200c9a66"/>
									<code code="2951-2" codeSystem="2.16.840.1.113883.6.1" displayName="NA"/>
									<text>
										<reference value="PtrToValueInsectionText"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime value="200004061300"/>
									<value xsi:type="PQ" value="140" unit="meq/l"/>
									<interpretationCode code="N" codeSystem="2.16.840.1.113883.5.83"/>
								</observation>
							</component>
							<component>
								<observation classCode="OBS" moodCode="EVN">
									<templateId root="2.16.840.1.113883.3.88.11.83.15.1" assigningAuthorityName="HITSP C83"/>
									<templateId root="2.16.840.1.113883.10.20.1.31" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.13" assigningAuthorityName="IHE PCC"/>
									Result observation template
									<id root="a40027e2-67a5-11db-bd13-0800200c9a66"/>
									<code code="2823-3" codeSystem="2.16.840.1.113883.6.1" displayName="K"/>
									<text>
										<reference value="PtrToValueInsectionText"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime value="200004061300"/>
									<value xsi:type="PQ" value="4.0" unit="meq/l"/>
									<interpretationCode code="N" codeSystem="2.16.840.1.113883.5.83"/>
								</observation>
							</component>
							<component>
								<observation classCode="OBS" moodCode="EVN">
									<templateId root="2.16.840.1.113883.3.88.11.83.15.1" assigningAuthorityName="HITSP C83"/>
									<templateId root="2.16.840.1.113883.10.20.1.31" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.13" assigningAuthorityName="IHE PCC"/>
									Result observation template
									<id root="a40027e3-67a5-11db-bd13-0800200c9a66"/>
									<code code="2075-0" codeSystem="2.16.840.1.113883.6.1" displayName="CL"/>
									<text>
										<reference value="PtrToValueInsectionText"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime value="200004061300"/>
									<value xsi:type="PQ" value="102" unit="meq/l"/>
									<interpretationCode code="N" codeSystem="2.16.840.1.113883.5.83"/>
								</observation>
							</component>
							<component>
								<observation classCode="OBS" moodCode="EVN">
									<templateId root="2.16.840.1.113883.3.88.11.83.15.1" assigningAuthorityName="HITSP C83"/>
									<templateId root="2.16.840.1.113883.10.20.1.31" assigningAuthorityName="CCD"/>
									<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.13" assigningAuthorityName="IHE PCC"/>
									Result observation template
									<id root="a40027e4-67a5-11db-bd13-0800200c9a66"/>
									<code code="1963-8" codeSystem="2.16.840.1.113883.6.1" displayName="HCO3"/>
									<text>
										<reference value="PtrToValueInsectionText"/>
									</text>
									<statusCode code="completed"/>
									<effectiveTime value="200004061300"/>
									<value xsi:type="PQ" value="35" unit="meq/l"/>
									<interpretationCode code="H" codeSystem="2.16.840.1.113883.5.83"/>
								</observation>
							</component>
						</organizer>
					</entry> -->
				</section>
			</component>
		</structuredBody>
	</component>
</ClinicalDocument>
</xsl:template>


<xsl:template name="AllergySummary" xmlns="urn:hl7-org:v3">
	<xsl:param name="allergyNode"/>
	<xsl:param name="allergyNodeCnt" select="0"/>
	<xsl:param name="totalAllergyNodes" select="0" />
	<tr>
		<xsl:attribute name="ID">
			<xsl:value-of select="concat('ALGSUMMARY_', $allergyNodeCnt)" />
		</xsl:attribute>	
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('ALGTYPE_', $allergyNodeCnt)" />
			</xsl:attribute>	
			<xsl:value-of select="$allergyNode/child::node()[position()=$allergyNodeCnt]/DrugType" />..TODO:needs fixing
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
			<xsl:value-of select="$allergyNode/child::node()[position()=$allergyNodeCnt]/reaction" />..TODO:needs fixing
		</td>
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('ALGSTATUS_', $allergyNodeCnt)" />
			</xsl:attribute>
			<xsl:value-of select="$allergyNode/child::node()[position()=$allergyNodeCnt]/Status" />
		</td>
	</tr>
	 <xsl:if test="$totalAllergyNodes > $allergyNodeCnt">
      <xsl:call-template name="AllergySummary">
      	<xsl:with-param name="allergyNode" select="$allergyNode"/>
        <xsl:with-param name="totalAllergyNodes" select="$totalAllergyNodes" />
        <xsl:with-param name="allergyNodeCnt" select="$allergyNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>


				
<xsl:template name="ProblemSummary">
	<xsl:param name="problemNode"/>
	<xsl:param name="problemNodeCnt" select="0"/>
	<xsl:param name="totalProblemNodes" select="0" />
	<tr>
		<xsl:attribute name="ID">
			<xsl:value-of select="concat('PROBSUMMARY_', $problemNodeCnt)" />
		</xsl:attribute>	
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('PROBKIND_', $problemNodeCnt)" />
			</xsl:attribute>	
			<xsl:value-of select="$problemNode/child::node()[position()=$problemNodeCnt]/Problem" />
		</td>
		<td>
			<xsl:value-of select="substring-before($problemNode/child::node()[position()=$problemNodeCnt]/Date, ' ')" />
		</td>
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('PROBSTATUS_', $problemNodeCnt)" />
			</xsl:attribute>
			<xsl:value-of select="$problemNode/child::node()[position()=$problemNodeCnt]/Status" />
		</td>
	</tr>
	 <xsl:if test="$totalProblemNodes > $problemNodeCnt">
      <xsl:call-template name="ProblemSummary">
      	<xsl:with-param name="problemNode" select="$problemNode"/>
        <xsl:with-param name="totalProblemNodes" select="$totalProblemNodes" />
        <xsl:with-param name="problemNodeCnt" select="$problemNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>				


<xsl:template name="MedicationSummary">
	<xsl:param name="medNode"/>
	<xsl:param name="medNodeCnt" select="0"/>
	<xsl:param name="totalMedicationNodes" select="0" />
	<tr>
		<xsl:attribute name="ID">
			<xsl:value-of select="concat('MEDSUMMARY_', $medNodeCnt)" />
		</xsl:attribute>	
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('MEDNAME_', $medNodeCnt)" />
			</xsl:attribute>	
			<xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/name" />
		</td>
		<td><xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/name" /></td>
		<td><xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/name" /></td>
		<td><xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/route" /></td>
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('SIGTXT_', $medNodeCnt)" />
			</xsl:attribute>
			<xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/instructions" />			
		</td>
		<td><xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/start" /></td>
		<td>
			<xsl:attribute name="ID">
				<xsl:value-of select="concat('MEDSTATUS_', $medNodeCnt)" />
			</xsl:attribute>
			<xsl:value-of select="$medNode/child::node()[position()=$medNodeCnt]/actStatus" />
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

<xsl:template name="LabResultSummary">
	<xsl:param name="labResultNode"/>
	<xsl:param name="labResultNodeCnt" select="0"/>
	<xsl:param name="totalLabResultNodes" select="0" />

	<thead>
		<tr>
			<th><xsl:value-of select="$labResultNode/child::node()[position()=$labResultNodeCnt]/Date" />..TODO:needs fixing</th><!-- March 23, 2000 -->			
		</tr>
	</thead>
	<tbody>
		<tr>
			<td colspan="3">
				<content styleCode="BoldItalics">Hematology</content>
			</td>
		</tr>
		<tr>
			<td>HGB (M 13-18 g/dl; F 12-16 g/dl)</td>
			<td>13.2</td>
			<td>&#160;</td>
		</tr>
		<tr>
			<td>WBC (4.3-10.8 10+3/ul)</td>
			<td>6.7</td>
			<td>&#160;</td>
		</tr>
		<tr>
			<td>PLT (135-145 meq/l)</td>
			<td>123 (L)</td>
			<td>&#160;</td>
		</tr>
		<tr>
			<td colspan="3">
				<content styleCode="BoldItalics">Chemistry</content>
			</td>
		</tr>
		<tr>
			<td>NA (135-145meq/l)</td>
			<td>&#160;</td>
			<td>140</td>
		</tr>
		<tr>
			<td>K (3.5-5.0 meq/l)</td>
			<td>&#160;</td>
			<td>4.0</td>
		</tr>
		<tr>
			<td>CL (98-106 meq/l)</td>
			<td>&#160;</td>
			<td>102</td>
		</tr>
		<tr>
			<td>HCO3 (18-23 meq/l)</td>
			<td>&#160;</td>
			<td>35 (H)</td>
		</tr>
	</tbody>
							
	 <xsl:if test="$totalLabResultNodes > $labResultNodeCnt">
      <xsl:call-template name="LabResultSummary">
      	<xsl:with-param name="labResultNode" select="$labResultNode"/>
        <xsl:with-param name="totalLabResultNodes" select="$totalLabResultNodes" />
        <xsl:with-param name="labResultNodeCnt" select="$labResultNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>

<xsl:template name="AllergyDetail">
		<xsl:param name="allergyDetailNode"/>
		<xsl:param name="allergyDetailNodeCnt" select="0"/>
		<xsl:param name="totalAllergyNodes" select="0" />
	<entry typeCode="DRIV">
		<act classCode="ACT" moodCode="EVN">
			<templateId root="2.16.840.1.113883.3.88.11.83.6" assigningAuthorityName="HITSP C83"/>
			<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.3" assigningAuthorityName="IHE PCC"/>
			<!--Allergy act template -->
			<!-- <id root="36e3e930-7b14-11db-9fe1-0800200c9a66"/>
			<code nullFlavor="NA"/>
			<statusCode code="completed"/>
			<effectiveTime>
				<low nullFlavor="UNK"/>
				<high nullFlavor="UNK"/>
			</effectiveTime> -->
			<entryRelationship typeCode="SUBJ" inversionInd="false">
				<observation classCode="OBS" moodCode="EVN">
					<templateId root="2.16.840.1.113883.10.20.1.18" assigningAuthorityName="CCD"/>
					<templateId root="2.16.840.1.113883.10.20.1.28" assigningAuthorityName="CCD"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5" assigningAuthorityName="IHE PCC"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.6" assigningAuthorityName="IHE PCC"/>
					<templateId root="2.16.840.1.113883.10.20.1.18"/>
					<!--Allergy observation template. NOTE that the HITSP/C83 requirement for code (i.e. allergy type) differs from the IHE PCC recommendation for code.-->
					<!-- <id root="4adc1020-7b14-11db-9fe1-0800200c9a66" extension=""/> -->
					<id>
						<xsl:attribute name="root">
			 				<xsl:value-of select="$ctx/context/accountIdRoot/@id" />
						</xsl:attribute>			
						<xsl:attribute name="extension">
							<xsl:value-of select="$allergyDetailNode/child::node()[position()=$allergyDetailNodeCnt]/path" />
						</xsl:attribute>
					</id>								
					
					<code code="416098002" codeSystem="2.16.840.1.113883.6.96" displayName="drug allergy" codeSystemName="SNOMED CT"/>
					<text>
						<reference>
							<xsl:attribute name="value">
								<xsl:value-of select="concat('#ALGSUMMARY_', $allergyDetailNodeCnt)" />
							</xsl:attribute>
						</reference>
					</text>
					<statusCode>
						<xsl:attribute name="code"><xsl:value-of select="$allergyDetailNode/child::node()[position()=$allergyDetailNodeCnt]/actStatus" /></xsl:attribute>
					</statusCode>
					<effectiveTime>
						<xsl:value-of select="$allergyDetailNode/child::node()[position()=$allergyDetailNodeCnt]/effectiveTime" />
						<low nullFlavor="UNK"/>
					</effectiveTime>
					<!--Note that IHE/PCC and HITSP/C32 differ in how to represent the drug, substance, or food that one is allergic to. IHE/PCC expects to see that information in <value> and HITSP/C32 expects to see it in <participant>.-->
					<value codeSystem="2.16.840.1.113883.6.88"  codeSystemName="RxNorm">
						<xsl:attribute name="xsi:type">CD</xsl:attribute>
						<xsl:attribute name="displayName"><xsl:value-of select="$allergyDetailNode/child::node()[position()=$allergyDetailNodeCnt]/Allergy" /></xsl:attribute>
						<xsl:attribute name="code"><xsl:value-of select="$allergyDetailNode/child::node()[position()=$allergyDetailNodeCnt]/Code" /></xsl:attribute>
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
									<xsl:attribute name="xsi:type">CD</xsl:attribute>
									<xsl:attribute name="displayName"><xsl:value-of select="$allergyDetailNode/child::node()[position()=$allergyDetailNodeCnt]/Allergy" /></xsl:attribute>
									<xsl:attribute name="code"><xsl:value-of select="$allergyDetailNode/child::node()[position()=$allergyDetailNodeCnt]/Code" /></xsl:attribute>
									<originalText>
										<reference>
											<xsl:attribute name="value">
												<xsl:value-of select="concat('#ALGSUB_', $allergyDetailNodeCnt)" />
											</xsl:attribute>
										</reference>
									</originalText>
								</code>
								<name><xsl:value-of select="$allergyDetailNode/child::node()[position()=$allergyDetailNodeCnt]/Allergy" /></name>
							</playingEntity>
						</participantRole>
					</participant>
				</observation>
			</entryRelationship>
		</act>
	</entry>
	<xsl:if test="$totalAllergyNodes > $allergyDetailNodeCnt">
      <xsl:call-template name="AllergyDetail">
      	<xsl:with-param name="allergyDetailNode" select="$allergyDetailNode"/>
        <xsl:with-param name="totalAllergyNodes" select="$totalAllergyNodes" />
        <xsl:with-param name="allergyDetailNodeCnt" select="$allergyDetailNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>
<!-- <xsl:template match="/results/rows/row" mode="AllergyDetail">
	<entry typeCode="DRIV">
		<act classCode="ACT" moodCode="EVN">
			<templateId root="2.16.840.1.113883.3.88.11.83.6" assigningAuthorityName="HITSP C83"/>
			<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.3" assigningAuthorityName="IHE PCC"/>
			Allergy act template
			<id root="36e3e930-7b14-11db-9fe1-0800200c9a66"/>
			<code nullFlavor="NA"/>
			<statusCode code="completed"/>
			<effectiveTime>
				<low nullFlavor="UNK"/>
				<high nullFlavor="UNK"/>
			</effectiveTime>
			<entryRelationship typeCode="SUBJ" inversionInd="false">
				<observation classCode="OBS" moodCode="EVN">
					<templateId root="2.16.840.1.113883.10.20.1.18" assigningAuthorityName="CCD"/>
					<templateId root="2.16.840.1.113883.10.20.1.28" assigningAuthorityName="CCD"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5" assigningAuthorityName="IHE PCC"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.6" assigningAuthorityName="IHE PCC"/>
					<templateId root="2.16.840.1.113883.10.20.1.18"/>
					Allergy observation template. NOTE that the HITSP/C83 requirement for code (i.e. allergy type) differs from the IHE PCC recommendation for code.
					<id root="4adc1020-7b14-11db-9fe1-0800200c9a66" extension=""/>
					<id>
						<xsl:attribute name="root">
			 				<xsl:value-of select="$ctx/context/accountIdRoot/@id" />
						</xsl:attribute>			
						<xsl:attribute name="extension">
							<xsl:value-of select="./path" />
						</xsl:attribute>
					</id>								
					
					<code code="416098002" codeSystem="2.16.840.1.113883.6.96" displayName="drug allergy" codeSystemName="SNOMED CT"/>
					<text>
						<reference value="#ALGSUMMARY_1"/>
					</text>
					<statusCode>
						<xsl:attribute name="code"><xsl:value-of select="./actStatus" /></xsl:attribute>
					</statusCode>
					<effectiveTime>
						<low>
							<xsl:attribute name="value">
								<xsl:value-of select="translate(substring-before(./Date, ' '), '-', '')" />
							</xsl:attribute>
						</low>
						<xsl:value-of select="./effectiveTime" />
						<low nullFlavor="UNK"/>
					</effectiveTime>
					Note that IHE/PCC and HITSP/C32 differ in how to represent the drug, substance, or food that one is allergic to. IHE/PCC expects to see that information in <value> and HITSP/C32 expects to see it in <participant>.
					<value codeSystem="2.16.840.1.113883.6.88"  codeSystemName="RxNorm">
						<xsl:attribute name="xsi:type">CD</xsl:attribute>
						<xsl:attribute name="displayName"><xsl:value-of select="./Allergy" /></xsl:attribute>
						<xsl:attribute name="code"><xsl:value-of select="./Code" /></xsl:attribute>
						<originalText>
							<reference value="#ALGSUB_1"/>
						</originalText>
					</value>
					<participant typeCode="CSM">
						<participantRole classCode="MANU">
							<addr/>
							<telecom/>
							<playingEntity classCode="MMAT">
								<code codeSystem="2.16.840.1.113883.6.88" codeSystemName="RxNorm">
									<xsl:attribute name="xsi:type">CD</xsl:attribute>
									<xsl:attribute name="displayName"><xsl:value-of select="./Allergy" /></xsl:attribute>
									<xsl:attribute name="code"><xsl:value-of select="./Code" /></xsl:attribute>
									<originalText>
										<reference value="#ALGSUB_1"/>
									</originalText>
								</code>
								<name><xsl:value-of select="./Allergy" /></name>
							</playingEntity>
						</participantRole>
					</participant>
				</observation>
			</entryRelationship>
		</act>
	</entry>
	</xsl:template> -->
<xsl:template name="ProblemDetail">
		<xsl:param name="problemDetailNode"/>
		<xsl:param name="problemDetailNodeCnt" select="0"/>
		<xsl:param name="totalProblemNodes" select="0" />
	<!-- <xsl:template match="/results/rows/row" mode="ProblemDetail"> -->
	<entry typeCode="DRIV">
		<act classCode="ACT" moodCode="EVN">
			<templateId root="2.16.840.1.113883.3.88.11.83.7" assigningAuthorityName="HITSP C83"/>
			<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.2" assigningAuthorityName="IHE PCC"/>
			<!-- Problem act template -->
			<!-- <id root="6a2fa88d-4174-4909-aece-db44b60a3abb"/>
			<code nullFlavor="NA"/>
			<statusCode code="completed"/>
			<effectiveTime>
				<low value="1950"/>
				<high nullFlavor="UNK"/>
			</effectiveTime> -->
			<entryRelationship typeCode="SUBJ" inversionInd="false">
				<observation classCode="OBS" moodCode="EVN">
					<templateId root="2.16.840.1.113883.10.20.1.28" assigningAuthorityName="CCD"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5" assigningAuthorityName="IHE PCC"/>
					<!--Problem observation template - NOT episode template-->
					<!-- <id root="d11275e7-67ae-11db-bd13-0800200c9a66"/> -->
					<id>
						<xsl:attribute name="root">
			 				<xsl:value-of select="$ctx/context/accountIdRoot/@id" />
						</xsl:attribute>			
						<xsl:attribute name="extension">
							<xsl:value-of select="$problemDetailNode/child::node()[position()=$problemDetailNodeCnt]/path" />
						</xsl:attribute>
					</id>	
					<code code="64572001" displayName="Condition" codeSystem="2.16.840.1.113883.6.96" codeSystemName="SNOMED-CT"/>
					<text>
						<!-- <reference value="#PROBSUMMARY_1"/> -->
						<reference>
							<xsl:attribute name="value">
								<xsl:value-of select="concat('#PROBSUMMARY_', $problemDetailNodeCnt)" />
							</xsl:attribute>
						</reference>
					</text>
					<statusCode>
						<xsl:attribute name="code">
							<xsl:value-of select="$problemDetailNode/child::node()[position()=$problemDetailNodeCnt]/Status" />
						</xsl:attribute>
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
	<xsl:if test="$totalProblemNodes > $problemDetailNodeCnt">
      <xsl:call-template name="ProblemDetail">
      	<xsl:with-param name="problemDetailNode" select="$problemDetailNode"/>
        <xsl:with-param name="totalProblemNodes" select="$totalProblemNodes" />
        <xsl:with-param name="problemDetailNodeCnt" select="$problemDetailNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>
	<!-- <entry typeCode="DRIV">
		<act classCode="ACT" moodCode="EVN">
			<templateId root="2.16.840.1.113883.3.88.11.83.7" assigningAuthorityName="HITSP C83"/>
			<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.2" assigningAuthorityName="IHE PCC"/>
			Problem act template
			<id root="6a2fa88d-4174-4909-aece-db44b60a3abb"/>
			<code nullFlavor="NA"/>
			<statusCode code="completed"/>
			<effectiveTime>
				<low value="1950"/>
				<low>
					<xsl:attribute name="value">
						<xsl:value-of select="translate(substring-before(./Date, ' '), '-', '')" />
					</xsl:attribute>
				</low>
				<high nullFlavor="UNK"/>
			</effectiveTime>
			<entryRelationship typeCode="SUBJ" inversionInd="false">
				<observation classCode="OBS" moodCode="EVN">
					<templateId root="2.16.840.1.113883.10.20.1.28" assigningAuthorityName="CCD"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5" assigningAuthorityName="IHE PCC"/>
					Problem observation template - NOT episode template
					<id root="d11275e7-67ae-11db-bd13-0800200c9a66"/>
					<id>
						<xsl:attribute name="root">
			 				<xsl:value-of select="$ctx/context/accountIdRoot/@id" />
						</xsl:attribute>			
						<xsl:attribute name="extension">
							<xsl:value-of select="./path" />
						</xsl:attribute>
					</id>	
					<code code="64572001" displayName="Condition" codeSystem="2.16.840.1.113883.6.96" codeSystemName="SNOMED-CT"/>
					<text>
						<reference value="#PROBSUMMARY_1"/>
					</text>
					<statusCode>
						<xsl:attribute name="code"><xsl:value-of select="./Status" /></xsl:attribute>
					</statusCode>
					<effectiveTime>
						<low value="1950"/>
						<low>
							<xsl:attribute name="value">
								<xsl:value-of select="translate(substring-before(./Date, ' '), '-', '')" />
							</xsl:attribute>
						</low>
					</effectiveTime>
					<value xsi:type="CD" displayName="Asthma" code="195967001" codeSystemName="SNOMED" codeSystem="2.16.840.1.113883.6.96"/>
					<value codeSystemName="SNOMED" codeSystem="2.16.840.1.113883.6.96">
						<xsl:attribute name="xsi:type">CD</xsl:attribute>
						<xsl:attribute name="displayName"><xsl:value-of select="./Problem" /></xsl:attribute>
						<xsl:attribute name="code"><xsl:value-of select="./Code" /></xsl:attribute>
					</value>
				</observation>
			</entryRelationship>
		</act>
	</entry>-->

	<xsl:template name="MedicationDetail">
		<xsl:param name="medDetailNode"/>
		<xsl:param name="medDetailNodeCnt" select="0"/>
		<xsl:param name="totalMedicationNodes" select="0" />
	<!-- <xsl:template match="/results/rows/row" mode="MedicationDetail"> -->
	<entry typeCode="DRIV">
		<substanceAdministration classCode="SBADM" moodCode="EVN">
			<templateId root="2.16.840.1.113883.3.88.11.83.8" assigningAuthorityName="HITSP C83"/>
			<templateId root="2.16.840.1.113883.10.20.1.24" assigningAuthorityName="CCD"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7" assigningAuthorityName="IHE PCC"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7.1" assigningAuthorityName="IHE PCC"/>
			<!--Medication activity template -->
			<!-- <id root="cdbd33f0-6cde-11db-9fe1-0800200c9a66"/> -->
			<id>
				<xsl:attribute name="root">
	 				<xsl:value-of select="$ctx/context/accountIdRoot/@id" />
				</xsl:attribute>			
				<xsl:attribute name="extension">
					<xsl:value-of select="$medDetailNode/child::node()[position()=$medDetailNodeCnt]/path" />
				</xsl:attribute>
			</id>	
			<text>				
				<reference>
					<xsl:attribute name="value">
						<xsl:value-of select="concat('#SIGTEXT_', $medDetailNodeCnt)" />
					</xsl:attribute>
				</reference>
			</text>
			<statusCode>
				<xsl:attribute name="code"><xsl:value-of select="$medDetailNode/child::node()[position()=$medDetailNodeCnt]/actStatus" /></xsl:attribute>
			</statusCode>
			<effectiveTime>
				<xsl:attribute name="xsi:type">IVL_TS</xsl:attribute>
				<!-- <low value="200507"/> -->
				<low>
					<xsl:attribute name="value">
						<xsl:value-of select="translate(substring-before($medDetailNode/child::node()[position()=$medDetailNodeCnt]/start, ' '), '-', '')" />
					</xsl:attribute>
				</low>
				<high nullFlavor="UNK"/>
			</effectiveTime>
			<effectiveTime>
				<xsl:attribute name="xsi:type">PIVL_TS</xsl:attribute>
				<!-- <period value="6" unit="h"/> -->
				<period>
					<xsl:attribute name="value"><xsl:value-of select="substring($medDetailNode/child::node()[position()=$medDetailNodeCnt]/frequency,1,1)" /></xsl:attribute>
					<xsl:attribute name="unit"><xsl:value-of select="substring($medDetailNode/child::node()[position()=$medDetailNodeCnt]/frequency,2,1)" /></xsl:attribute>
				</period>
			</effectiveTime>
			<!--The following route, dose and administrationUnit elements are HITSP/C83 Sig Components that are optional elements in a HITSP/C83 document. The dose and administrationUnit information is often inferable from the code (e.g. RxNorm code) of the consumable/manufacturedProduct. The route is often inferable from the administrativeUnit (e.g tablet implies oral route). -->
			<routeCode>
				<originalText><xsl:value-of select="$medDetailNode/child::node()[position()=$medDetailNodeCnt]/route" /></originalText>
			</routeCode>
			<!-- <doseQuantity value="2" unit="puffs"/> -->
			<doseQuantity>
				<xsl:attribute name="value"><xsl:value-of select="substring-before($medDetailNode/child::node()[position()=$medDetailNodeCnt]/route, '=')" /></xsl:attribute>
				<xsl:attribute name="unit"><xsl:value-of select="substring-after($medDetailNode/child::node()[position()=$medDetailNodeCnt]/route, '=')" /></xsl:attribute>
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
						<!-- <code code="307782" codeSystem="2.16.840.1.113883.6.88" displayName="Albuterol 0.09 MG/ACTUAT inhalant solution" codeSystemName="RxNorm"> -->
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

<xsl:template name="LabResultDetail">
		<xsl:param name="labtestDetailNode"/>
		<xsl:param name="labtestDetailNodeCnt" select="0"/>
		<xsl:param name="totalLabResultNodes" select="0" />
		
	<entry typeCode="DRIV">
	<!-- REPEAT SECTION FOR EACH BATTERY/EVN -->
		<organizer classCode="BATTERY" moodCode="EVN">
			<templateId root="2.16.840.1.113883.10.20.1.32"/>
			Result organizer template
			<!-- <id root="a40027e0-67a5-11db-bd13-0800200c9a66"/> -->
			<id>
				<xsl:attribute name="root">
	 				<xsl:value-of select="$ctx/context/accountIdRoot/@id" />
				</xsl:attribute>			
				<xsl:attribute name="extension">
					<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/path" />
				</xsl:attribute>
			</id>	
			<code code="20109005" codeSystem="2.16.840.1.113883.6.96" codeSystemName="SNOMED CT">
				<xsl:attribute name="displayName">	
					<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/name"/><!-- displayName="LYTES"  -->
				</xsl:attribute>
			</code>
			<statusCode code="completed"/>
			<effectiveTime value="200004061300"/>
			<component>
			<!-- REPEAT SECTION FOR EACH PROC/EVN -->
				<procedure classCode="PROC" moodCode="EVN">
					<templateId root="2.16.840.1.113883.3.88.11.83.17" assigningAuthorityName="HITSP C83"/>
					<templateId root="2.16.840.1.113883.10.20.1.29" assigningAuthorityName="CCD"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.19" assigningAuthorityName="IHE PCC"/>
					<id>
						<xsl:attribute name="root">
			 				<xsl:value-of select="$ctx/context/accountIdRoot/@id" />
						</xsl:attribute>			
						<xsl:attribute name="extension">
							<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/path" />
						</xsl:attribute>
					</id>
					<code code="20109005" codeSystem="2.16.840.1.113883.6.96" codeSystemName="SNOMED CT">
						<xsl:attribute name="displayName">	
							<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/name"/><!-- displayName="LYTES"  -->
						</xsl:attribute>
						<originalText><xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/name"/><!-- Extract blood for electrolytes tests-->
							<reference value="Ptr to text  in parent Section"/>
						</originalText>
					</code>
					<text>
						<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/name"/>
						<!-- Extract blood for electrolytes tests. IHE rules require description and reference to go here rather than in originalText of code. -->
						<reference value="Ptr to text  in parent Section"/>
					</text>
					<statusCode>
						<xsl:attribute name="code">	
							<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/actStatus"/><!-- completed-->
						</xsl:attribute>
					</statusCode>
					<effectiveTime>
						<xsl:attribute name="value">	
							<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/Date"/><!-- value="200004061300"-->
						</xsl:attribute>
					</effectiveTime>
				</procedure>
			</component>
			<component>
				<!-- REPEAT SECTION FOR EACH OBS/EVN -->
				<observation classCode="OBS" moodCode="EVN">
					<templateId root="2.16.840.1.113883.3.88.11.83.15.1" assigningAuthorityName="HITSP C83"/>
					<templateId root="2.16.840.1.113883.10.20.1.31" assigningAuthorityName="CCD"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.13" assigningAuthorityName="IHE PCC"/>
					Result observation template
					<id>
						<xsl:attribute name="root">
			 				<xsl:value-of select="$ctx/context/accountIdRoot/@id" />
						</xsl:attribute>			
						<xsl:attribute name="extension">
							<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/path" />
						</xsl:attribute>
					</id>
					<code codeSystem="2.16.840.1.113883.6.1">
						<xsl:attribute name="displayName">	
							<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/name"/><!-- displayName="NA"  -->
						</xsl:attribute>
						<xsl:attribute name="code">	
							<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/code"/><!-- 2951-2  -->
						</xsl:attribute>
					</code>
					<text>
						<reference value="PtrToValueInsectionText"/>
					</text>
					<statusCode>
						<xsl:attribute name="code">	
							<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/actStatus"/><!-- completed-->
						</xsl:attribute>
					</statusCode>
					<effectiveTime>
						<xsl:attribute name="value">	
							<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/Date"/><!-- value="200004061300"-->
						</xsl:attribute>
					</effectiveTime>
					<value>
						<xsl:attribute name="xsi:type">	
							<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/type"/><!-- PQ-->
						</xsl:attribute>
						<xsl:attribute name="value">	
							<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/value"/><!-- 140-->
						</xsl:attribute>
						<xsl:attribute name="unit">	
							<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/unit"/><!-- meq/l-->
						</xsl:attribute>
					</value>
					<interpretationCode codeSystem="2.16.840.1.113883.5.83">
						<xsl:attribute name="code">	
							<xsl:value-of select="$labtestDetailNode/child::node()[position()=$labtestDetailNodeCnt]/code"/><!-- code="N"  -->
						</xsl:attribute>					
					</interpretationCode>
				</observation>
			</component>
			<!-- <component>
				<observation classCode="OBS" moodCode="EVN">
					<templateId root="2.16.840.1.113883.3.88.11.83.15.1" assigningAuthorityName="HITSP C83"/>
					<templateId root="2.16.840.1.113883.10.20.1.31" assigningAuthorityName="CCD"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.13" assigningAuthorityName="IHE PCC"/>
					Result observation template
					<id root="a40027e2-67a5-11db-bd13-0800200c9a66"/>
					<code code="2823-3" codeSystem="2.16.840.1.113883.6.1" displayName="K"/>
					<text>
						<reference value="PtrToValueInsectionText"/>
					</text>
					<statusCode code="completed"/>
					<effectiveTime value="200004061300"/>
					<value xsi:type="PQ" value="4.0" unit="meq/l"/>
					<interpretationCode code="N" codeSystem="2.16.840.1.113883.5.83"/>
				</observation>
			</component>
			<component>
				<observation classCode="OBS" moodCode="EVN">
					<templateId root="2.16.840.1.113883.3.88.11.83.15.1" assigningAuthorityName="HITSP C83"/>
					<templateId root="2.16.840.1.113883.10.20.1.31" assigningAuthorityName="CCD"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.13" assigningAuthorityName="IHE PCC"/>
					Result observation template
					<id root="a40027e3-67a5-11db-bd13-0800200c9a66"/>
					<code code="2075-0" codeSystem="2.16.840.1.113883.6.1" displayName="CL"/>
					<text>
						<reference value="PtrToValueInsectionText"/>
					</text>
					<statusCode code="completed"/>
					<effectiveTime value="200004061300"/>
					<value xsi:type="PQ" value="102" unit="meq/l"/>
					<interpretationCode code="N" codeSystem="2.16.840.1.113883.5.83"/>
				</observation>
			</component>
			<component>
				<observation classCode="OBS" moodCode="EVN">
					<templateId root="2.16.840.1.113883.3.88.11.83.15.1" assigningAuthorityName="HITSP C83"/>
					<templateId root="2.16.840.1.113883.10.20.1.31" assigningAuthorityName="CCD"/>
					<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.13" assigningAuthorityName="IHE PCC"/>
					Result observation template
					<id root="a40027e4-67a5-11db-bd13-0800200c9a66"/>
					<code code="1963-8" codeSystem="2.16.840.1.113883.6.1" displayName="HCO3"/>
					<text>
						<reference value="PtrToValueInsectionText"/>
					</text>
					<statusCode code="completed"/>
					<effectiveTime value="200004061300"/>
					<value xsi:type="PQ" value="35" unit="meq/l"/>
					<interpretationCode code="H" codeSystem="2.16.840.1.113883.5.83"/>
				</observation>
			</component> -->
		</organizer>
	</entry>
			
	<xsl:if test="$totalLabResultNodes > $labtestDetailNodeCnt">
      <xsl:call-template name="LabResultDetail">
      	<xsl:with-param name="labtestDetailNode" select="$labtestDetailNode"/>
        <xsl:with-param name="totalLabResultNodes" select="$totalLabResultNodes" />
        <xsl:with-param name="labtestDetailNodeCnt" select="$labtestDetailNodeCnt + 1" />
      </xsl:call-template>
    </xsl:if>
</xsl:template>

								
</xsl:stylesheet>
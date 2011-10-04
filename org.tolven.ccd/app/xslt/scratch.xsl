<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:param name="context" />
	<xsl:variable name="ctx" select="document('context')" />
	<xsl:variable name="pat" select="document($context)" />
	<xsl:variable name="meds" select="document(concat($context,':medications:active'))" />
	<xsl:variable name="allergies"
		select="document(concat($context,':allergies:current'))" />
	<xsl:variable name="problems"
		select="document(concat($context,':problems:active'))" />
	<xsl:variable name="diagnoses"
		select="document(concat($context,':diagnoses:current'))" />
	<xsl:template match="/">
		
		<patient>
		<problems>
		<xsl:apply-templates select="$problems/results/rows" mode="ProblemDetail" />
		</problems>
		<medication>
		<xsl:apply-templates select="$meds/results/rows" mode="MedicationDetail" />
	
		</medication>
		<allergy>
		<xsl:apply-templates select="$allergies/results/rows" mode="AllergyDetail" />
		</allergy>
			<birthTime>
				<xsl:attribute name="value">
	 				<xsl:value-of select="concat(substring($pat/results/rows/row/dob, 1,4), substring($pat/results/rows/row/dob, 6,2), substring($pat/results/rows/row/dob, 9,2))" />
				</xsl:attribute>				
			</birthTime>
			<languageCode code="en-US">
						<xsl:value-of select="$pat/results/rows/row/language" />
					</languageCode>
			<administrativeGenderCode>
				<xsl:attribute name="displayName">
	 				<xsl:value-of select="$pat/results/rows/row/gender" />
				</xsl:attribute>
				<xsl:attribute name="code">
					<xsl:value-of select="substring($pat/results/rows/row/gender, 1, 1)" />
				</xsl:attribute>
			</administrativeGenderCode>
			
			<id assigningAuthorityName="Provider Name">
				<xsl:attribute name="root">
	 				<xsl:value-of select="$ctx/context/accountIdRoot/@id" />
				</xsl:attribute>			
				<xsl:attribute name="extension">
					<xsl:value-of select="$context" />
				</xsl:attribute>
			</id>
			
			<allergies>
				<fields>
					<xsl:value-of select="$allergies/results/fields"></xsl:value-of>
				</fields>
				<tbody>
					<xsl:apply-templates select="$allergies/results/rows" mode="Allergy" />

					<!-- <tr ID="ALGSUMMARY_1"> <td ID="ALGTYPE_1">Drug Allergy</td> <td 
						ID="ALGSUB_1">Penicillin</td> <td ID="ALGREACT_1">Hives</td> <td ID="ALGSTATUS_1">Active</td> 
						</tr> <tr ID="ALGSUMMARY_2"> <td ID="ALGTYPE_2">Drug Intolerance</td> <td 
						ID="ALGSUB_2">Aspirin</td> <td ID="ALGREACT_2">Wheezing</td> <td ID="ALGSTATUS_2">Active</td> 
						</tr> <tr ID="ALGSUMMARY_3"> <td ID="ALGTYPE_3">Drug Intolerance</td> <td 
						ID="ALGSUB_3">Codeine</td> <td ID="ALGREACT_3">Nausea</td> <td ID="ALGSTATUS_3">Active</td> 
						</tr> -->
				</tbody>
				<!--<one> <xsl:value-of select="$allergies/results/fields" /> </one> 
					<two> <xsl:value-of select="$allergies/results/rows/row/Allergy" /> </two> 
					<three> <xsl:value-of select="document(concat($pat,':allergies:current'))"/> 
					</three> -->
			</allergies>
			<problems>
				<tbody>
					<xsl:apply-templates select="$problems/results/rows"
						mode="Problem" />
					<xsl:value-of select="$problems/results/fields" />
				</tbody>
			</problems>
			<diagnoses>
				<tbody>
					<xsl:apply-templates select="$diagnoses/results/rows"
						mode="Diagnosis" />
					<xsl:value-of select="$diagnoses/results/fields" />
				</tbody>
			</diagnoses>
			<addr>
				<streetAddressLine>
					<xsl:value-of select="$pat/results/rows/row/homeAddr1" />
				</streetAddressLine>
				<streetAddressLine>
					<xsl:value-of select="$pat/results/rows/row/homeAddr2" />
				</streetAddressLine>
				<city>
					<xsl:value-of select="$pat/results/rows/row/homeCity" />
				</city>
				<state>
					<xsl:value-of select="$pat/results/rows/row/homeState" />
				</state>
				<postalCode>
					<xsl:value-of select="$pat/results/rows/row/homeZip" />
				</postalCode>
				<country>
					<xsl:value-of select="$pat/results/rows/row/homeCountry" />
				</country>
			</addr>



		</patient>
	</xsl:template>
	<xsl:template match="/results/rows/row" mode="Allergy">
		<tr ID="ALGSUMMARY_1">
			<td ID="ALGTYPE_1">
				<xsl:value-of select="./Code" />
				...Drug Allergy
			</td>
			<td ID="ALGSUB_1">
				<xsl:value-of select="./Allergy" />
			</td>
			<!-- <xsl:variable name="drilldownAllergy" select="document(./path)"/> 
				<xsl:value-of select="./drilldown"/> <xsl:value-of select="./path"/> -->
			<td ID="ALGREACT_1">
				<xsl:value-of select="./documentId" />
				...Hives
			</td>
			<td ID="ALGSTATUS_1">
				<xsl:value-of select="./Status" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="/results/rows/row" mode="Problem">
		<tr ID="PROBSUMMARY_1">
			<td ID="PROBKIND_1">
				<xsl:value-of select="./Problem" />
			</td>
			<td>
				<xsl:value-of select="./Code" />
			</td>
			<td ID="PROBSTATUS_1">
				<xsl:value-of select="./Status" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="/results/rows/row" mode="Diagnosis">
		<thead>
			<tr>
				<th>
					<xsl:value-of select="./Date" />
				</th>
			</tr>
		</thead>
		<tr>
			<td colspan="3">
				<content styleCode="BoldItalics">
					<xsl:value-of select="./Diagnosis" />
				</content>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:value-of select="./Code" />
			</td>
		</tr>
		<tr>
			<td>
				<xsl:value-of select="./Status" />
			</td>
		</tr>

	</xsl:template>
	
	<xsl:template match="/results/rows/row" mode="AllergyDetail">
	<entry typeCode="DRIV">
		<act classCode="ACT" moodCode="EVN">
			<templateId root="2.16.840.1.113883.3.88.11.83.6" assigningAuthorityName="HITSP C83"/>
			<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.3" assigningAuthorityName="IHE PCC"/>
			<!--Allergy act template -->
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
					<!--Allergy observation template. NOTE that the HITSP/C83 requirement for code (i.e. allergy type) differs from the IHE PCC recommendation for code.-->
					<!-- <id root="4adc1020-7b14-11db-9fe1-0800200c9a66" extension=""/> -->
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
						<xsl:value-of select="./effectiveTime" />
						<low nullFlavor="UNK"/>
					</effectiveTime>
					<!--Note that IHE/PCC and HITSP/C32 differ in how to represent the drug, substance, or food that one is allergic to. IHE/PCC expects to see that information in <value> and HITSP/C32 expects to see it in <participant>.-->
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
</xsl:template>

<xsl:template match="/results/rows/row" mode="ProblemDetail">
	<entry typeCode="DRIV">
		<act classCode="ACT" moodCode="EVN">
			<templateId root="2.16.840.1.113883.3.88.11.83.7" assigningAuthorityName="HITSP C83"/>
			<templateId root="2.16.840.1.113883.10.20.1.27" assigningAuthorityName="CCD"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.1" assigningAuthorityName="IHE PCC"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.5.2" assigningAuthorityName="IHE PCC"/>
			<!-- Problem act template -->
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
					<!--Problem observation template - NOT episode template-->
					<!-- <id root="d11275e7-67ae-11db-bd13-0800200c9a66"/> -->
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
					</effectiveTime>
					<!-- <value xsi:type="CD" displayName="Asthma" code="195967001" codeSystemName="SNOMED" codeSystem="2.16.840.1.113883.6.96"/> -->
					<value codeSystemName="SNOMED" codeSystem="2.16.840.1.113883.6.96">
						<xsl:attribute name="xsi:type">CD</xsl:attribute>
						<xsl:attribute name="displayName"><xsl:value-of select="./Problem" /></xsl:attribute>
						<xsl:attribute name="code"><xsl:value-of select="./Code" /></xsl:attribute>
					</value>
				</observation>
			</entryRelationship>
		</act>
	</entry>
</xsl:template>
					
<xsl:template match="/results/rows/row" mode="MedicationDetail">
	<entry typeCode="DRIV">
		<substanceAdministration classCode="SBADM" moodCode="EVN">
			<templateId root="2.16.840.1.113883.3.88.11.83.8" assigningAuthorityName="HITSP C83"/>
			<templateId root="2.16.840.1.113883.10.20.1.24" assigningAuthorityName="CCD"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7" assigningAuthorityName="IHE PCC"/>
			<templateId root="1.3.6.1.4.1.19376.1.5.3.1.4.7.1" assigningAuthorityName="IHE PCC"/>
			<!--Medication activity template -->
			<id root="cdbd33f0-6cde-11db-9fe1-0800200c9a66"/>
			<text>
				<reference value="#SIGTEXT_1"/>
			</text>
			<statusCode>
				<xsl:attribute name="code"><xsl:value-of select="./actStatus" /></xsl:attribute>
			</statusCode>
			<effectiveTime >
				<xsl:attribute name="xsi:type">IVL_TS</xsl:attribute>
				<low value="200507"/>
				<high nullFlavor="UNK"/>
			</effectiveTime>
			<effectiveTime>
				<xsl:attribute name="xsi:type">PIVL_TS</xsl:attribute>
				<!-- <period value="6" unit="h"/> -->
				<period>
					<xsl:attribute name="value"><xsl:value-of select="substring(./frequency,1,1)" /></xsl:attribute>
					<xsl:attribute name="unit"><xsl:value-of select="substring(./frequency,2,1)" /></xsl:attribute>
				</period>
			</effectiveTime>
			<!--The following route, dose and administrationUnit elements are HITSP/C83 Sig Components that are optional elements in a HITSP/C83 document. The dose and administrationUnit information is often inferable from the code (e.g. RxNorm code) of the consumable/manufacturedProduct. The route is often inferable from the administrativeUnit (e.g tablet implies oral route). -->
			<routeCode>
				<originalText><xsl:value-of select="./route" /></originalText>
			</routeCode>
			<!-- <doseQuantity value="2" unit="puffs"/> -->
			<doseQuantity>
				<xsl:attribute name="value"><xsl:value-of select="substring-before(./route, '=')" /></xsl:attribute>
				<xsl:attribute name="unit"><xsl:value-of select="substring-after(./route, '=')" /></xsl:attribute>
			</doseQuantity>
			<administrationUnitCode>
				<originalText>inhaler</originalText>
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
							<xsl:attribute name="code"><xsl:value-of select="./code" /></xsl:attribute>
							<xsl:attribute name="displayName"><xsl:value-of select="./name" /></xsl:attribute>
							<originalText><xsl:value-of select="./name" /><reference/>
							</originalText>
						</code>
					</manufacturedMaterial>
				</manufacturedProduct>
			</consumable>
		</substanceAdministration>
	</entry>	
</xsl:template>					

	<!-- <xsl:template name="allergy"> <tr ID="ALGSUMMARY_1"></tr> <tr ID="ALGSUMMARY_1"> 
		<td ID="ALGTYPE_1">Drug Allergy</td> <td ID="ALGSUB_1"> <xsl:value-of select="/row/Allergy" 
		/> </td> <td ID="ALGREACT_1">Hives</td> <td ID="ALGSTATUS_1">Active</td> 
		</tr> </xsl:template> -->
</xsl:stylesheet>
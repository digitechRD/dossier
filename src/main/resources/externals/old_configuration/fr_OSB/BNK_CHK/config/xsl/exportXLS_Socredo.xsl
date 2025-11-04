<?xml version="1.0" encoding="ISO-8859-15"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="urn:schemas-microsoft-com:office:spreadsheet"
    xmlns:o="urn:schemas-microsoft-com:office:office"
    xmlns:x="urn:schemas-microsoft-com:office:excel"
    xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:ns1="http://www.digitech.com/airsIII">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:key name="code" match="ns1:CODE" use="."/>
    <xsl:template match="/">
        <xsl:text disable-output-escaping="yes">&lt;?mso-application progid=&quot;Excel.Sheet&quot;?&gt;&#10;</xsl:text>
        <Workbook>
            <Worksheet ss:Name="Feuil1">
                <Table>
                    <Row>
                        <xsl:for-each select="//ns1:CODE[generate-id() = generate-id(key('code',.)[1])]">
                            <xsl:variable name="NomColonne">
                          	  <xsl:value-of select="."/>
                          	</xsl:variable>
                            <xsl:choose>
                              <xsl:when test="$NomColonne = 'CHQ_ETAT_SIGN'"> 
                                 <Cell>
                                    <Data ss:Type="String">
                                        <xsl:text>Controle de signature</xsl:text>
                                    </Data>
                                 </Cell>
                              </xsl:when>
                              <xsl:when test="$NomColonne = 'CHQ_ETAT_REGL'"> 
                                 <Cell>
                                    <Data ss:Type="String">
                                        <xsl:text>Controle reglementaire</xsl:text>
                                    </Data>
                                 </Cell>
                              </xsl:when>
                              <xsl:when test="$NomColonne = 'COMMENTAIRE'"> 
                                 <Cell>
                                    <Data ss:Type="String">
                                        <xsl:text>Commentaire</xsl:text>
                                    </Data>
                                 </Cell>
                              </xsl:when>
                              <xsl:when test="$NomColonne = 'MON_CHQ'"> 
                                 <Cell>
                                    <Data ss:Type="String">
                                        <xsl:text>Montant du cheque</xsl:text>
                                    </Data>
                                 </Cell>
                              </xsl:when>
                              <xsl:when test="$NomColonne = 'CPT_CHQ'"> 
                                 <Cell>
                                    <Data ss:Type="String">
                                        <xsl:text>Compte tiree</xsl:text>
                                    </Data>
                                 </Cell>
                              </xsl:when>
                              <xsl:when test="$NomColonne = 'NUM_CHQ'"> 
                                 <Cell>
                                    <Data ss:Type="String">
                                        <xsl:text>Numero de cheque</xsl:text>
                                    </Data>
                                 </Cell>
                              </xsl:when>
                              <xsl:when test="$NomColonne = 'BNK_CHQ'"> 
                                 <Cell>
                                    <Data ss:Type="String">
                                        <xsl:text>Banque tiree</xsl:text>
                                    </Data>
                                 </Cell>
                              </xsl:when>
                              <xsl:when test="$NomColonne = 'NUM_MRQ'"> 
                                 <Cell>
                                    <Data ss:Type="String">
                                        <xsl:text>Indice MRQ</xsl:text>
                                    </Data>
                                 </Cell>
                              </xsl:when>
                              <xsl:when test="$NomColonne = 'CPT_REM'"> 
                                 <Cell>
                                    <Data ss:Type="String">
                                        <xsl:text>Compte remettant</xsl:text>
                                    </Data>
                                 </Cell>
                              </xsl:when>
							               <xsl:when test="$NomColonne = 'BNK_REM'"> 
                                 <Cell>
                                    <Data ss:Type="String">
                                        <xsl:text>Banque remettante</xsl:text>
                                    </Data>
                                 </Cell>
                              </xsl:when>
							               <xsl:when test="$NomColonne = 'DAT_TMT'"> 
                                 <Cell>
                                    <Data ss:Type="String">
                                        <xsl:text>Date de traitement</xsl:text>
                                    </Data>
                                 </Cell>
                              </xsl:when>
                              <xsl:otherwise></xsl:otherwise>
                            </xsl:choose>   
                        </xsl:for-each>
                    </Row>
                    <xsl:apply-templates select="./ns1:FICHE_ROOT-LIST/ns1:FICHE_ROOT/ns1:FICHE"/>    
                </Table>
            </Worksheet>
        </Workbook>
    </xsl:template>

    <xsl:template match="ns1:FICHE">
        <Row>
          <xsl:for-each select="ns1:CHAMP">
              <xsl:variable name="ValeurColonne">
          	     <xsl:value-of select="ns1:CODE"/>
          	  </xsl:variable>
              <xsl:choose>
        			  <xsl:when test="$ValeurColonne='CHQ_ETAT_SIGN' or $ValeurColonne='CHQ_ETAT_REGL' or $ValeurColonne='COMMENTAIRE' or $ValeurColonne='CPT_CHQ' or $ValeurColonne='NUM_CHQ' or $ValeurColonne='BNK_CHQ' or $ValeurColonne='NUM_MRQ' or $ValeurColonne='CPT_REM' or $ValeurColonne='BNK_REM' or $ValeurColonne='DAT_TMT'">
        				  <Cell>
        					  <Data ss:Type="String">
        						  <xsl:value-of select="ns1:VALEUR"/>
        					  </Data>
        				  </Cell>
        			  </xsl:when>
        			  <xsl:when test="$ValeurColonne='MON_CHQ'">
        			     <Cell>
        					  <Data ss:Type="Number">
        						  <xsl:value-of select="ns1:VALEUR"/>
        					  </Data>
        				  </Cell>
        			  </xsl:when>
        			  <xsl:otherwise></xsl:otherwise>
        		 </xsl:choose>
          </xsl:for-each>
        </Row>
    </xsl:template>
</xsl:stylesheet>

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
                        <xsl:for-each
                            select="//ns1:CODE[generate-id() = generate-id(key('code',.)[1])]">
                            <Cell>
                                <Data ss:Type="String">
                                    <xsl:value-of select="."/>
                                </Data>
                            </Cell>
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
        			  <xsl:when test="$ValeurColonne='MON_CHQ' or $ValeurColonne='MON_REM'">
        			     <Cell>
        					  <Data ss:Type="Number">
        						  <xsl:value-of select="ns1:VALEUR"/>
        					  </Data>
        				  </Cell>
        			  </xsl:when>
        			  <xsl:otherwise>
        			    <Cell>
                   <Data ss:Type="String">
        						  <xsl:value-of select="ns1:VALEUR"/>
        					  </Data>
        					</Cell>
                </xsl:otherwise>
        		 </xsl:choose>
          </xsl:for-each>
        </Row>
    </xsl:template>
</xsl:stylesheet>

<?xml version="1.0" encoding="ISO-8859-15"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="urn:schemas-microsoft-com:office:spreadsheet" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet" xmlns:html="http://www.w3.org/TR/REC-html40">
  <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
 <xsl:key name="code" match="CODE" use="."/>
  <xsl:template match="/">
    <xsl:text disable-output-escaping="yes">&lt;?mso-application progid=&quot;Excel.Sheet&quot;?&gt;&#10;</xsl:text>
    <Workbook>
      <Worksheet ss:Name="Feuil1" >
        <Table>
         <Row>
       <Cell><Data ss:Type="String">DOC_ID</Data></Cell>
            <xsl:for-each select="//CODE[generate-id() = generate-id(key('code',.)[1])]">
               <Cell><Data ss:Type="String">
                 <xsl:value-of select="/." />
                </Data></Cell>  
            </xsl:for-each>
            </Row>
          <!-- <xsl:apply-templates select="./FICHE_ROOT-LIST"/>-->
          <xsl:apply-templates select="./FICHE_ROOT-LIST/FICHE_ROOT/FICHE"/>          
        </Table>
      </Worksheet>
    </Workbook>
  </xsl:template>

<xsl:template match="FICHE_ROOT-LIST/FICHE_ROOT/FICHE">
<Row>
 <Cell><Data ss:Type="String">
          <xsl:value-of select="@ID" />
          </Data></Cell>
<xsl:for-each select="CHAMP">
 <Cell><Data ss:Type="String">
          <xsl:value-of select="VALEUR" />
          </Data></Cell>
</xsl:for-each> 
</Row>
</xsl:template>
</xsl:stylesheet>

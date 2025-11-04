<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ns1="http://www.digitech.com/airsIII">
    <xsl:output method="text" encoding="UTF-8" />
    <xsl:key name="airsFields" match="ns1:CODE" use="."/>
    <xsl:variable name="distinct-fields" select="//ns1:CODE[ generate-id() = generate-id( key('airsFields', .)[1])]"/>
    <xsl:template match="ns1:FICHE_ROOT-LIST">
        <xsl:text>|Id</xsl:text>
        <xsl:for-each select="$distinct-fields"><xsl:text>|,|</xsl:text><xsl:value-of select="."/>
        </xsl:for-each>
        <xsl:text>|</xsl:text>
        <xsl:for-each select="ns1:FICHE_ROOT">            
            <xsl:call-template name="traitFiche">
                <xsl:with-param name="fileIn" select="./ns1:FICHE"/>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="traitFiche">        
        <xsl:param name="fileIn"/>         
        <xsl:text>&#10;</xsl:text>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="$fileIn/@ns1:ID" />
        <xsl:for-each select="$fileIn/ns1:CHAMP/ns1:CODE"><xsl:text>|,|</xsl:text><xsl:value-of select="./../ns1:VALEUR" />
        </xsl:for-each>
        <xsl:text>|</xsl:text>
    </xsl:template>
</xsl:stylesheet>
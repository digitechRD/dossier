<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" />
	<xsl:template match="FICHE_ROOT-LIST">
		<xsl:text>Id</xsl:text><xsl:for-each select="FICHE_ROOT/FICHE/CHAMP/CODE">,<xsl:value-of select="." /></xsl:for-each>		
		<xsl:for-each select="FICHE_ROOT">
			<xsl:call-template name="traitFiche">
				<xsl:with-param name="fileIn" select="./FICHE"/>
				</xsl:call-template>
			</xsl:for-each>
	</xsl:template>

	<xsl:template name="traitFiche">
	<xsl:param name="fileIn"/> 
		<xsl:text>&#10;</xsl:text>
		<xsl:value-of select="$fileIn/@ID" /><xsl:for-each select="$fileIn/CHAMP/CODE">,<xsl:value-of select="./../VALEUR" /></xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
# dossier docs

Ce projet regroupe les documentations de _Dossier_.

Les documentations disponibles sont:
* [installation sso](src/adoc/administration/installation-sso.adoc) 
* <span style="color: #FF0000;">... à suivre</span> 



# Génération des documentations

Si besoin, les documentations peuvent être regénérées.
La commande [maven](https://maven.apache.org/) à utiliser est la suivante

mvn generate-resources -P<profiles.docs>

La clé **profiles.docs** peut contenir plusieurs profils _maven_  (séparés par une **,**) parmi les valeurs suivantes
* **docs-sso**
* ...

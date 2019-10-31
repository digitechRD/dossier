[//]: <> (qsdqsd) 
# _documentations_ **Dossier** 

Ce projet regroupe les documentations du produit _Dossier_.

Les documentations disponibles sont:
* [installation sso](src/adoc/administration/installation-sso.adoc) (version *PDF* [ici](src/main/resources/docs/administration/installation-sso.pdf)) 
* <span style="color: #FF0000;">... à suivre</span> 

# Génération des documentations

Si besoin, les documentations peuvent être regénérées.

Après avoir téléchargé les sources du projet,
la commande [maven](https://maven.apache.org/) suivante doit être exécutée:

```console
mvn generate-resources -P<profiles.docs>
```

La clé **profiles.docs** peut contenir plusieurs profils _maven_  (séparés par une **,**) parmi les valeurs suivantes
* **docs-sso**
* ...

Vous trouverez alors les documentations générées (au format _pdf_ et _html_) dans le répertoire **src/main/resources/docs**.

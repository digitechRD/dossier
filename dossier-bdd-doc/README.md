
[//]: #  (github url : https://github.com/digitechRD/dossier)

# _documentations_ **Dossier** 

Ce projet regroupe les documentations du produit _Dossier_.

Les documentations disponibles sont:
* [installation sso](src/adoc/sso/dossier-sso.adoc) (version *PDF* [ici](src/main/resources/docs/sso/dossier-sso.pdf)) 
* [cmis](src/adoc/cmis/dossier-cmis.adoc) (version *PDF* [ici](src/main/resources/docs/cmis/dossier-cmis.pdf)) 
* <span style="color: #FF0000;">... à suivre</span> 

# Génération des documentations

Si besoin, les documentations peuvent être regénérées.

Après avoir téléchargé les sources du projet,
la commande [maven](https://maven.apache.org/) suivante doit être exécutée :

```console
mvn generate-resources -P<profiles.docs>
```

La clé **profiles.docs** peut contenir plusieurs profils _maven_ (séparés par une **,**) parmi les valeurs suivantes
* **docs-sso**
* **docs-cmis**
* ...

Vous trouverez alors les documentations générées (au format _pdf_ et _html_) dans le répertoire **src/main/resources/docs**.

# Push multi repositories

Paramétrage du commit sur 2 _repositories_ en parallèle/synchro

1. Récupérer les sources du repo **interne**

`git@10.1.4.105:dossier/dossier-docs.git`

2. Ajout repo **github** à la liste des repos* _**remote**_ 
   
`git remote add github https://github.com/digitechRD/dossier.git`

3. Vérification des _remotes_

`git remote -v` >> doit afficher les 2 urls

4. Configuration multi-push automatique (reconnue par *git* et *Intellij*)

```
git remote set-url --add --push origin git@10.1.4.105:dossier/dossier-docs.git
git remote set-url --add --push origin https://github.com/digitechRD/dossier.git
```
5. Vérification

```
git remote show origin

>> Push  URL: git@10.1.4.105:dossier/dossier-docs.git
>> Push  URL: https://github.com/digitechRD/dossier.git
```

6. Push

Le **push** direct doit alors fonctionner
* soit en utilisant `git` >> `git push`
* soit `intellij` >>  <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>K</kbd>
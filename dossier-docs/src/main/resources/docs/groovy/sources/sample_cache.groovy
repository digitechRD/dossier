import com.digitech.dossier.script.utils.ScriptUtilities

// ajout au cache
ScriptUtilities.addToCache("<key>", obj)

// récupération du cache cache
def value = ScriptUtilities.getFromCache("<key>")

// suppression du cache
ScriptUtilities.removeFromCache("<key>")
import com.digitech.dossier.script.utils.ScriptUtilities

/**
 * (optional) method called when script is loaded (or reloaded)
 * */
void setup() {
  // initialisation du pool de connexion (par exemple)
  def poolJdbc = dd//

  // et mise en cache (voir chapitre suivant)
  ScriptUtilities.addToCache("poolScript", poolJdbc)
}
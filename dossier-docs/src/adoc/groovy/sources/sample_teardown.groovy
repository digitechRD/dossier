import com.digitech.dossier.script.utils.ScriptUtilities

/**
 * (optional) method called when script is unloaded (or before being reloaded)
 * */
void tearDown() {
  // libération du pool de connexion (par exemple)
  try {
    def poolJdbc = ScriptUtilities.getFromCache("poolScript")
    poolJdbc?.release()
  }
  finally{
    ScriptUtilities.removeFromCache("poolScript")
  }
}
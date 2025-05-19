import  com.digitech.dossier.common.model.backend.airs.LocutionModel
import  com.digitech.dossier.common.model.backend.airs.impl.IDocument
import  com.digitech.dossier.common.model.backend.airs.ILocutionModel.Operator
import com.digitech.dossier.common.service.ServiceUtils
import com.digitech.dossier.common.utils.DocumentUtils

// IDocument _document

//  mise à jour d'un index
_document.setFieldValue("FIELD_CODE_1", "nouvelle valeur 1")
_document.setFieldValue("FIELD_CODE_2", "nouvelle valeur 2")
// sauvegarde du document
_document.save()

// récupération d'un index
def summary = _document.getFieldValue("CR_RESUME")
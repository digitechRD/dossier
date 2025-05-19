import  com.digitech.dossier.common.model.backend.airs.LocutionModel
import  com.digitech.dossier.common.model.backend.airs.impl.IDocument
import  com.digitech.dossier.common.model.backend.airs.ILocutionModel.Operator
import com.digitech.dossier.common.service.ServiceUtils
import com.digitech.dossier.common.utils.DocumentUtils


// construction des locutions
def locutionModel = new LocutionModel()
DocumentUtils.buildLocutionModelWithDate(locutionModel, "D_ECHEANCE", Operator.OPERATOR_VALUE_EQUAL, new Date())
DocumentUtils.buildLocutionModel(locutionModel, "FIELD_CODE_D_CLOTURE", Operator.OPERATOR_ISNULL, null)

// lancement de la recherche et chargement direct des documents
List<IDocument> docs = DocumentUtils.search(_userContext, locutionModel, "COU_COURRIER_OUT")

// ou lancement simple pour récupération des docIDs
def searchModel = ServiceUtils.getSearchService().getSearchModel(_userContext, locutionModel, "COU_COURRIER_OUT")
List<Integer> docIds =  ServiceUtils.getSearchService().getSearch(_userContext, searchModel, false)

for(Integer dId : docIds) {
  IDocument loadedDocument = ServiceUtils.getDocumentService().getDocument(_userContext, dId)
}
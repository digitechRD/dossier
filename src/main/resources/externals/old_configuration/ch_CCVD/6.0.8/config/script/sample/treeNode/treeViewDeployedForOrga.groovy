import java.util.List;

import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import java.util.*

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IHierarchicalTreeNode
import com.digitech.dossier.script.model.impl.result.ScriptResultModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueHierarchicalTreeNodeInitializer
import com.digitech.dossier.script.model.impl.result.ScriptResultValueHierarchicalTreeNodeInitializer.TreeNodeProperty;

/********************* Informations ********************/
/* autor : R.Krawezyk
 * date : 09/07/2012
 * target version : 5.3.14
 * features : open node when the current user organization id equals one contains in the orgaList.
 * limit : make the tree view display slower
 */
/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document
// orgaList define the organization list for with the tree would be totaly deployed
def orgaList = [5, 6, 7, 8, 20]

/************************************************/

UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
IHierarchicalTreeNode theTreeNode = treeNode;

ScriptResultModel<ScriptResultValueHierarchicalTreeNodeInitializer> theOutput = output;

logger.debug("Script field initialization: treeViewDeployedForOrga.groovy --- Start");

logger.debug("Testing organization id in order to deploy the tree view");


List<Integer>  IntegerListToTest= new ArrayList<Integer>();
IntegerListToTest.addAll(orgaList);

if( IntegerListToTest.contains(usrContext.getCurrentOrgId()) )
{
  logger.debug("The node would be expended");
  theOutput.getValue().getProperties().put(TreeNodeProperty.EXPANDED, "true");
}

logger.debug("Script field initialization: treeViewDeployedForOrga.groovy --- End");


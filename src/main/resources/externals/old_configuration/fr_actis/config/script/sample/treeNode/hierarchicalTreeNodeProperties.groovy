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

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document
/************************************************/

UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
IHierarchicalTreeNode theTreeNode = treeNode;

ScriptResultModel<ScriptResultValueHierarchicalTreeNodeInitializer> theOutput = output;

logger.debug("Script field initialization: treeNodeProperties.groovy --- Start");


if (!com.digitech.dossier.common.model.backend.Constants.NODE_TYPE_FILE.equals(theTreeNode.getType())) {
  // Opens a tree node based on the authority list value 714
  if (isTreeNodeWithValue(theTreeNode, 714)) {
    theOutput.getValue().getProperties().put(TreeNodeProperty.EXPANDED, "true");
  }

  // Opens all the children of 714
  IHierarchicalTreeNode parentTreeNode = theTreeNode.getParent(false);
  if (parentTreeNode != null && isTreeNodeWithValue(parentTreeNode, 714)) {
    theOutput.getValue().getProperties().put(TreeNodeProperty.EXPANDED, "true");
  }
}

private boolean isTreeNodeWithValue(IHierarchicalTreeNode treeNode, Integer value) {
  List<?> values = treeNode.getValue();
  if (values != null && !values.isEmpty()) {
    Object val = values.get(0)
    if (val instanceof Integer) {
      Integer valInt = (Integer)val;
      return 714 == valInt.intValue();
    }
  }
}


logger.debug("Script field initialization: treeNodeProperties.groovy --- End");


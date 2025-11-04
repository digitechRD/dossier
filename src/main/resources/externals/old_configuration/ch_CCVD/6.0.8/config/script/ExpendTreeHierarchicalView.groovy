import java.util.List;

import java.util.*

import com.digitech.dossier.common.model.backend.airs.IHierarchicalTreeNode
import com.digitech.dossier.script.model.impl.result.ScriptResultValueHierarchicalTreeNodeInitializer.TreeNodeProperty;

import Constants;

/*************************************************************************************************
 *   					    Distribution des documents - INIT
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Ouvre le noeud lorsque celui-ci est égal à la valeur 714
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EXPEND TREE HIERARCHICAL VIEW - START");

/**
 * TRAITEMENT
 **************************************************************************************************/

// On recupere le service pour l'organisation courante
int servicevalue = Constants.MAP_ORGANIZATION_SERVICE.get(userContext.getCurrentOrgId());

if (!com.digitech.dossier.common.model.backend.Constants.NODE_TYPE_FILE.equals(treeNode.getType())) {
    // Opens a tree node based on the authority list value servicevalue
    if (isTreeNodeWithValue(treeNode, servicevalue)) {
        output.getValue().getProperties().put(TreeNodeProperty.EXPANDED, "true");
    }

    // Opens all the children of servicevalue
    IHierarchicalTreeNode parentTreeNode = treeNode.getParent(false);
    if (parentTreeNode != null && isTreeNodeWithValue(parentTreeNode, servicevalue)) {
        output.getValue().getProperties().put(TreeNodeProperty.EXPANDED, "true");
    }
}

scriptLogger.debug("[CUSTOM ACTION] - EXPEND TREE HIERARCHICAL VIEW - END");

/**
 * METHODES
 **************************************************************************************************/

private boolean isTreeNodeWithValue(IHierarchicalTreeNode treeNode, Integer value) {
    List<?> values = treeNode.getValue();
    if (values != null && !values.isEmpty()) {
        Object val = values.get(0)
        if (val instanceof Integer) {
            Integer valInt = (Integer)val;
            return value == valInt.intValue();
        }else return false;
    }else return false;
}


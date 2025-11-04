import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.mail.Session;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.jsf.FacesContextUtils;

import com.digitech.dossier.admin.Utils;
import com.digitech.dossier.common.Constants;
import com.digitech.dossier.common.exception.ServiceException;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backing.factory.SelectItemFactory;
import com.digitech.dossier.common.model.backing.factory.SelectItemFactory.CacheKey;
import com.digitech.dossier.common.service.CacheManager;
import com.digitech.dossier.common.service.impl.PopulationMgr;
import com.digitech.dossier.common.utils.ApplicationUtils;
import com.digitech.dossier.common.utils.UserUtils;

private  Logger LOGGER = LoggerFactory.getLogger(SelectItemFactory.class);

/**
 * Script permettant la mise a jour du module Population
 */

Collection<Locale> localeList = ApplicationUtils.getSupportedLocales(null);

//reload le cache si la propriété needReloadPopCache est a true.
if(PopulationMgr.isNeedReloadPopCache()){
  removePopSelectItems(CacheKey.POP_PERSON,localeList);
  removePopSelectItems(CacheKey.POP_COMPANY,localeList);
  
    try {
    new SelectItemFactory().loadPopPersonCache(localeList);
    new SelectItemFactory().loadPopCompaniesCache(localeList);
  }
  catch(ServiceException se) {
    LOGGER.error("Error while getting Pop Person: " + se.getLocalizedMessage(), se);
  }
  
  removePopSelectItems(CacheKey.POP_PERSON_COMPANY_RELATION,localeList);
  new SelectItemFactory().LoadPopPersonCompanyRelationsCache(localeList);
  
    PopulationMgr.setNeedReloadPopCache(false);
}


private void removePopSelectItems(CacheKey cacheKey, Collection<Locale> localeList) {
  
  for(Locale currentLocale : localeList) {
    String key = cacheKey.name() + "_" + currentLocale.toString();
    Map<String, List<SelectItem>> selectItemsMap = ((Map<String, List<SelectItem>>) CacheManager.getInstance().get(CacheManager.CACHE_SELECTITEM_MAP));
    if(selectItemsMap != null) {
      selectItemsMap.remove(key);
    }
  }
  
  
}

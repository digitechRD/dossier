import java.time.temporal.ChronoUnit
import java.time.ZoneId
import java.util.Calendar
import java.text.DateFormat;  
import java.text.SimpleDateFormat;  

import com.digitech.dossier.common.utils.DateUtils

import com.digitech.courrier.common.model.backend.CourrierConstants.FlowType
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backend.params.CourrierType
import com.digitech.dossier.common.model.backing.AbstractFormLocutionModel
import com.digitech.jcorbairs.Term
import com.digitech.jcorbairs.User
import com.digitech.dossier.common.utils.FieldUtils
import org.slf4j.Logger
import javax.faces.component.UIComponent
import java.util.*
import com.digitech.dossier.common.service.ServiceUtils
import static DocCong_Utils;

/*******************************************************************
 * Calcule MSC_DATE_FIN à partir de MSC_DATE_DEBUT et MSC_DUREE_SEJOUR_CONF
 * Version : 1.0
 * Date : 28/07/2025
 *******************************************************************/

UserContext usrContext = userContext

Logger log = scriptLogger
final String SCRIPT_NAME = "MSC_dependency_MSC_DATE_FIN.groovy"

log.debug("{}: --- Start", SCRIPT_NAME)

try {
    def dtDebut = _fieldMap.get("MSC_DATE_DEBUT")?.value
	log.debug("{}: Date de début = {}", SCRIPT_NAME, dtDebut)
    def nbJours = _fieldMap.get("MSC_DUREE_SEJOUR_CONF")?.value
	log.debug("{}: Nombre de jours = {}", SCRIPT_NAME, nbJours)

    if (dtDebut != null && nbJours != null && nbJours instanceof Number && nbJours > 0) {
        Calendar cal = Calendar.getInstance()
        cal.setTime(dtDebut)

        // On ajoute (durée - 1) pour que le calcul soit inclusif
        cal.add(Calendar.DATE, nbJours - 1)

        def dtFin = cal.getTime()
        log.debug("{}: Date calculée = {}", SCRIPT_NAME, dtFin)

        FieldUtils.setValue(_fieldMap.get("MSC_DATE_FIN"), dtFin, true)
    } else {
        log.debug("{}: Champs manquants ou invalides, calcul ignoré", SCRIPT_NAME)
    }
} catch (Exception e) {
    log.error("{}: EXCEPTION {} {}", SCRIPT_NAME, e.getLocalizedMessage(), e)
}

log.debug("{}: --- End", SCRIPT_NAME)

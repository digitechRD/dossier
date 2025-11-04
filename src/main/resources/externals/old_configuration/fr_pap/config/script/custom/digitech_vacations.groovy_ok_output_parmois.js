package homeblock.custom.shipyard.timeline

import com.digitech.common.script.model.EnumScriptStatus
import com.digitech.common.utils.DateUtils
import com.digitech.dossier.common.model.backend.Constants
import com.digitech.dossier.common.model.backend.airs.ISearchModel
import com.digitech.dossier.common.model.backend.airs.impl.Search
import com.digitech.dossier.common.service.ServiceUtils
import com.digitech.dossier.common.utils.time.PeriodBuilderHelper
import com.digitech.dossier.script.model.IScriptResultValueModel.Severity
import groovy.json.JsonOutput
import groovy.transform.ToString
import org.apache.commons.collections4.CollectionUtils
import org.joda.time.DateTime
import org.joda.time.Days
import javax.faces.model.SelectItem
import static com.digitech.dossier.common.model.backend.ConstantsConfig.*

_logger.debug(">>> navires.timeline - mois")

/** Constantes spécifiques MONTEE_SUR_CALE */
class ShipConstants {
    final static String CT_CODE = "MONTEE_SUR_CALE"
    final static String CODE_NAVIRE     = "MSC_NOM_NAVIRE"
    final static String CODE_DATE_DEBUT = "MSC_DATE_DEBUT"
    final static String CODE_DATE_FIN   = "MSC_DATE_FIN"

    // Options mois
    final static String THIS_MONTH  = "M0"
    final static String LAST_MONTH  = "M-1"
    final static String NEXT_MONTH  = "M1"
}

/** DTO affiché sur la timeline */
@ToString(includeNames = true, includeFields = true)
class ShipOccupation {
    final String navire
    final String dateDeb
    final String dateFin
    double longueur
    double offset

    ShipOccupation(String navire, String dateDeb, String dateFin) {
        this.navire = navire
        this.dateDeb = dateDeb
        this.dateFin = dateFin
    }
}

try {
    // 1) Paramètres utilisateur
    String pStart = _model.getUserParameterValue("dateDebut", null, true)
    String pEnd   = _model.getUserParameterValue("dateFin", null, true)

    Date firstDay
    Date lastDay

    if (pStart && pEnd) {
        Date sd = DateUtils.parseDate(pStart, [Constants.FORMAT_DATE_TIME, Constants.FORMAT_DATE] as String[])
        Date ed = DateUtils.parseDate(pEnd, [Constants.FORMAT_DATE_TIME, Constants.FORMAT_DATE] as String[])
        if (sd.after(ed)) {
            Date tmp = sd; sd = ed; ed = tmp
        }
        firstDay = sd
        lastDay  = ed
    } else {
        String startPeriodKey = _model.getUserParameterValue("rangeDate", ShipConstants.THIS_MONTH, true)
        _logger.debug("rangeDate: {}", startPeriodKey)
        // Calcul du 1er et dernier jour du mois
        DateTime now = new DateTime()
        if (startPeriodKey == ShipConstants.THIS_MONTH) {
            firstDay = now.withDayOfMonth(1).toDate()
            lastDay  = now.dayOfMonth().withMaximumValue().toDate()
        } else if (startPeriodKey == ShipConstants.LAST_MONTH) {
            DateTime lastMonth = now.minusMonths(1)
            firstDay = lastMonth.withDayOfMonth(1).toDate()
            lastDay  = lastMonth.dayOfMonth().withMaximumValue().toDate()
        } else if (startPeriodKey == ShipConstants.NEXT_MONTH) {
            DateTime nextMonth = now.plusMonths(1)
            firstDay = nextMonth.withDayOfMonth(1).toDate()
            lastDay  = nextMonth.dayOfMonth().withMaximumValue().toDate()
        } else {
            firstDay = now.withDayOfMonth(1).toDate()
            lastDay  = now.dayOfMonth().withMaximumValue().toDate()
        }
    }

    _logger.debug("firstDay: '{}', lastDay: '{}'", firstDay, lastDay)
    String startDate = DateUtils.applyPattern(firstDay, Constants.FORMAT_DATE)
    String endDate   = DateUtils.applyPattern(lastDay, Constants.FORMAT_DATE)

    // 2) Recherche AIRS
    ISearchModel sm = new Search()
    sm.setContentTypes(Collections.singletonList(
            ServiceUtils.getServerService().getSearchContentTypeModel(_userContext.getJeton(), ShipConstants.CT_CODE)
    ))

    StringBuilder q = new StringBuilder()
    q.append("(")
    q.append(ShipConstants.CODE_DATE_DEBUT).append(OPERATOR_VALUE_GREATEROREQ).append(startDate)
    q.append(SEARCH_AND_SPACED)
    q.append(ShipConstants.CODE_DATE_DEBUT).append(OPERATOR_VALUE_LESSOREQ).append(endDate)
    q.append(")")
    q.append(SEARCH_OR_SPACED).append("(")
    q.append(ShipConstants.CODE_DATE_FIN).append(OPERATOR_VALUE_GREATEROREQ).append(startDate)
    q.append(SEARCH_AND_SPACED)
    q.append(ShipConstants.CODE_DATE_FIN).append(OPERATOR_VALUE_LESSOREQ).append(endDate)
    q.append(")")
    q.append(SEARCH_OR_SPACED).append("(")
    q.append(ShipConstants.CODE_DATE_DEBUT).append(OPERATOR_VALUE_LESS).append(startDate)
    q.append(SEARCH_AND_SPACED)
    q.append(ShipConstants.CODE_DATE_FIN).append(OPERATOR_VALUE_GREATER).append(endDate)
    q.append(")")

    sm.setAirsRequest(q.toString())
    ServiceUtils.getSearchService().removeSearchModelFromCache(_userContext, sm)
    List<Integer> docIDs = ServiceUtils.getSearchService().getSearch(_userContext, sm, false)
    _logger.debug("docIDs size: {}", (docIDs == null ? 0 : docIDs.size()))

    List<ShipOccupation> occupations = []

    if (CollectionUtils.isNotEmpty(docIDs)) {
        List<String> fieldsKey = [ShipConstants.CODE_NAVIRE, ShipConstants.CODE_DATE_DEBUT, ShipConstants.CODE_DATE_FIN]
        List<List<String>> values = ServiceUtils.getDocumentService()
                .getFieldValues(_userContext, docIDs, sm.getContentTypesAsDomain(), fieldsKey)

        String refDateStr = DateUtils.applyPattern(firstDay, Constants.FORMAT_DATE)

        for (final def row in values) {
            String navire = row.size() > 1 ? row.get(1) : null
            String ddeb   = row.size() > 2 ? row.get(2) : null
            String dfin   = row.size() > 3 ? row.get(3) : null

            if (!ddeb || !dfin) continue

            try {
                ShipOccupation so = new ShipOccupation(navire, ddeb, dfin)
                so.longueur = computeOccupationLength(so, firstDay, lastDay)
                so.offset   = computeDateOffset(refDateStr, so.dateDeb, firstDay)
                if (so.longueur > 0) occupations.add(so)
            } catch (Exception ex) {
                _logger.error("Erreur parsing navire='{}' debut='{}' fin='{}' : {}", navire, ddeb, dfin, ex.message, ex)
            }
        }

        occupations.sort { a, b ->
            int c = a.navire?.compareToIgnoreCase(b.navire ?: "") ?: 0
            if (c != 0) return c
            return a.offset <=> b.offset
        }
    }

    // 3) Résultats
    _result.addProperty("XCategories", JsonOutput.toJson(buildXCategories(firstDay, lastDay)))
    _result.addProperty("YCategories", JsonOutput.toJson(buildYCategories(occupations)))
    _result.addProperty("OccupationLengths", JsonOutput.toJson(buildLengths(occupations)))
    _result.addProperty("OccupationOffsets", JsonOutput.toJson(buildOffsets(occupations)))
    _result.addProperty("occupations", occupations)
    _result.addProperty("rangeOptions", buildRangeOptions())

    _result.outputData.each { k, v -> _logger.debug("result data '{}': '{}'", k, v) }

} catch (Exception e) {
    _logger.error("Navires timeline error: '{}'", e.getMessage(), e)
    _result.status = EnumScriptStatus.KO
    _result.messageSeverity = Severity.ERROR
    _result.messageSummary = e.getMessage()
} finally {
    _logger.debug("<<< navires.timeline - mois")
}

/* ===================== Helpers ===================== */

static String[] buildXCategories(Date startDate, Date endDate) {
    List<String> ret = []
    def d = new DateTime(startDate)
    def last = new DateTime(endDate)
    while (!d.isAfter(last)) {
        ret.add(DateUtils.applyPattern(d.toDate(), Constants.FORMAT_DATE))
        d = d.plusDays(1)
    }
    return ret.toArray(new String[0])
}

static List<String[]> buildYCategories(List<ShipOccupation> data) {
    data.collect { [it.navire, ""] as String[] }
}

static Double[] buildLengths(List<ShipOccupation> data) {
    data.collect { it.longueur } as Double[]
}

static Object[] buildOffsets(List<ShipOccupation> data) {
    data.collect { it.offset <= 0 ? '-' : it.offset } as Object[]
}

static double computeOccupationLength(ShipOccupation so, Date startDate, Date endDate) {
    Date usd = DateUtils.parseDate(so.dateDeb, [Constants.FORMAT_DATE_TIME, Constants.FORMAT_DATE] as String[])
    Date ued = DateUtils.parseDate(so.dateFin, [Constants.FORMAT_DATE_TIME, Constants.FORMAT_DATE] as String[])
    DateTime s = new DateTime(usd.before(startDate) ? startDate : usd)
    DateTime e = new DateTime(ued.after(endDate) ? endDate : ued)
    if (e.isBefore(s)) return 0d
    return (double)(Days.daysBetween(s.withTimeAtStartOfDay(), e.withTimeAtStartOfDay()).getDays() + 1)
}

static double computeDateOffset(String startDateStr, String occStartStr, Date startDate) {
    Date occStart = DateUtils.parseDate(occStartStr, [Constants.FORMAT_DATE_TIME, Constants.FORMAT_DATE] as String[])
    if (occStart.before(startDate)) return 0d
    Date ref = DateUtils.parseDate(startDateStr, [Constants.FORMAT_DATE] as String[])
    return (double)Days.daysBetween(new DateTime(ref).withTimeAtStartOfDay(), new DateTime(occStart).withTimeAtStartOfDay()).getDays()
}

static List<SelectItem> buildRangeOptions() {
    [
        new SelectItem(ShipConstants.THIS_MONTH, "Mois en cours"),
        new SelectItem(ShipConstants.LAST_MONTH, "Mois dernier"),
        new SelectItem(ShipConstants.NEXT_MONTH, "Mois prochain")
    ]
}

package homeblock.custom.shipyard.timeline

import com.digitech.common.script.model.EnumScriptStatus
import com.digitech.common.utils.DateUtils
import com.digitech.dossier.common.model.backend.Constants
import com.digitech.dossier.common.model.backend.airs.ISearchModel
import com.digitech.dossier.common.model.backend.airs.impl.Search
import com.digitech.dossier.common.model.backing.ApplicationModel
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

_logger.debug(">>> navires.timeline")

/** Constantes spécifiques MONTEE_SUR_CALE */
class ShipConstants {
  final static String CT_CODE = "MONTEE_SUR_CALE"

  final static String CODE_NAVIRE = "MSC_NOM_NAVIRE"
  final static String CODE_DATE_DEBUT = "MSC_DATE_DEBUT"
  final static String CODE_DATE_FIN   = "MSC_DATE_FIN"

  // Périodes par défaut (aligné sur votre script)
  final static String THIS_WEEK = "W0"
  final static String LAST_WEEK = "W-1"
  final static String NEXT_WEEK = "W1"
}

/** DTO affiché sur la timeline */
@ToString(includeNames = true, includeFields = true)
class ShipOccupation {
  final String navire
  final String dateDeb
  final String dateFin

  // longueur (nb de jours) du segment
  double longueur
  // décalage (jours) depuis le début de l’intervalle demandé
  double offset

  ShipOccupation(String navire, String dateDeb, String dateFin) {
    this.navire = navire
    this.dateDeb = dateDeb
    this.dateFin = dateFin
  }
}

try {
  // 1) Paramètres utilisateur : dateDebut/dateFin (facultatifs)
  String pStart = _model.getUserParameterValue("dateDebut", null, true)
  String pEnd   = _model.getUserParameterValue("dateFin",   null, true)

  Date firstDay
  Date lastDay

  if (pStart && pEnd) {
    Date sd = DateUtils.parseDate(pStart, new String[] { Constants.FORMAT_DATE_TIME, Constants.FORMAT_DATE })
    Date ed = DateUtils.parseDate(pEnd,   new String[] { Constants.FORMAT_DATE_TIME, Constants.FORMAT_DATE })
    if (sd.after(ed)) {
      // on inverse si l’utilisateur s’est trompé
      Date tmp = sd; sd = ed; ed = tmp
    }
    firstDay = sd
    // on rend la borne fin inclusive en ajoutant 0 jour (mais on travaille en inclusif plus bas)
    lastDay  = ed
  } else {
    // 2) Sinon : période “cette semaine” (comme l’original)
    String startPeriodKey = _model.getUserParameterValue("rangeDate", ShipConstants.THIS_WEEK, true)
    _logger.debug("rangeDate: {}", startPeriodKey)
    firstDay = PeriodBuilderHelper.computeStartingIntervalDay(startPeriodKey, new Date())
    lastDay  = PeriodBuilderHelper.computeStartingIntervalDay("D4", firstDay) // semaine complète (lun→ven) ou 5 jours glissants selon votre helper
  }

  _logger.debug("firstDay: '{}', lastDay: '{}'", firstDay, lastDay)

  String startDate = DateUtils.applyPattern(firstDay, Constants.FORMAT_DATE)
  String endDate   = DateUtils.applyPattern(lastDay, Constants.FORMAT_DATE)

  // 3) Construction de la recherche AIRS (chevauchement d’intervalle)
  ISearchModel sm = new Search()
  sm.setContentTypes(Collections.singletonList(
    ServiceUtils.getServerService().getSearchContentTypeModel(_userContext.getJeton(), ShipConstants.CT_CODE)
  ))

  // Overlap logic:
  // (ddeb >= start && ddeb <= end)
  // OR (dfin >= start && dfin <= end)
  // OR (ddeb < start && dfin > end)
  StringBuilder q = new StringBuilder(SEARCH_OPEN_PARENTHESIS)
  q.append(ShipConstants.CODE_DATE_DEBUT).append(OPERATOR_VALUE_GREATEROREQ).append(startDate)
  q.append(SEARCH_AND_SPACED)
  q.append(ShipConstants.CODE_DATE_DEBUT).append(OPERATOR_VALUE_LESSOREQ).append(endDate)
  q.append(SEARCH_CLOSE_PARENTHESIS)

  q.append(SEARCH_OR_SPACED).append(SEARCH_OPEN_PARENTHESIS)
  q.append(ShipConstants.CODE_DATE_FIN).append(OPERATOR_VALUE_GREATEROREQ).append(startDate)
  q.append(SEARCH_AND_SPACED)
  q.append(ShipConstants.CODE_DATE_FIN).append(OPERATOR_VALUE_LESSOREQ).append(endDate)
  q.append(SEARCH_CLOSE_PARENTHESIS)

  q.append(SEARCH_OR_SPACED).append(SEARCH_OPEN_PARENTHESIS)
  q.append(ShipConstants.CODE_DATE_DEBUT).append(OPERATOR_VALUE_LESS).append(startDate)
  q.append(SEARCH_AND_SPACED)
  q.append(ShipConstants.CODE_DATE_FIN).append(OPERATOR_VALUE_GREATER).append(endDate)
  q.append(SEARCH_CLOSE_PARENTHESIS)

  sm.setAirsRequest(q.toString())

  // 4) Exécution de la recherche & récupération des champs
  ServiceUtils.getSearchService().removeSearchModelFromCache(_userContext, sm)
  List<Integer> docIDs = ServiceUtils.getSearchService().getSearch(_userContext, sm, false)
  _logger.debug("docIDs size: {}", (docIDs == null ? 0 : docIDs.size()))

  List<ShipOccupation> occupations = new ArrayList<>()

  if (CollectionUtils.isNotEmpty(docIDs)) {
    List<String> fieldsKey = Arrays.asList(
      ShipConstants.CODE_NAVIRE,
      ShipConstants.CODE_DATE_DEBUT,
      ShipConstants.CODE_DATE_FIN
    )
    List<List<String>> values = ServiceUtils.getDocumentService()
      .getFieldValues(_userContext, docIDs, sm.getContentTypesAsDomain(), fieldsKey)

    String mondayRef = DateUtils.applyPattern(firstDay, Constants.FORMAT_DATE) // référence pour l’offset

    for (final def row in values) {
      String navire = row.get(0)
      String ddeb   = row.get(1)
      String dfin   = row.get(2)

      ShipOccupation so = new ShipOccupation(navire, ddeb, dfin)
      so.longueur = computeOccupationLength(so, firstDay, lastDay)        // en jours (inclusif)
      so.offset   = computeDateOffset(mondayRef, so.dateDeb, firstDay)    // jours depuis la borne min
      // On écarte les enregistrements hors borne visible (longueur <= 0)
      if (so.longueur > 0) {
        occupations.add(so)
      }
    }

    // Tri : navire asc puis offset asc
    occupations.sort { a, b ->
      int c = a.navire?.compareToIgnoreCase(b.navire ?: "") ?: 0
      if (c != 0) return c
      return a.offset <=> b.offset
    }
  }

  // 5) Sorties pour la timeline
  String[] dates = buildXCategories(firstDay, lastDay)
  _result.addProperty("XCategories", JsonOutput.toJson(dates))

  List<String[]> ycats = buildYCategories(occupations)
  _result.addProperty("YCategories", JsonOutput.toJson(ycats))

  _result.addProperty("OccupationLengths", JsonOutput.toJson(buildLengths(occupations)))
  _result.addProperty("OccupationOffsets", JsonOutput.toJson(buildOffsets(occupations)))
  _result.addProperty("occupations", occupations)

  _result.addProperty("rangeOptions", buildRangeOptions())

  _result.outputData.each { k, v -> _logger.debug("result data '{}': '{}'", k, v) }
}
catch (Exception e) {
  _logger.error("Navires timeline error: '{}'", e.getMessage(), e)
  _result.status = EnumScriptStatus.KO
  _result.messageSeverity = Severity.ERROR
  _result.messageSummary = e.getMessage()
}
finally {
  _logger.debug("<<< navires.timeline")
}

/* ===================== Helpers ===================== */

static String[] buildXCategories(Date startDate, Date endDate) {
  List<String> ret = new ArrayList<>()
  def d = new DateTime(startDate)
  def last = new DateTime(endDate)
  while (!d.isAfter(last)) {
    ret.add(DateUtils.applyPattern(d.toDate(), Constants.FORMAT_DATE))
    d = d.plusDays(1)
  }
  return ret.toArray(new String[0])
}

/** Chaque entrée = [libellé, avatarUrl] ; pas d'avatar ici → "" */
static List<String[]> buildYCategories(List<ShipOccupation> data) {
  List<String[]> ret = new ArrayList<>()
  for (final def d in data) {
    String[] nv = new String[2]
    nv[0] = d.navire
    nv[1] = ""      // pas d’avatar pour un navire
    ret.add(nv)
  }
  return ret
}

static Double[] buildLengths(List<ShipOccupation> data) {
  List<Double> ret = new ArrayList<>()
  for (final def d in data) {
    ret.add(d.longueur)
  }
  return ret.toArray(new Double[0])
}

static Object[] buildOffsets(List<ShipOccupation> data) {
  List<Object> ret = new ArrayList<>()
  for (final def d in data) {
    ret.add(d.offset <= 0 ? '-' : d.offset)
  }
  return ret.toArray(new Object[0])
}

/** Longueur inclusive (en jours) du segment visible dans [startDate, endDate] */
static double computeOccupationLength(ShipOccupation so, Date startDate, Date endDate) {
  Date usd = DateUtils.parseDate(so.dateDeb, new String[] { Constants.FORMAT_DATE_TIME, Constants.FORMAT_DATE })
  Date ued = DateUtils.parseDate(so.dateFin, new String[] { Constants.FORMAT_DATE_TIME, Constants.FORMAT_DATE })

  // bornes tronquées à la fenêtre demandée
  DateTime s = new DateTime(usd.before(startDate) ? startDate : usd)
  DateTime e = new DateTime(ued.after(endDate)   ? endDate   : ued)

  if (e.isBefore(s)) return 0d
  // +1 pour un calcul inclusif
  return (double) (Days.daysBetween(s.withTimeAtStartOfDay(), e.withTimeAtStartOfDay()).getDays() + 1)
}

/** Décalage (en jours) depuis la borne de début (startDate) jusqu’au début de l’occupation (tronqué à 0) */
static double computeDateOffset(String startDateStr, String occStartStr, Date startDate) {
  Date occStart = DateUtils.parseDate(occStartStr, new String[] { Constants.FORMAT_DATE_TIME, Constants.FORMAT_DATE })
  if (occStart.before(startDate)) return 0d

  Date ref = DateUtils.parseDate(startDateStr, new String[] { Constants.FORMAT_DATE })
  DateTime rs = new DateTime(new DateTime(ref).withTimeAtStartOfDay())
  DateTime os = new DateTime(new DateTime(occStart).withTimeAtStartOfDay())
  return (double) Days.daysBetween(rs, os).getDays()
}

/** Options d’intervalle (pour compatibilité UI) */
static List<SelectItem> buildRangeOptions() {
  List<SelectItem> ret = new ArrayList<>()
  ret.add(new SelectItem(ShipConstants.THIS_WEEK, "Cette semaine"))
  ret.add(new SelectItem(ShipConstants.LAST_WEEK, "La semaine dernière"))
  ret.add(new SelectItem(ShipConstants.NEXT_WEEK, "La semaine prochaine"))
  return ret
}

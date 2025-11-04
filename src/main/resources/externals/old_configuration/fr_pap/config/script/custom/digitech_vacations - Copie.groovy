package homeblock.custom.vacations.script.custom

import com.digitech.common.script.model.EnumScriptStatus
import com.digitech.common.utils.DateUtils
import com.digitech.dossier.common.model.backend.Constants
import com.digitech.dossier.common.model.backend.airs.ISearchModel
import com.digitech.dossier.common.model.backend.airs.impl.Search
import com.digitech.dossier.common.model.backing.ApplicationModel
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.DWServiceUtils
import com.digitech.dossier.common.service.ServiceUtils
import com.digitech.dossier.common.utils.time.PeriodBuilderHelper
import com.digitech.dossier.script.model.IScriptResultValueModel.Severity
import groovy.json.JsonOutput
import groovy.transform.ToString
import org.apache.commons.collections4.CollectionUtils
import org.joda.time.DateTime

import javax.faces.model.SelectItem

import static com.digitech.dossier.common.model.backend.ConstantsConfig.*

_logger.debug(">>> vacations")

/**
 * class holding constants
 */
class VacConstants {
  final static String CT_DEM_CONGE = "DEM_CONGE"
  final static String CODE_DATE_DEBUT = "DC_DATE_DEBUT"
  final static String CODE_DATE_FIN = "DC_DATE_FIN"
  final static String CODE_HEURE_DEBUT = "DC_HEURE_DEBUT"
  final static String CODE_HEURE_FIN = "DC_HEURE_FIN"
  final static String CODE_DEMANDEUR = "DC_DEMANDEUR"
  final static String CODE_SERVICE = "DC_SERVICE"
  final static String CODE_STATUS = "DC_STATUT"
  final static String MATIN_CONST = "matin"

  final static String STATUS_SERVICE = "DC_STATUT.ITEM.CODE"
  final static String STATUS_AL_CODE = "DC_STATUT"
  final static String STATUS_CANCEL1 = "ANNULE"
  final static String STATUS_CANCEL2 = "ANNULE-AP-ERR"

  final static String THIS_WEEK = "W0"
  final static String LAST_WEEK = "W-1"
  final static String NEXT_WEEK = "W1"
}

/**
 * class holding computing data DTO
 */
@ToString(includeNames = true, includeFields = true)
class UserVacation {
  final int userID
  final String userFullName
  String avatarUrl
  String dateDeb
  String dateFin
  String heureDeb
  String heureFin
  String service

  // longueur attendue de la ligne
  double longueur
  // son offset
  double offset

  UserVacation(int userID, String userFullName) {
    this.userID = userID
    this.userFullName = userFullName
  }
}

try {

  String startPeriodKey = _model.getUserParameterValue("rangeDate", VacConstants.THIS_WEEK, true)
  _logger.debug("startPeriodKey: '{}'", startPeriodKey)

  def firstDay = PeriodBuilderHelper.computeStartingIntervalDay(startPeriodKey, new Date())
  def lastDay = PeriodBuilderHelper.computeStartingIntervalDay("D4", firstDay) // full week from first (computed) day
  _logger.debug("firstDay: '{}', lastDay: '{}'", firstDay, lastDay)

  String startDate = DateUtils.applyPattern(firstDay, Constants.FORMAT_DATE)
  String endDate = DateUtils.applyPattern(lastDay, Constants.FORMAT_DATE)

  ISearchModel sm = new Search()
  sm.setContentTypes(Collections.singletonList(ServiceUtils.getServerService().getSearchContentTypeModel(_userContext.getJeton(), VacConstants.CT_DEM_CONGE)))

  // query is
  // request.ddeb >= dateDeb && request.ddeb <= dateFin
  // || request.dfin >= dateDeb && request.dfin <= dateFin
  // || (request.ddeb < dateDeb && request.dfin > dateFin
  // start 1
  StringBuilder sbQuery = new StringBuilder(SEARCH_OPEN_PARENTHESIS)
  sbQuery.append(VacConstants.CODE_DATE_DEBUT).append(OPERATOR_VALUE_GREATEROREQ).append(startDate)
  sbQuery.append(SEARCH_AND_SPACED)
  sbQuery.append(VacConstants.CODE_DATE_DEBUT).append(OPERATOR_VALUE_LESSOREQ).append(endDate)
  sbQuery.append(SEARCH_CLOSE_PARENTHESIS)
  // end 1
  // start 2
  sbQuery.append(SEARCH_OR_SPACED).append(SEARCH_OPEN_PARENTHESIS)
  sbQuery.append(VacConstants.CODE_DATE_FIN).append(OPERATOR_VALUE_GREATEROREQ).append(startDate)
  sbQuery.append(SEARCH_AND_SPACED)
  sbQuery.append(VacConstants.CODE_DATE_FIN).append(OPERATOR_VALUE_LESSOREQ).append(endDate)
  sbQuery.append(SEARCH_CLOSE_PARENTHESIS)
  // end 2
  // start 3
  sbQuery.append(SEARCH_OR_SPACED).append(SEARCH_OPEN_PARENTHESIS)
  sbQuery.append(VacConstants.CODE_DATE_DEBUT).append(OPERATOR_VALUE_LESS).append(startDate)
  sbQuery.append(SEARCH_AND_SPACED)
  sbQuery.append(VacConstants.CODE_DATE_FIN).append(OPERATOR_VALUE_GREATER).append(endDate)
  sbQuery.append(SEARCH_CLOSE_PARENTHESIS)
  // end 3

//  sbQuery.append(OPERATOR_NOT).append(VacConstants.STATUS_SERVICE).append(OPERATOR_VALUE_EQUALS).append(SEARCH_QUOTES).append(VacConstants.STATUS_CANCEL1).append(SEARCH_QUOTES)
//  sbQuery.append(OPERATOR_NOT).append(VacConstants.STATUS_SERVICE).append(OPERATOR_VALUE_EQUALS).append(SEARCH_QUOTES).append(VacConstants.STATUS_CANCEL2).append(SEARCH_QUOTES)

  sm.setAirsRequest(sbQuery.toString())

  ServiceUtils.getSearchService().removeSearchModelFromCache(_userContext, sm)
  List<Integer> docIDs = ServiceUtils.getSearchService().getSearch(_userContext, sm, false)
//  _logger.debug("res: '{}'", docIDs)

  List<UserVacation> vacations = new ArrayList()
  if (CollectionUtils.isNotEmpty(docIDs)) {
    List<String> fieldsKey = Arrays.asList(VacConstants.CODE_DEMANDEUR, VacConstants.CODE_DATE_DEBUT, VacConstants.CODE_DATE_FIN, VacConstants.CODE_HEURE_DEBUT,
        VacConstants.CODE_HEURE_FIN, VacConstants.CODE_SERVICE, VacConstants.CODE_STATUS)
    List<List<String>> documentValues = ServiceUtils.getDocumentService().getFieldValues(_userContext, docIDs, sm.getContentTypesAsDomain(), fieldsKey)

    def termCancel1 = ServiceUtils.getAuthorityListService().getTerm(VacConstants.STATUS_AL_CODE, VacConstants.STATUS_CANCEL1)
    def termCancel2 = ServiceUtils.getAuthorityListService().getTerm(VacConstants.STATUS_AL_CODE, VacConstants.STATUS_CANCEL2)
    String appContext = ApplicationModel.getInstance().getContextPath()
    for (final def dcValues in documentValues) {
      _logger.debug("status '{}' (1: '{}', 2: '{}')", dcValues.get(7), termCancel1?.getId(), termCancel2?.getId())

      if (Integer.parseInt(dcValues.get(7)) == termCancel1?.getId() || Integer.parseInt(dcValues.get(7)) == termCancel2?.getId()) {
        _logger.debug("skipping value '{}' as status is cancelled ('{}')", dcValues.get(0), dcValues.get(7))
        continue
      }

      def userId = Integer.valueOf(dcValues.get(1))
      def user = ServiceUtils.getUserService().getUser(userId, true)
      if (user != null) {
        UserVacation uv = new UserVacation(userId, BundleUtils.getTitle(user))
        uv.avatarUrl = appContext + "/" + DWServiceUtils.getAvatarService().getAvatarUrl(user)
        uv.dateDeb = dcValues.get(2)
        uv.dateFin = dcValues.get(3)
        uv.heureDeb = BundleUtils.getTitle(ServiceUtils.getAuthorityListService().getTerm(Integer.valueOf(dcValues.get(4))))
        uv.heureFin = BundleUtils.getTitle(ServiceUtils.getAuthorityListService().getTerm(Integer.valueOf(dcValues.get(5))))
        uv.service = getService(Integer.valueOf(dcValues.get(6)))

        uv.longueur = computeVacationsLength(uv, firstDay, lastDay)
        uv.offset = computeDateOffset(startDate, uv.dateDeb, uv.heureDeb, firstDay)

        vacations.add(uv)
      }
    }
    // sort them by services then full name
    vacations.sort { uv1, uv2 -> uv2.service <=> uv1.service ?: uv2.userFullName <=> uv1.userFullName }
    _logger.debug("user vacations: '{}'", vacations.size())
  }

  String[] dates = buildXCategories(firstDay, lastDay)
  _result.addProperty("XCategories", JsonOutput.toJson(dates))

  List<String[]> users = buildYCategories(vacations)
  _result.addProperty("YCategories", JsonOutput.toJson(users))
  _result.addProperty("VacationLengths", JsonOutput.toJson(buildLengths(vacations)))
  _result.addProperty("VacationOffsets", JsonOutput.toJson(buildOffsets(vacations)))
  _result.addProperty("vacations", vacations)

  _result.addProperty("rangeOptions", buildRangeOptions())

  _result.outputData.each { k, v -> _logger.debug("result data '{}': '{}'", k, v) }
//  _result.addProperty("vacationsJson", JsonOutput.toJson(vacations))
}
catch (Exception e) {
  _logger.error("Digitech vacations error: '{}'", e.getMessage(), e)
  _result.status = EnumScriptStatus.KO
  _result.messageSeverity = Severity.ERROR
  _result.messageSummary = e.getMessage()
}
finally {
  _logger.debug("<<< vacations")
}

static String[] buildXCategories(Date startDate, Date endDate) {
  List<String> ret = new ArrayList<>()

  def sd1 = new DateTime(startDate)
  def sdLastDate = new DateTime(endDate)
  while (sd1 <= sdLastDate) {
    ret.add(DateUtils.applyPattern(sd1.toDate(), Constants.FORMAT_DATE))
    sd1 = sd1.plusDays(1)
  }
  return ret.toArray(new String[0])
}

static List<String[]> buildYCategories(List<UserVacation> data) {
  List<String[]> ret = new ArrayList<>()

  for (final def d in data) {
    String[] nv = new String[2]
    nv[0] = d.service + " - " + d.userFullName
    nv[1] = d.avatarUrl
    ret.add(nv)
  }
//  return ret.toArray(new String[0])
  return ret
}

static Double[] buildLengths(List<UserVacation> data) {
  List<Double> ret = new ArrayList<>()

  for (final def d in data) {
    ret.add(d.longueur)
  }
  return ret.toArray(new Double[0])
}

static Object[] buildOffsets(List<UserVacation> data) {
  List<Object> ret = new ArrayList<>()

  for (final def d in data) {
    ret.add(d.offset <= 0 ? '-' : d.offset)
  }
  return ret.toArray(new Object[0])
}

static double computeVacationsLength(UserVacation uv, Date startDate, Date endDate) {

  def usd = DateUtils.parseDate(uv.dateDeb, new String[] { Constants.FORMAT_DATE_TIME, Constants.FORMAT_DATE})
  def ued = DateUtils.parseDate(uv.dateFin, new String[] { Constants.FORMAT_DATE_TIME, Constants.FORMAT_DATE})

  boolean useFirstDay = usd < startDate
  boolean useLastDay = ued > endDate
  double ret = com.digitech.dossier.common.utils.DateUtils.countWorkingDaysBetween(useFirstDay ? DateUtils.applyPattern(startDate, Constants.FORMAT_DATE) : uv.dateDeb,
      useLastDay ? DateUtils.applyPattern(endDate, Constants.FORMAT_DATE) : uv.dateFin)

//  _logger.debug("uv: {}, ret: {}, useFirstDay {}, useLastDay {}", uv, ret, useFirstDay, useLastDay)

  if (!useFirstDay && !VacConstants.MATIN_CONST.equalsIgnoreCase(uv.heureDeb)) {
    ret -= 0.5
  }
  if (!useLastDay && VacConstants.MATIN_CONST.equalsIgnoreCase(uv.heureFin)) {
    ret -= 0.5
  }
  return ret
}

static double computeDateOffset(String monday, String uvStartDate, String uvStartHeure, Date startDate) {
  def usd = DateUtils.parseDate(uvStartDate, new String[] { Constants.FORMAT_DATE_TIME, Constants.FORMAT_DATE})

  boolean useFirstDay = usd < startDate
  double ret = useFirstDay ? 0 : (com.digitech.dossier.common.utils.DateUtils.countWorkingDaysBetween(monday, uvStartDate) - 1)

//  _logger.debug("monday {}, uvStartDate {}, uvStartHeure {}, startDate {}, useFirstDay {}, ret {}", monday, uvStartDate, uvStartHeure, startDate, useFirstDay, ret)

  if (uvStartHeure != VacConstants.MATIN_CONST) {
    ret += 0.5
  }
  return ret
}

String getService(int svcID) {
  def svcShortLabels = [3 : "2AC", 8: "PSI", 4: "Prod", 5: "R&D", 6: "Scli", 2: "DG", 7: "Com", 9: "P.E.",
                        10: "P.D.C.", 11: "G.F.", 12: "M&C", 13: "Com.Exp.", 14: "PSI Prod", 15: "PSI R&D", 16: "ITQ", 17: "Infra"]

  def svcLabel = svcShortLabels[svcID]
  if (svcLabel == null) {
    svcLabel = BundleUtils.getTitle(ServiceUtils.getServerService().getOrganization(_userContext.getJeton(), svcID))
  }
  return svcLabel
}

static List<SelectItem> buildRangeOptions() {
  List<SelectItem> ret = new ArrayList<>()
  ret.add(new SelectItem(VacConstants.THIS_WEEK, "Cette semaine"))
  ret.add(new SelectItem(VacConstants.LAST_WEEK, "La semaine dernière"))
  ret.add(new SelectItem(VacConstants.NEXT_WEEK, "La semaine prochaine"))
  return ret
}
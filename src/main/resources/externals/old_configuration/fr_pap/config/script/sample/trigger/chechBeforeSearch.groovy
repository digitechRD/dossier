import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;

TEST_OK_SUM                 = "infos_test_script_ispossibleSuppresion_summary_OK"; // TODO : a definir
TEST_KO_SUM                 = "infos_test_script_ispossibleSuppresion_summary_KO"; // TODO : a definir

TEST_OK_DETAIL              = "infos_test_script_ispossibleSuppresion_detail_OK"; // TODO : a definir
TEST_KO_DETAIL              = "infos_test_script_ispossibleSuppresion_detail_KO"; // TODO : a definir

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
result.setValid(false);
result.setMessageSummary(TEST_KO_SUM);
result.setMessageDetail(TEST_KO_DETAIL);
scriptLogger.warn("[isPossiblleSuppression] : suppression non accept�e ");

output.setValue( result );
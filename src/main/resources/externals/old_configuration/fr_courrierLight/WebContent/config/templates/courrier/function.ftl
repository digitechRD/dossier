[#setting locale="fr_FR"]

[#assign today = .now]

[#assign document = reportModel.first]
[#assign templateScriptUtils = statics["com.digitech.dossier.script.service.impl.ScriptMgr"]]     

[#function emetteurBloc]
[#if document.reportFieldsMap.C_DESTINATAIRE??]
[#return templateScriptUtils.invokeGlobalScriptMethod("TemplateScriptUtils", "getEmetteur", [reportModel.first])]
[#else]
[#return templateScriptUtils.invokeGlobalScriptMethod("TemplateScriptUtils", "getProprietaire", [reportModel.first])]
[/#if]
[/#function]

[#function destinataireBloc]
[#if document.reportFieldsMap.C_DESTINATAIRE??]
[#return templateScriptUtils.invokeGlobalScriptMethod("TemplateScriptUtils", "getDestinataire", [reportModel.first])]
[#else]
[#return templateScriptUtils.invokeGlobalScriptMethod("TemplateScriptUtils", "getEmetteur", [reportModel.first])]         
[/#if]
[/#function]

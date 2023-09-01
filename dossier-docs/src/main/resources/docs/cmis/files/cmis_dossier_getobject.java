

public Map<String, String> getObjectIDPropertiesAsString(Session session, String objectId)
    throws Exception{

    try{
      CmisObjectWrapper obj = cmisClient.getObjectById(session, objectId);
      return obj.getProperties().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValueAsString()));
    }
    catch(...){
    }
}

# tag::simple[]
public void createFolder(Session session, String objectId)
    throws Exception {

  try {
    String testString = "TEST CREATE FOLDER";
    Map<String, Object> properties = new HashMap<>();
    properties.put("cmis:objectTypeId", "cm:document:cr");
    properties.put("cmis:baseTypeId", "cmis:folder");
    properties.put("cm:document:cr:crDes", testString);
    properties.put("cm:document:cr:crTheme", "172");
    properties.put("cm:document:cr:crRedacteur", "97");
    properties.put("cm:document:cr:crDate", new Date(531658800));
    ObjectId newObjectId = cmisClient.createObject(session, "ZmxvdzpDUg==", properties, null);
  }
  catch(...){
  }
}
# end::simple[]

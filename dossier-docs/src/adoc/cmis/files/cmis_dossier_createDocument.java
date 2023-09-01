# tag::simple[]
public void updateObject(Session session, String objectId)
    throws Exception {

  try {
    String testString = "TEST_CREATE_DOCUMENT";
    Map<String, Object> properties = new HashMap<>();
    properties.put("cmis:objectTypeId", "cm:attachment");
    properties.put("cmis:baseTypeId", "cmis:document");
    properties.put("cmis:versionLabel", testString);
    properties.put("cmis:contentStreamFileName", "nomDeFichier.pdf");
    properties.put("cmis:name", testString);
    Path contentStream = Paths.get("C:/files/1.pdf");
    ObjectId newObjectId = cmisClient.createObject(session, "ZG9jdW1lbnQ6Q1I6RGVybmllcnMgY29tcHRlIHJlbmR1czo3NjQy", properties, contentStream);
  }
  catch(...) {
  }
}
# end::simple[]

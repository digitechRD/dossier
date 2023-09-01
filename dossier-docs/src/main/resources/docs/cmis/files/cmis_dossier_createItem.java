# tag::simple[]
public void createComment(Session session, String objectId)
  throws Exception {

  try {
    String testString = "TEST COMMENT ";
    Map<String, Object> properties = new HashMap<>();
    properties.put("cmis:name", testString);
    properties.put("cmis:objectTypeId", "cm:comment");
    properties.put("cmis:baseTypeId", "cmis:item");
    ObjectId newObjectId = cmisClient.createObject(session, objectId, properties, null);
  }
  catch(...) {
  }
}
# end::simple[]

# tag::simple[]
public void updateObject(Session session, String objectId)
    throws Exception {

  try {
    String testString = "TEST UPDATE ";
    Map<String, Object> properties = new HashMap<>();
    properties.put("cm:document:cr:crDes", testString);
    properties.put("cm:document:cr:crDate", new Date(531658800));
    cmisClient.updateObject(session, objectId, properties);
  }
  catch(...) {
  }
}
# end::simple[]

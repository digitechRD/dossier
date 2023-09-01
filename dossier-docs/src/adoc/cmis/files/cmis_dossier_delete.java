# tag::simple[]
public void deleteObject(Session session, String objectId)
    throws Exception {

  try {
    cmisClient.deleteObject(session, objectId);
  }
  catch(...) {
  }
}
# end::simple[]

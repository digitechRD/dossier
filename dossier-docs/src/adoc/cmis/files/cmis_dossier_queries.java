
# tag::simple[]
public List<CmisQueryResultWrapper> query(Session session, String query)
    throws Exception{

  return cmisClient.queryObjects(session, query);
}
# end::simple[]

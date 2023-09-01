

public List<CmisObjectWrapper> getObjectChildren(Session session, String objectId)
    throws Exception{

    try{
      CmisObjectWrapper obj = cmisClient.getObjectById(session, objectId);

      return cmisClient.getAllChildren(obj.getCmisObject());
    }
    catch(...){
    }
}

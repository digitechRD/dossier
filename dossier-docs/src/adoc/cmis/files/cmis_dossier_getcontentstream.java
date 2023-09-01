
# tag::getall[]
public ContentStream getContentStream(Session session, String objectId)
    throws Exception{

    try{
      // attention, ContentStream#getStream, si utilisé, doit être fermé !
      return cmisClient.getContentStream(session, objectId);
    }
    catch(...){
    }
}

public String downloadContentStream(Session session, String objectId)
    throws Exception{

  try{
    // renvoie le chemin absolu vers le fichier téléchargé
    return cmisClient.downloadContentStream(session, objectId);
  }
  catch(...){
  }
}
# end::getall[]

# tag::getpartial[]
public ContentStream getPartialContentStream(Session session, String objectId, long offset, long length)
    throws Exception{

  try{
    // attention, ContentStream#getStream, si utilisé, doit être fermé !
    return cmisClient.getContentStream(session, objectId, offset,  length );
  }
  catch(...){
  }
}
# end::getpartial[]

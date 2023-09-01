package com.digitech.dossier.cmis.client;

import java.util.List;
import java.util.Set;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.RepositoryCapabilities;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;

import com.digitech.dossier.cmis.client.exception.CmisClientException;
import com.digitech.dossier.cmis.client.exception.InvalidArgumentException;
import com.digitech.dossier.cmis.client.typedef.CmisObjectWrapper;
import com.digitech.dossier.cmis.client.typedef.CmisQueryResultWrapper;
import com.digitech.dossier.cmis.client.typedef.CmisTypeDefinitionWrapper;

/**
 * Dossier Cmis Client interface
 */
public interface IDossierCmisClient {

  /**
   * inject basic credential (and default ones)
   *
   * @param login user login
   * @param pwd   user password
   */
  void setCredentials(String login, String pwd);

  /**
   * inject language (and default one)
   *
   * @param language expected user language
   */
  void setLanguage(String language);

  /**
   * create CMIS session on 1st repository, using already set credentials
   *
   * @return {@link Session}
   * @throws CmisClientException in case of any exception
   */
  Session createSession()
      throws CmisClientException;

  /**
   * create CMIS session on 1st repository, using already set credentials
   *
   * @param repoId repository Id
   * @return {@link Session}
   * @throws CmisClientException in case of any exception
   */
  Session createSession(String repoId)
      throws CmisClientException;

  /**
   * create CMIS session
   *
   * @param login login
   * @param pwd   password
   * @return {@link Session}
   * @throws CmisClientException in case of any exception
   */
  Session createSession(String login, String pwd)
      throws CmisClientException;

  /**
   * create CMIS session
   *
   * @param repoId repository Id
   * @param login  login
   * @param pwd    password
   * @return {@link Session}
   * @throws CmisClientException in case of any exception
   */
  Session createSession(String repoId, String login, String pwd)
      throws CmisClientException;

  /**
   * create CMIS session
   *
   * @param login    login
   * @param pwd      password
   * @param repoId   repository Id
   * @param language language
   * @return {@link Session}
   * @throws CmisClientException in case of any exception
   */
  Session createSession(String repoId, String login, String pwd, String language)
      throws CmisClientException;

  /**
   * get cmis repository information
   *
   * @param session valid CMIS server session
   * @return {@link RepositoryInfo}
   * @throws InvalidArgumentException if input {@link Session} is invalid
   * @throws CmisClientException      in case of any exception
   */
  RepositoryInfo getRepositoryCmisInfo(Session session)
      throws InvalidArgumentException, CmisClientException;

  /**
   * get repository capabilities
   *
   * @param session valid CMIS server session
   * @return {@link RepositoryCapabilities}
   * @throws InvalidArgumentException if input {@link Session} is invalid
   * @throws CmisClientException      in case of any exception
   */
  RepositoryCapabilities getRepositoryCapabilities(Session session)
      throws InvalidArgumentException, CmisClientException;

  /**
   * check if current repository enabled Query
   *
   * @param session valid CMIS server session
   * @return true if it spport
   * @throws InvalidArgumentException if input {@link Session} is invalid
   * @throws CmisClientException      in case of any exception
   */
  boolean isSupportQuery(Session session)
      throws InvalidArgumentException, CmisClientException;

  /**
   * get type definition from its ID
   *
   * @param session valid CMIS server session
   * @param typeId  type ID
   * @return {@link ObjectType}
   * @throws CmisClientException if input {@link Session} is invalid
   *                             * @throws CmisClientException      in case of any exception
   */
  ObjectType getCmisTypeDefinition(Session session, String typeId)
      throws CmisClientException, InvalidArgumentException;

  /**
   * load type definitions
   *
   * @param session valid CMIS server session
   * @return list of {@link ObjectType}
   * @throws CmisClientException if input {@link Session} is invalid
   * @throws CmisClientException in case of any exception
   */
  List<ObjectType> getCmisTypeDefinitions(Session session)
      throws CmisClientException, InvalidArgumentException;

  /**
   * get type definition from its ID
   *
   * @param session valid CMIS server session
   * @param typeId  type ID
   * @return {@link CmisTypeDefinitionWrapper}
   * @throws CmisClientException if input {@link Session} is invalid
   *                             * @throws CmisClientException      in case of any exception
   */
  CmisTypeDefinitionWrapper getWrappedTypeDefinition(Session session, String typeId)
      throws CmisClientException, InvalidArgumentException;

  /**
   * load type definitions
   *
   * @param session valid CMIS server session
   * @return list of {@link CmisTypeDefinitionWrapper}
   * @throws CmisClientException if input {@link Session} is invalid
   * @throws CmisClientException in case of any exception
   */
  List<CmisTypeDefinitionWrapper> getWrappedTypeDefinitions(Session session)
      throws CmisClientException, InvalidArgumentException;

  /**
   * get root folder
   *
   * @param session valid CMIS server session
   * @return root {@link Folder}
   * @throws CmisClientException      in case of any exception
   * @throws InvalidArgumentException if input {@link Session} is invalid
   */
  Folder getRootFolder(Session session)
      throws CmisClientException, InvalidArgumentException;

  /**
   * get root folder
   *
   * @param parent folder to get children from
   * @return iterable children
   * @throws CmisClientException      in case of any exception
   * @throws InvalidArgumentException if input {@link Session} is invalid
   */
  ItemIterable<CmisObject> getChildren(Folder parent)
      throws CmisClientException, InvalidArgumentException;

  /**
   * get object by its id
   *
   * @param session valid CMIS server session
   * @param id      object id
   * @return {@link CmisObjectWrapper}
   * @throws CmisClientException      in case of any exception
   * @throws InvalidArgumentException if input {@link Session} is invalid
   */
  CmisObjectWrapper getObjectById(Session session, String id)
      throws CmisClientException, InvalidArgumentException;

  /**
   * get object by its id
   *
   * @param session          valid CMIS server session
   * @param id               object id
   * @param filteredMetadata expected returned metadata (by default all)
   * @return {@link CmisObjectWrapper}
   * @throws CmisClientException      in case of any exception
   * @throws InvalidArgumentException if input {@link Session} is invalid
   */
  CmisObjectWrapper getObjectById(Session session, String id, Set<String> filteredMetadata)
      throws CmisClientException, InvalidArgumentException;

  /**
   * get object by its path
   *
   * @param session valid CMIS server session
   * @param path    object path
   * @return {@link CmisObjectWrapper}
   * @throws CmisClientException      in case of any exception
   * @throws InvalidArgumentException if input {@link Session} is invalid
   */
  CmisObjectWrapper getObjectByPath(Session session, String path)
      throws CmisClientException, InvalidArgumentException;

  /**
   * get object by its path
   *
   * @param session          valid CMIS server session
   * @param path             object path
   * @param filteredMetadata expected returned metadata (by default all)
   * @return {@link CmisObjectWrapper}
   * @throws CmisClientException      in case of any exception
   * @throws InvalidArgumentException if input {@link Session} is invalid
   */
  CmisObjectWrapper getObjectByPath(Session session, String path, Set<String> filteredMetadata)
      throws CmisClientException, InvalidArgumentException;

  /**
   * query objects
   *
   * @param session valid CMIS server session
   * @param query   SQL-92 compliant query
   * @return list of results (wrapped to {@link CmisQueryResultWrapper})
   * @throws CmisClientException      in case of any exception
   * @throws InvalidArgumentException if input {@link Session} is invalid
   */
  List<CmisQueryResultWrapper> queryObjects(Session session, String query)
      throws CmisClientException, InvalidArgumentException;

  /**
   * query objects
   *
   * @param session  valid CMIS server session
   * @param query    SQL-92 compliant query
   * @param pageSize page size
   * @return list of results (wrapped to {@link CmisQueryResultWrapper})
   * @throws CmisClientException      in case of any exception
   * @throws InvalidArgumentException if input {@link Session} is invalid
   */
  List<CmisQueryResultWrapper> queryObjects(Session session, String query, int pageSize)
      throws CmisClientException, InvalidArgumentException;

  void downloadContentStream(Session session, String objectId)
      throws CmisClientException, InvalidArgumentException;

}



# tag::connection[]
public class CmisConnection {
  /** The repository id. */
  private static final String REPOSITORY_ID = "myRepoID";

  private Session session = null;

  private IDossierCmisClient cmisClient;

  /**
   * constructor
   *
   * @param appUrl dossier URL
   */
  public CmisConnection(String appUrl) {
    super();
    cmisClient = new DossierBrowserCmisClient(url);
  }

  /**
   * Main method.
   */
  public static void main(String[] args)
      throws Exception {
    String dossierUrl = args[0];
    String login = args[0];
    String pwd = args[0];

    CmisConnection con = new CmisConnection(dossierUrl);
    Session session = con.connect2Repo(login, pwd);
  }

  /**
   * connect to repository
   *
   * @param login login
   * @param pwd   password
   * @return token Session
   * @throws Exception ...
   */
  public Session connect2Repo(String login, String pwd)
      throws Exception {
    Session session = null;
    try {
      // This call will work if there is only one repository !
      session = cmisClient.createSession(login, pwd);
      // else call
      // session = cmisClient.createSession(REPOSITORY_ID, login, pwd);
    }
    catch(...){
    }
    return session;
  }

  # end::connection[]

  # tag::repos[]
  /**
   * get available repository IDs
   *
   * @param login login
   * @param pwd   password
   * @return repo IDs
   * @throws Exception ...
   */
  public List<String> getRepositoryIDs(String login, String pwd)
      throws Exception {
    try {
      List<Repository> repositories = cmisClient.getRepositoriesCmisInfo(login, pwd);
      if(repositories != null) {
        return repositories.stream().map(RepositoryInfo::getId).collect(Collectors.toList())
      }
    }
    catch(...){
    }
    return Collections.emptyList();
  }
  # end::repos[]

}
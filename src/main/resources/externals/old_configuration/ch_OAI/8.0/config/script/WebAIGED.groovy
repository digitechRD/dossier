import com.fasterxml.jackson.annotation.JsonProperty

class WebAIGED {
    
    @JsonProperty("option")
	private String option
    @JsonProperty("n_nss")
	private String n_nss
    @JsonProperty("docs_id")
	private String docs_id
    @JsonProperty("env_ged")
	private String env_ged
    @JsonProperty("uid")
	private String uid
    @JsonProperty("folder")
	private String folder


    WebAIGED(@JsonProperty("option") String option, @JsonProperty("n_nss") String n_nss, @JsonProperty("docs_id")String docs_id,
             @JsonProperty("env_ged") String env_ged, @JsonProperty("uid") String uid, @JsonProperty("folder")String folder ) {
        this.option = option
        this.n_nss = n_nss
        this.docs_id = docs_id
        this.env_ged = env_ged
        this.uid = uid
        this.folder = folder
    }

    WebAIGED() {
    }

    String getOption() {
        return option
    }

    void setOption(String option) {
        this.option = option
    }

    String getN_nss() {
        return n_nss
    }

    void setN_nss(String n_nss) {
        this.n_nss = n_nss
    }

    String getDocs_id() {
        return docs_id
    }

    void setDocs_id(String docs_id) {
        this.docs_id = docs_id
    }

    String getenv_ged() {
        return env_ged
    }

    void setenv_ged(String env_ged) {
        this.env_ged = env_ged
    }

    String getuid() {
        return uid
    }

    void setuid(String uid) {
        this.uid = uid
    }

    String getfolder() {
        return folder
    }

    void setfolder(String folder) {
        this.folder = folder
    }
    
}
import com.fasterxml.jackson.annotation.JsonProperty

class DocumentGED {
    
    @JsonProperty("option")
	private String option
    @JsonProperty("n_nss")
	private String n_nss
    @JsonProperty("doc_id")
	private int doc_id
    @JsonProperty("n_dem")
	private String n_dem
    @JsonProperty("al_assure_type_doc")
	private String al_assure_type_doc
    @JsonProperty("al_assure_groupe_doc")
	private String al_assure_groupe_doc
    @JsonProperty("al_wkf_statut")
	private String al_wkf_statut
    @JsonProperty("u_gest")
	private String u_gest
    @JsonProperty("u_gests")
	private String u_gests
    @JsonProperty("u_gests_histo")
	private String u_gests_histo
    @JsonProperty("u_creat")
	private String u_creat
    @JsonProperty("reattribution")
	private boolean reattribution
    @JsonProperty("nom")
	private String nom
    @JsonProperty("t_prior")
	private String t_prior
    @JsonProperty("d_valeur")
	private String d_valeur


    DocumentGED(@JsonProperty("option") String option, @JsonProperty("n_nss") String n_nss, @JsonProperty("doc_id")int doc_id, @JsonProperty("n_dem") String n_dem, @JsonProperty("al_assure_type_doc") String al_assure_type_doc,
                @JsonProperty("al_assure_groupe_doc") String al_assure_groupe_doc, @JsonProperty("al_wkf_statut") String al_wkf_statut, @JsonProperty("u_gest") String u_gest, @JsonProperty("u_gests") String u_gests, @JsonProperty("u_gests_histo") String u_gests_histo,
                @JsonProperty("u_creat") String u_creat, @JsonProperty("reattribution") boolean reattribution, @JsonProperty("nom") String nom, @JsonProperty("t_prior") String t_prior, @JsonProperty("d_valeur") String d_valeur) {
        this.option = option
        this.n_nss = n_nss
        this.doc_id = doc_id
        this.n_dem = n_dem
        this.al_assure_type_doc = al_assure_type_doc
        this.al_assure_groupe_doc = al_assure_groupe_doc
        this.al_wkf_statut = al_wkf_statut
        this.u_gest = u_gest
        this.u_gests = u_gests
        this.u_gests_histo = u_gests_histo
        this.u_creat = u_creat
        this.reattribution = reattribution
        this.nom=nom
        this.t_prior=t_prior
        this.d_valeur=d_valeur
    }

    DocumentGED() {
    }

    String getD_valeur() {
        return d_valeur
    }

    void setD_valeur(String d_valeur) {
        this.d_valeur = d_valeur
    }

    String d_valeur() {
        return t_prior
    }

    void setT_prior(String t_prior) {
        this.t_prior = t_prior
    }

    String getNom() {
        return nom
    }

    void setNom(String nom) {
        this.nom = nom
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

    int getDoc_id() {
        return doc_id
    }

    void setDoc_id(int doc_id) {
        this.doc_id = doc_id
    }

    String getN_dem() {
        return n_dem
    }

    void setN_dem(String n_dem) {
        this.n_dem = n_dem
    }

    String getAl_assure_type_doc() {
        return al_assure_type_doc
    }

    void setAl_assure_type_doc(String al_assure_type_doc) {
        this.al_assure_type_doc = al_assure_type_doc
    }

    String getAl_assure_groupe_doc() {
        return al_assure_groupe_doc
    }

    void setAl_assure_groupe_doc(String al_assure_groupe_doc) {
        this.al_assure_groupe_doc = al_assure_groupe_doc
    }

    String getAl_wkf_statut() {
        return al_wkf_statut
    }

    void setAl_wkf_statut(String al_wkf_statut) {
        this.al_wkf_statut = al_wkf_statut
    }

    String getU_gest() {
        return u_gest
    }

    void setU_gest(String u_gest) {
        this.u_gest = u_gest
    }

    String getU_gests() {
        return u_gests
    }

    void setU_gests(String u_gests) {
        this.u_gests = u_gests
    }

    String getU_gests_histo() {
        return u_gests_histo
    }

    void setU_gests_histo(String u_gests_histo) {
        this.u_gests_histo = u_gests_histo
    }

    String getU_creat() {
        return u_creat
    }

    void setU_creat(String u_creat) {
        this.u_creat = u_creat
    }

    boolean isReattribution() {
        return reattribution
    }

    void setReattribution(boolean reattribution) {
        this.reattribution = reattribution
    }
    
}
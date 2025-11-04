import com.fasterxml.jackson.annotation.JsonProperty

class AttachmentGED {
    
    @JsonProperty("option")
	private String option
    @JsonProperty("doc_id")
	private int doc_id
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
    @JsonProperty("pagesToTreat")
	private String pagesToTreat
    @JsonProperty("rotationDegree")
	private String rotationDegree
    @JsonProperty("pjtoAdd")
	private byte[] pjtoAdd
    @JsonProperty("intervalPage")
	private String intervalPage

    AttachmentGED(@JsonProperty("option") String option, @JsonProperty("doc_id")int doc_id, @JsonProperty("al_assure_type_doc") String al_assure_type_doc,
                  @JsonProperty("al_assure_groupe_doc") String al_assure_groupe_doc, @JsonProperty("al_wkf_statut") String al_wkf_statut, @JsonProperty("u_gest") String u_gest, @JsonProperty("u_gests") String u_gests, @JsonProperty("u_gests_histo") String u_gests_histo,
                  @JsonProperty("u_creat") String u_creat, @JsonProperty("pageToDelete") String pagesToTreat, @JsonProperty("rotationDegree") String rotationDegree
                  , @JsonProperty("pjtoAdd") byte[] pjtoAdd, @JsonProperty("intervalPage") String intervalPage) {
        this.option = option
        this.doc_id = doc_id
        this.al_assure_type_doc = al_assure_type_doc
        this.al_assure_groupe_doc = al_assure_groupe_doc
        this.al_wkf_statut = al_wkf_statut
        this.u_gest = u_gest
        this.u_gests = u_gests
        this.u_gests_histo = u_gests_histo
        this.u_creat = u_creat
        this.pagesToTreat = pagesToTreat
        this.rotationDegree = rotationDegree
        this.pjtoAdd = pjtoAdd
        this.intervalPage = intervalPage
    }

    AttachmentGED() {
    }

    String getOption() {
        return option
    }

    void setPjtoAdd(byte[] pjtoAdd) {
        this.pjtoAdd = pjtoAdd
    }

    byte[] getPjtoAdd() {
        return pjtoAdd
    }

    void setOption(String option) {
        this.option = option
    }

    String getPagesToTreat() {
        return pagesToTreat
    }

    void setIntervalPage(String intervalPage) {
        this.intervalPage = intervalPage
    }

    String getIntervalPage() {
        return intervalPage
    }

    void setPagesToTreat(String pagesToTreat) {
        this.pagesToTreat = pagesToTreat
    }

    String getRotationDegree() {
        return rotationDegree
    }

    void setRotationDegree(String rotationDegree) {
        this.rotationDegree = rotationDegree
    }

    int getDoc_id() {
        return doc_id
    }

    void setDoc_id(int doc_id) {
        this.doc_id = doc_id
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


    
}
/**
 * Created by Mathieu Toubache on 04.11.2014.
 * Regroupe l'ensenble des variables
 */

public class Constants {
    /* Variable Métier */
        public final static int CCVD_DAY_NUMBER_DATE_DUE_1 = 21;
        public final static int CCVD_DAY_NUMBER_DATE_DUE_2 = 30;
        public final static String CCVD_NIP_REGEX = "((([0-9]{7})|(756[0-9]{10}))[;]?)+";
        public final static Integer CCVD_NIP_MIN_SIZE = 7;
        public final static String CCVD_NSS_CARACTERES_START = "756";

    /* Variable globale AIRS */
        public final static String AIRS_DOSSIER_URL = "http://jabba:8080/AirsDossier/";
        public final static Integer UNLOCK_TYPE = new Integer(0);
        public final static Integer AIRS_NOTE_ID = new Integer(1);
        public final static Integer DOC_LOCKED_BY_OTHER = new Integer(2);
        public final static String DATE_FORMAT = "dd/MM/yyyy";
        public final static String ADV_EVENT_FIELDCHANGE = "ADV_EVENT_FIELDCHANGE";
		public final static String ADV_EVENT_WF_TASK_SUBMIT = "ADV_EVENT_WF_TASK_SUBMIT";

    /* Niveau de secret */
        public final static Integer SECRET_LEVEL_DEFAULT = 500;
        public final static Integer SECRET_LEVEL_AC = 200;

    /* Utilisateur */
        public final static Integer USER_SCAN_ID = 112;

    /* Organisation */
        public final static int ORGANIZATION_DEFAULT_ID = 0;
        public final static int ORGANIZATION_AFFILIATION_ID = 1;
        public final static int ORGANIZATION_ACCORDS_BI_ID = 2;
        public final static int ORGANIZATION_AFFAIRES_FAMILIALES_ID = 3;
        public final static int ORGANIZATION_AFFAIRES_FAMILIALES_PSA_ID = 4;
        public final static int ORGANIZATION_ALLOCATIONS_MATERNITES_CANTONALES_ID = 5;
        public final static int ORGANIZATION_ALLOCATIONS_MATERNITES_FEDERALES_ID = 6;
        public final static int ORGANIZATION_APG_ID = 7;
        public final static int ORGANIZATION_CI_CA_ID = 8;
        public final static int ORGANIZATION_CTA_ID = 9;
        public final static int ORGANIZATION_EMPLOYEURS_ID = 10;
        public final static int ORGANIZATION_ESTIMATIONS_ID = 11;
        public final static int ORGANIZATION_FCF_ID = 12;
        public final static int ORGANIZATION_IJAI_ID = 13;
        public final static int ORGANIZATION_IM_ID = 14;
        public final static int ORGANIZATION_JURIDIQUE_ID = 15;
        public final static int ORGANIZATION_PC_ID = 16;
        public final static int ORGANIZATION_PCF_ID = 17;
        public final static int ORGANIZATION_RFM_ID = 18;
        public final static int ORGANIZATION_PCI_ID = 19;
        public final static int ORGANIZATION_PSA_1_ID = 20;
        public final static int ORGANIZATION_RECOUVREMENT_ID = 21;
        public final static int ORGANIZATION_RENTES_1_ID = 22;
        public final static int ORGANIZATION_RENTES_2_ID = 23;
        public final static int ORGANIZATION_RENTES_3_ID = 24;
        public final static int ORGANIZATION_REVISION_ID = 25;
        public final static int ORGANIZATION_SCAN_ID = 26;
        public final static int ORGANIZATION_RENTES_4_ID = 28;
        public final static int ORGANIZATION_CCVD_ID = 29;
        public final static int ORGANIZATION_FINANCE_ID = 30;
        public final static int ORGANIZATION_PSA_2_ID = 31;
		
	/* Profils */
		public final static int PROFIL_RESPONSABLE_FINANCE_ID = 50;
		public final static int PROFIL_UTILISATEUR_FINANCE_ID = 51;
		public final static int PROFIL_RESPONSABLE_DIRECTEUR_ID = 52;

    /* Content type */
        public final static String CTY_AFFILIATED_FOLDER = "DOSSIER_AFFILIE";
        public final static String CTY_AFFILIATED_DOCUMENT = "DOCUMENT_AFFILIE";
        public final static String CTY_AVIS_MUTATION_DOCUMENT = "DOCUMENT_AVIS_MUTATION";

    /* Champs AIRS */
        public final static String FIELD_AFF_CODE = "N_AFF";
        public final static String FIELD_NSS_CODE = "N_NSS";
        public final static String FIELD_ADRESS_AFF_CODE = "ADRESSE_AFF";
        public final static String FIELD_LASTNAME_AFF_CODE = "NOM_AFF";
        public final static String FIELD_FIRSTNAME_AFF_CODE = "PRENOM_AFF";
        public final static String FIELD_DATE_DUE_CODE = "D_ECHEANCE";
        public final static String FIELD_DATE_ARCHIVE_CODE = "D_ARCHIVAGE";
        public final static String FIELD_TAXING_USER_CODE = "U_TAXATEUR";
		public final static String FIELD_AL_ORGA_WKF_OLD_CODE = "AL_ORGA_WKF_OLD";
        public final static String FIELD_SCANNER_USER_CODE = "U_SCAN";
        public final static String FIELD_ORGANIZATION_WORKFLOW_CODE = "AL_ORGA_WKF";
        public final static String FIELD_STATUS_WORKFLOW_CODE = "STATUT_WORKFLOW";
        public final static String FIELD_COMMENT_CODE = "COM";
        public final static String FIELD_CREATE_DATE_CODE = "D_CREAT";
        public final static String FIELD_FILENAME = "FILENAME";
        public final static String FIELD_DATE_CODE = "D_VALEUR";
        public final static String FIELD_NAVISMUTATION_CODE = "N_AVIS_MUTATION";
        public final static String FIELD_REF_AVS_CODE = "N_AVS";
        public final static String FIELD_NIDE_CODE = "N_IDE";
        public final static String FIELD_REF_CAISSE_AVS_CODE = "REF_CAISSE_AVS";
		public final static String FIELD_DESCRIPTION_CODE = "COM";

    /* Code Liste Autorité */
        // Liste d'autorité des services
        public final static String LIST_SERVICE_CODE = "AL_SERVICE";
        public final static Integer LIST_SERVICE_ID = 5;
        public final static Integer LIST_SERVICE_ITEM_AC_ID = 24;
        public final static Integer LIST_SERVICE_ITEM_AF_ID = 25;
        public final static Integer LIST_SERVICE_ITEM_AF_PSA_ID = 26;
        public final static Integer LIST_SERVICE_ITEM_ALMAT_CANT_ID = 27;
        public final static Integer LIST_SERVICE_ITEM_AMF_ID = 28;
        public final static Integer LIST_SERVICE_ITEM_FM_ID = 29;
        public final static Integer LIST_SERVICE_ITEM_PC_ID = 30;
        public final static Integer LIST_SERVICE_ITEM_PCF_ID = 31;
        public final static Integer LIST_SERVICE_ITEM_RFM_ID = 32;
        public final static Integer LIST_SERVICE_ITEM_RECOUVREMENT_ID = 33;
        public final static Integer LIST_SERVICE_ITEM_RENTES_ID = 34;
        public final static Integer LIST_SERVICE_ITEM_APG_ID = 35;
        public final static Integer LIST_SERVICE_ITEM_IJAI_ID = 36;
        public final static Integer LIST_SERVICE_ITEM_RENTES_AI_ID = 37;
        public final static Integer LIST_SERVICE_ITEM_RENTES_AVS_ID = 38;
        public final static Integer LIST_SERVICE_ITEM_API_AI_ID = 39;
        public final static Integer LIST_SERVICE_ITEM_API_AVS_ID = 40;
        public final static Integer LIST_SERVICE_ITEM_SCAN_ID = 41;
        public final static Integer LIST_SERVICE_ITEM_AVS_ID = 650;
        public final static Integer LIST_SERVICE_ITEM_PCC_ID = 670;
        public final static Integer LIST_SERVICE_ITEM_JU_ID = 695;
        public final static Integer LIST_SERVICE_ITEM_REV_ID = 696;
		public final static Integer LIST_SERVICE_ITEM_FIN_ID = 775;

        // Liste d'autorité des titres
        public final static String LIST_TITLE_CODE = "AL_TITRE";
        public final static Integer LIST_TITLE_ITEM_MR_ID = 93;
        public final static Integer LIST_TITLE_ITEM_MME_ID = 94;

        // Liste d'autorité des statuts
        public final static Integer LIST_STATUS_ID = 7;
		public final static String LIST_STATUS_CODE = "AL_STATUT";
        public final static Integer LIST_STATUS_ITEM_TO_CONTROL_ID = 70;
        public final static Integer LIST_STATUS_ITEM_TO_DISTRIBUTE_ID = 71;
        public final static Integer LIST_STATUS_ITEM_TO_TREAT_ID = 74;
        public final static Integer LIST_STATUS_ITEM_TO_VALID_ID = 75;
        public final static Integer LIST_STATUS_ITEM_ADRESS_CREATED_ID = 76;
        public final static Integer LIST_STATUS_ITEM_ARCHIVE_ID = 77;
        public final static Integer LIST_STATUS_ITEM_AFFILIATE_PCI_ID = 80;
        public final static Integer LIST_STATUS_ITEM_AFFILIATE_EMP_ID = 81;
        public final static Integer LIST_STATUS_ITEM_AFFILIATE_PSA_ID = 82;
        public final static Integer LIST_STATUS_ITEM_WAITING_FOR_REPLY_ID = 83;
        public final static Integer LIST_STATUS_ITEM_IN_TREATMENT_ID = 84;
        public final static Integer LIST_STATUS_ITEM_MUTATION_ID = 87;
        public final static Integer LIST_STATUS_ITEM_TO_CREATE_NIP_ID = 88;
        public final static Integer LIST_STATUS_ITEM_TO_CREATE_NSS_ID = 89;
        public final static Integer LIST_STATUS_ITEM_URGENT_ID = 91;
        public final static Integer LIST_STATUS_ITEM_RESPONSE_OBTAINED_ID = 395;
        public final static Integer LIST_STATUS_ITEM_TO_ARCHIVED_ID = 629;
        public final static Integer LIST_STATUS_ITEM_TO_MOOR_ID = 668;
        public final static Integer LIST_STATUS_ITEM_REQUEST_MOORED_ID = 669;

        // Liste d'autorité des types
        public final static String LIST_TYPE_CODE = "AL_TYPE";
        public final static Integer LIST_TYPE_ID = 9;
        public final static Integer LIST_TYPE_ITEM_AVMUT_ID = 816;

        // Liste d'autorité des types
        public final static String LIST_CATEGORY_CODE = "AL_CATEGORIE";
        public final static Integer LIST_CATEGORY_ID = 3;

        // Liste d'autorité des formes juridiques
        public final static String LIST_FORME_JURIDIQUE_CODE = "AL_FORME_JURIDIQUE";
        public final static Integer LIST_FORME_JURIDIQUE_ID = 11;

        // Liste d'autorité des codes motif OFAS
        public final static String LIST_CODE_MOTIF_OFAS_CODE = "AL_CODE_MOTIF_OFAS";
        public final static Integer LIST_CODE_MOTIF_OFAS_ID = 12;

    // Liste d'autorité des informations pour avis de mutations
    public final static String LIST_CODE_INFORMATIONS_CODE = "AL_INFORMATIONS";
    public final static Integer LIST_CODE_INFORMATIONS_ID = 14;

    /* MAPS */
        //  Service / Niveau de secret
        public static final Map<Integer, String> MAP_SERVICE_SECRET_LEVEL = new HashMap<Integer, String>();
        static
        {
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_PC_ID, "100");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_PCF_ID, "100");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_RFM_ID, "100");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_ALMAT_CANT_ID, "800");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_AMF_ID, "800");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_FM_ID, "700");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_AF_ID, "700");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_AF_PSA_ID, "700");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_AC_ID, "200");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_JU_ID, "200");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_REV_ID, "200");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_RECOUVREMENT_ID, "900");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_IJAI_ID, "400");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_APG_ID, "600");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_RENTES_ID, "300");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_RENTES_AI_ID, "300");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_RENTES_AVS_ID, "300");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_API_AI_ID, "300");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_API_AVS_ID, "300");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_SCAN_ID, "500");
            MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_AVS_ID, "500");
			MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_FIN_ID, "1000");
			MAP_SERVICE_SECRET_LEVEL.put(LIST_SERVICE_ITEM_PCC_ID, "100");
        };

        //  Organisation / Chemin partage réseau
        public static final Map<Integer, String> MAP_ORGANIZATION_SHARE = new HashMap<Integer, String>();
        static
        {
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_DEFAULT_ID, "\\\\dagobah\\O2K_MOD\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_AFFILIATION_ID, "\\\\dagobah\\O2K_MOD\\Sr\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_AFFAIRES_FAMILIALES_ID, "\\\\dagobah\\O2K_MOD\\AF\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_AFFAIRES_FAMILIALES_PSA_ID, "\\\\dagobah\\O2K_MOD\\AF\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_FCF_ID, "\\\\dagobah\\O2K_MOD\\FCF\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_PC_ID, "\\\\dagobah\\O2K_MOD\\PC\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_RFM_ID, "\\\\dagobah\\O2K_MOD\\PCFA\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_PCF_ID, "\\\\dagobah\\O2K_MOD\\PCG\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_APG_ID, "\\\\dagobah\\O2k_MOD\\APG");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_IJAI_ID, "\\\\dagobah\\O2K_MOD\\IJAI\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_RECOUVREMENT_ID, "\\\\dagobah\\O2K_MOD\\RECOUVR\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_ESTIMATIONS_ID, "\\\\dagobah\\O2K_MOD\\RENTES\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_ALLOCATIONS_MATERNITES_FEDERALES_ID, "\\\\dagobah\\O2K_MOD\\ALMAT\\Allocations M. Fédérales\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_CI_CA_ID, "\\\\dagobah\\O2K_MOD\\CI\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_EMPLOYEURS_ID, "\\\\dagobah\\O2K_MOD\\SR\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_CTA_ID, "\\\\dagobah\\O2K_MOD\\SR\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_PCI_ID, "\\\\dagobah\\O2K_MOD\\SR\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_PSA_1_ID, "\\\\dagobah\\O2K_MOD\\SR\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_IM_ID, "\\\\dagobah\\O2K_MOD\\SR\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_JURIDIQUE_ID, "\\\\dagobah\\O2K_MOD\\Juridique\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_RENTES_1_ID, "\\\\dagobah\\O2K_MOD\\RENTES\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_RENTES_2_ID, "\\\\dagobah\\O2K_MOD\\RENTES\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_RENTES_3_ID, "\\\\dagobah\\O2K_MOD\\RENTES\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_RENTES_4_ID, "\\\\dagobah\\O2K_MOD\\RENTES\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_ACCORDS_BI_ID, "\\\\dagobah\\O2K_MOD\\RENTES\\Accords bilatéraux\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_REVISION_ID, "\\\\dagobah\\O2K_MOD\\REVISION\\");
            MAP_ORGANIZATION_SHARE.put(ORGANIZATION_PSA_2_ID, "\\\\dagobah\\O2K_MOD\\SR\\");
        };

        public static final Map<Integer, Integer> MAP_ORGANIZATION_SERVICE = new HashMap<Integer, Integer>();
        static
        {
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_AFFILIATION_ID, LIST_SERVICE_ITEM_AC_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_AFFAIRES_FAMILIALES_ID, LIST_SERVICE_ITEM_AF_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_AFFAIRES_FAMILIALES_PSA_ID, LIST_SERVICE_ITEM_AF_PSA_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_ALLOCATIONS_MATERNITES_CANTONALES_ID, LIST_SERVICE_ITEM_ALMAT_CANT_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_ALLOCATIONS_MATERNITES_FEDERALES_ID, LIST_SERVICE_ITEM_AMF_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_APG_ID, LIST_SERVICE_ITEM_RENTES_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_CI_CA_ID, LIST_SERVICE_ITEM_AC_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_EMPLOYEURS_ID, LIST_SERVICE_ITEM_AC_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_ESTIMATIONS_ID, LIST_SERVICE_ITEM_RENTES_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_FCF_ID, LIST_SERVICE_ITEM_FM_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_IJAI_ID, LIST_SERVICE_ITEM_RENTES_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_IM_ID, LIST_SERVICE_ITEM_AC_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_JURIDIQUE_ID, LIST_SERVICE_ITEM_AC_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_PC_ID, LIST_SERVICE_ITEM_PC_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_RFM_ID, LIST_SERVICE_ITEM_RFM_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_PCF_ID, LIST_SERVICE_ITEM_PCF_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_PCI_ID, LIST_SERVICE_ITEM_AC_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_PSA_1_ID, LIST_SERVICE_ITEM_AC_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_RECOUVREMENT_ID, LIST_SERVICE_ITEM_RECOUVREMENT_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_RENTES_1_ID, LIST_SERVICE_ITEM_RENTES_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_RENTES_2_ID, LIST_SERVICE_ITEM_RENTES_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_RENTES_3_ID, LIST_SERVICE_ITEM_RENTES_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_REVISION_ID, LIST_SERVICE_ITEM_AC_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_SCAN_ID, LIST_SERVICE_ITEM_SCAN_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_RENTES_4_ID, LIST_SERVICE_ITEM_RENTES_ID);
            MAP_ORGANIZATION_SERVICE.put(ORGANIZATION_PSA_2_ID, LIST_SERVICE_ITEM_AC_ID);
        };
		
		public static final Map<String, String> MAP_RENTES_DISTRIBUTION = new HashMap<String, String>();
        static
        {
            MAP_RENTES_DISTRIBUTION.put("22", "A-C");
            MAP_RENTES_DISTRIBUTION.put("23", "D-J");
            MAP_RENTES_DISTRIBUTION.put("24", "K-P");
            MAP_RENTES_DISTRIBUTION.put("28", "Q-Z");
            
        };
		
		public static final Map<String, String> MAP_PSA_DISTRIBUTION = new HashMap<String, String>();
        static
        {
            MAP_PSA_DISTRIBUTION.put("20", "A-J");
            MAP_PSA_DISTRIBUTION.put("31", "K-Z");
        };

    /* AKAZI */
        public final static String AKAZI_NAME_PROCESS = "flow_ccvd";
        public final static List<String> AKAZI_TASKS_TO_TREAT = Collections.unmodifiableList(new ArrayList<String>(){{
            add("TraiterPC");
            add("TraiterAC");
            add("TraiterAF");
            add("TraiterAFPSA");
            add("TraiterAMF");
            add("TraiterREC");
            add("TraiterREN");
            add("TraiterCTE");
            add("ArchiveCTE");
			add("AValiderAC");
			add("MutationCTE");
        }});
            // Outputs
        public final static String AKAZI_OUTPUT_VALID = "VALIDE";
        public final static String AKAZI_OUTPUT_URGENT = "URGENT";
        public final static String AKAZI_OUTPUT_CENTRAL_RESPONSE_OK = "REPONSE CENTRAL OK";
        public final static String AKAZI_OUTPUT_REJECTED = "REJETE";
        public final static String AKAZI_OUTPUT_REFUSED_URGENT = "REFUSE URGENT";
        public final static String AKAZI_OUTPUT_REFUSAL_AWAITING_RESPONSE = "REFUSE EN ATTENTE DE REPONSE";
        public final static String AKAZI_OUTPUT_TO_CREATE_NSS = "NSS A CREER";
        public final static String AKAZI_OUTPUT_MUTATION_IN_PROGRESS = "MUTATION EN COURS";
        public final static String AKAZI_OUTPUT_EMAIL_INDEXE = "MAIL INDEXE";
        public final static String AKAZI_OUTPUT_CORRECTED_INDEX = "INDEXATION CORRIGEE";
        public final static String AKAZI_OUTPUT_IN_TREATMENT = "EN TRAITEMENT";
        public final static String AKAZI_OUTPUT_AWAITING_RESPONSE = "EN ATTENTE DE REPONSE";
        public final static String AKAZI_OUTPUT_AFFILIATE_PSA = "EN ATTENTE AFFIL PSA";
        public final static String AKAZI_OUTPUT_AFFILIATE_PCI = "EN ATTENTE AFFIL PCI";
        public final static String AKAZI_OUTPUT_AFFILIATE_EMP = "EN ATTENTE AFFIL EMP";
        public final static String AKAZI_OUTPUT_REQUEST_MOORED = "DEMANDE AMARREE";
        public final static String AKAZI_OUTPUT_TO_VALID = "A VALIDER";
        public final static String AKAZI_OUTPUT_ARCHIVE = "ARCHIVE";
        public final static String AKAZI_OUTPUT_ADRESS_CREATED = "ADRESSE CREEE";
        public final static String AKAZI_OUTPUT_TO_CONTROL = "A CONTROLER";
        public final static String AKAZI_OUTPUT_NIP_CREATED = "NIP CREE";

            // Workflow Finance
        public final static String FLAG_WORKFLOW_FINANCE = "WORKFIN";

            // Valeur
        public final static String VALUE_CONVERT_PDF  = "Converti en PDF";


            // Taches
        public final static String AKAZI_TASK_TO_CONTROL_REN = "ACONTROLERREN";
        public final static String AKAZI_TASK_TO_CONTROL_PC = "ACONTROLERPC";
        public final static String AKAZI_TASK_MUTATION_CTE = "MUTATIONCTE";
        public final static String AKAZI_TASK_ARCHIVE_CTE = "ARCHIVECTE";

    /* DB AIRS */
        public final static String DB_AIRS_DRIVER = "com.ibm.db2.jcc.DB2Driver";
        public final static String DB_AIRS_URL = "jdbc:db2://airsdbtst:50000/AIRSUSER";
        public final static String DB_AIRS_USERNAME = "db2inst1";
        public final static String DB_AIRS_PASSWORD = "avs_022";

        public final static String DB_AIRS_REQUEST_GET_USERS_BY_ORGANIZATION = "SELECT DISTINCT t1.usr_id , t1.usr_login FROM users t1, user_orga t2 WHERE t1.usr_id = t2.usr_id and t2.org_id = ? AND t1.usr_id != 1 AND t1.usr_id != 237 AND t1.usr_id != 225 AND t1.usr_id != 9 AND t1.usr_id != 27 AND t1.usr_id != 169 AND t1.usr_id != 164 AND t1.usr_id != 170 AND t1.usr_id != 127 AND t1.usr_id != 304 AND t1.usr_id != 169 AND t1.usr_active = 1 ORDER BY t1.usr_login asc";
        public final static String DB_AIRS_REQUEST_GET_USERS_BY_SOME_ORGANIZATIONS = "SELECT DISTINCT t1.usr_id , t1.usr_login FROM users t1, user_orga t2 WHERE t1.usr_id = t2.usr_id  AND t1.usr_id != 1  AND t1.usr_id != 237 AND t1.usr_id != 225 AND t1.usr_id != 9 AND t1.usr_id != 27 AND t1.usr_id != 169 AND t1.usr_id != 164 AND t1.usr_id != 170 AND t1.usr_id != 127 AND t1.usr_id != 304 AND t1.usr_id != 169 AND t1.usr_active = 1 and (t2.org_id = ? or t2.org_id = ? OR t2.org_id = ? OR t2.org_id = ?) ORDER BY t1.usr_login asc";

    /* DB Globaz */
        public final static String DB_GLOBAZ_DRIVER = "com.ibm.as400.access.AS400JDBCDriver";
        public final static String DB_GLOBAZ_URL = "jdbc:as400://chclapr1";
        public final static String DB_GLOBAZ_USERNAME = "digitech";
        public final static String DB_GLOBAZ_PASSWORD = "avs_022";

        public final static String DB_GLOBAZ_CODE_TITLE_MR = "502001";
        public final static String DB_GLOBAZ_CODE_TITLE_MME = "502002";

        public final static String DB_GLOBAZ_REQUEST_GET_IDENTITY_BY_NAFF = "SELECT HTTTTI, HTLDE1, HTLDE2 FROM CCVDPRD.TITIERP where CCVDPRD.TITIERP.HTITIE=?";
        public final static String DB_GLOBAZ_REQUEST_GET_NSS_BY_NAFF = "SELECT HXNAVS FROM CCVDPRD.TIPAVSP WHERE CCVDPRD.TIPAVSP.HTITIE=?";
        //public final static String DB_GLOBAZ_REQUEST_GET_LISTS_ADRESS_BY_NAFF = "SELECT DISTINCT LEFT(TRIM(ccvdprd.tilocap.hjnpa),4) CONCAT ' ' CONCAT TRIM(ccvdprd.tilocap.hjloca) FROM ccvdprd.titierp INNER JOIN ccvdprd.tipersp  ON ( ccvdprd.tipersp.htitie = ccvdprd.titierp.htitie ) INNER JOIN ccvdprd.tipavsp  ON ( ccvdprd.tipavsp.htitie = ccvdprd.titierp.htitie ) LEFT OUTER JOIN ccvdprd.tiaadrp ON ( ccvdprd.tiaadrp.htitie = ccvdprd.titierp.htitie AND heiadr = heiaau AND hettad = 508008 AND hfiapp = 519004 AND heidex = '' AND ( ( 20110505 BETWEEN heddad AND hedfad ) OR ( hedfad = 0 AND heddad <= 20110505 ) ) AND hfiapp <> 519013 AND hfiapp <> 519014 ) LEFT OUTER JOIN ccvdprd.tiadrep ON ( ccvdprd.tiaadrp.haiadr = ccvdprd.tiadrep.haiadr ) LEFT OUTER JOIN ccvdprd.tilocap ON ( ccvdprd.tiadrep.hjiloc = ccvdprd.tilocap.hjiloc ) LEFT OUTER JOIN ccvdprd.tipaysp  ON ( ccvdprd.tilocap.hnipay = ccvdprd.tipaysp.hnipay ) WHERE ccvdprd.titierp.htitie=?";
		public final static String DB_GLOBAZ_REQUEST_GET_LISTS_ADRESS_BY_NAFF = "select case when adrcou1.htitie is null then left(locdom1.HJNPA,4) || ' ' || REPLACE(locdom1.HJLOCA,'¬','''') else left(loccou1.HJNPA,4) || ' ' || REPLACE(loccou1.HJLOCA,'¬','''') end from ccvdprd.titierp tiers left join ccvdprd.TIAADRP adrcou1 on (adrcou1.HFIAPP=519004 and adrcou1.HETTAD=508001 and adrcou1.htitie=tiers.htitie and adrcou1.HEDFAD=0 and adrcou1.heidex='') left join ccvdprd.TIADREP adrcou11 on (adrcou11.HAIADR=adrcou1.HAIADR) left join ccvdprd.tilocap loccou1 on (loccou1.HJILOC=adrcou11.HJILOC) left join ccvdprd.TIAADRP adrdom1 on (adrdom1.HFIAPP=519004 and adrdom1.HETTAD=508008 and adrdom1.htitie=tiers.htitie and adrdom1.HEDFAD=0 and adrdom1.heidex='') left join ccvdprd.TIADREP adrdom11 on (adrdom11.HAIADR=adrdom1.HAIADR) left join ccvdprd.tilocap locdom1 on (locdom1.HJILOC=adrdom11.HJILOC) where tiers.htitie = ?";
        public final static String DB_GLOBAZ_REQUEST_GET_ADRESS_BY_NAFF = "SELECT replace(trim(coalesce(trad_typ.pcolut,'')),'¬','''') as TYPE, tiers.htitie as NIP, replace(trim(coalesce(trad_dom.pcolut,'')),'¬','''') as DOMAINE, replace(trim(coalesce(trad_titre.pcolut,'')),'¬','''') as ADR1, replace(trim(tiers.htlde1) concat ' ' concat trim(tiers.htlde2),'¬','''') as ADR2, replace(adr.haatte,'¬','''') as ADR3, case when length(trim(adr.HACPOS))>0 then 'Case postale ' concat trim(adr.HACPOS) else '' end as ADR4, replace(trim(adr.harue),'¬','''') concat ' ' concat trim(adr.hanrue) AS ADR5, case when loca.hnipay=100 then left(loca.hjnpa,4) concat ' ' concat replace(trim(loca.hjloca),'¬','''') else loca.hjnpa concat ' ' concat replace(trim(loca.hjloca),'¬','''') end AS ADR6 from ccvdprd.titierp tiers left join ccvdprd.fwcoup trad_titre on trad_titre.pcosid=tiers.htttti and trad_titre.plaide='F' left join ccvdprd.tiaadrp avadr on avadr.htitie=tiers.htitie and avadr.hedfad=0 inner join ccvdprd.fwcoup trad_dom on trad_dom.pcosid=avadr.hfiapp and trad_dom.plaide='F' inner join ccvdprd.fwcoup trad_typ on trad_typ.pcosid=avadr.hettad and trad_typ.plaide='F' inner join ccvdprd.tiadrep adr on adr.haiadr=avadr.haiadr inner join ccvdprd.tilocap loca ON loca.hjiloc=adr.hjiloc where tiers.htitie= ?";

        public final static String DB_GLOBAZ_REQUEST_GET_INFORMATIONS_BY_NAVISMUTATION = "SELECT aff.AFAMCR, aff.AFNOM1, aff.AFNOM2, adm.AMNUM || ' ' || adm.AMNOM1 || ' ' || adm.AMNOM2, aff.AFIDE, forjur.PACODE, aff.AFNSS, ofas.pacode FROM AVSDBPRD.FCAFFIP aff LEFT OUTER JOIN avsdbprd.FCADMIP adm ON aff.AFNCAI = adm.AMID LEFT OUTER JOIN avsdbprd.FCPARAP forjur ON aff.AFFJUR = forjur.PAID LEFT OUTER JOIN avsdbprd.FCPARAP ofas ON aff.AFCMOT = ofas.paid WHERE aff.AFAMCR IN (?)";


   /* Applicatif CCVD */
        // Chemins
        public final static String PATH_FOLDER_DEPOSIT = "C:\\AIRS\\DIGITECH\\";
        public final static String PATH_APPLICATION_WORD = "D:\\Program Files\\Microsoft Office\\Office\\WINWORD.exe";
        public final static String PATH_APPLICATION_OPENOFFICE = "/opt/openoffice.org3/program/";
        public final static String PATH_APPLICATION_DOWNLOAD_LINK = "/opt/digitech/apache-tomcat-webapps/AirsDossier/download";
		public final static String PATH_APPLICATION_DOWNLOAD = "/opt/digitech/apache-tomcat-7.0.57/webapps/AirsDossier/download";
            // Liste des champs a recupere ainsi que dossier temporaire cote client :
            // Ficher contenant la liste des champs/signet a remplir cette valeur doit aussi etre rensigner dans la macro d'insertion
            // Le fichier office temporaire sera aussi present a cet endroit
        public final static String PATH_FILE_REMINDER_PROPERTIES = "C:\\AIRS\\DIGITECH\\fieldsReminder.properties";
        public final static String PATH_FILE_FIELDS_PROPERTIES = "C:\\AIRS\\DIGITECH\\fields.properties";

        // Applications
        public final static String APPLICATION_WORD_EXTENSION = ".DOC";
        public final static String APPLICATION_EXCEL_EXTENSION = ".XLS";
		public final static String APPLICATION_EXCEL_EXTENSION_NEW = ".XLSX";
        public final static String APPLICATION_PDF_EXTENSION = ".PDF";
        public final static String APPLICATION_OPENOFFICE_HOST = "localhost";
        public final static String APPLICATION_OPENOFFICE_PORT = "8100";
}

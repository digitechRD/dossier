import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name="root")
class ExportSedex {
    String env_ged
    String id_lot_sedex
    String nss
    String uid
    String folder
    String date_export
    String hours_export
    String supplier
    String ecm_version
    String ecm_build
    List<Row> rows

    ExportSedex() {
    }

    ExportSedex(String env_ged, String id_lot_sedex, String nss, String uid, String folder, String date_export, String hours_export, String supplier, String ecm_version, String ecm_build) {
        this.env_ged = env_ged
        this.id_lot_sedex = id_lot_sedex
        this.nss = nss
        this.uid = uid
        this.folder = folder
        this.date_export = date_export
        this.hours_export = hours_export
        this.supplier = supplier
        this.ecm_version = ecm_version
        this.ecm_build = ecm_build
    }

    void setRows(List<Row> rows) {
        this.rows = rows
    }
}

class Row{
    String filename
    String original_extension
    String current_extension
    String date_document
    String id_type_doc
    String label_fr
    String label_it
    String label_de

    Row() {
    }

    Row(String filename, String original_extension, String current_extension, String date_document, String id_type_doc, String label_fr, String label_it, String label_de) {
        this.filename = filename
        this.original_extension = original_extension
        this.current_extension = current_extension
        this.date_document = date_document
        this.id_type_doc = id_type_doc
        this.label_fr = label_fr
        this.label_it = label_it
        this.label_de = label_de
    }
}
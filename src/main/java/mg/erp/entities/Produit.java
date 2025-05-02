package mg.erp.entities;

public class Produit {
    private String name;           // Code de la ligne item dans ERP
    private String item_code;      // Code article
    private String item_name;      // Nom de l’article
    private String description;    // Description
    private Double qty;            // Quantité
    private String uom;            // Unité
    private String rate;           // Prix unitaire
    private String schedule_date;  // Date de livraison prévue
    private String devis_name;     // Nom du devis (parent)
    private String fournisseur;    // Nom du/des fournisseur(s)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getSchedule_date() {
        return schedule_date;
    }

    public void setSchedule_date(String schedule_date) {
        this.schedule_date = schedule_date;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDevis_name() {
        return devis_name;
    }

    public void setDevis_name(String devis_name) {
        this.devis_name = devis_name;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }
}

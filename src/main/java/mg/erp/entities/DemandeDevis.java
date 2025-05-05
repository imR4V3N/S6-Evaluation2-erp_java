package mg.erp.entities;

import com.fasterxml.jackson.databind.JsonNode;
import mg.erp.utils.Config;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class DemandeDevis {
    private String name;
    private String transaction_date;
    private String status;
    private List<Fournisseur> fournisseurs;
    private List<Produit> produits;
    private double total;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Fournisseur> getFournisseurs() {
        return fournisseurs;
    }

    public void setFournisseurs(List<Fournisseur> fournisseurs) {
        this.fournisseurs = fournisseurs;
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public void setProduits(List<Produit> produits) {
        this.produits = produits;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    private List<JsonNode> fetchDevisList(String baseUrl, HttpEntity<String> entity) throws Exception {
        String url = baseUrl + "/api/resource/Request for Quotation?fields=[\"name\",\"transaction_date\",\"status\"]";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        return response.getBody().path("data").findValues("name");
    }

    private DemandeDevis processDevisDetail(String baseUrl, JsonNode devisNode, String supplierName, HttpEntity<String> entity) throws Exception {
        String devisName = devisNode.asText();
        String urlDetail = baseUrl + "/api/resource/Request for Quotation/" + devisName;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> detailResponse = restTemplate.exchange(urlDetail, HttpMethod.GET, entity, JsonNode.class);
        JsonNode data = detailResponse.getBody().path("data");

        if (containsSupplier(data.path("suppliers"), supplierName)) {
            return buildDevis(data);
        }

        return null;
    }

    private boolean containsSupplier(JsonNode suppliers, String supplierName) {
        for (JsonNode supplier : suppliers) {
            if (supplierName.equalsIgnoreCase(supplier.path("supplier_name").asText())) {
                return true;
            }
        }
        return false;
    }

    private DemandeDevis buildDevis(JsonNode data) {
        DemandeDevis demandeDevis = new DemandeDevis();
        demandeDevis.setName(data.path("name").asText());
        demandeDevis.setTransaction_date(data.path("transaction_date").asText());
        demandeDevis.setStatus(data.path("status").asText());

        List<Fournisseur> supplierList = new ArrayList<>();
        for (JsonNode supp : data.path("suppliers")) {
            Fournisseur fournisseur = new Fournisseur();
            fournisseur.setName(supp.path("supplier").asText());
            fournisseur.setSupplier_name(supp.path("supplier_name").asText());
            fournisseur.setQuote_status(supp.path("quote_status").asText());
            supplierList.add(fournisseur);
        }
        demandeDevis.setFournisseurs(supplierList);

        List<Produit> items = new ArrayList<>();
        double total = 0.0;
        for (JsonNode item : data.path("items")) {
            Produit produit = new Produit();
            produit.setItem_code(item.path("item_code").asText());
            produit.setUom(item.path("uom").asText());
            produit.setQty(item.path("qty").asDouble());

            double rate = item.path("rate").asDouble(); // ou item.path("price_list_rate")
            total += produit.getQty();

            produit.setRate(String.valueOf(rate));
            items.add(produit);
        }
        demandeDevis.setProduits(items);
        demandeDevis.setTotal(total);

        return demandeDevis;
    }

    public List<DemandeDevis> fetchDevisForSupplier(String supplierName, String sid, HttpEntity<String> entity, ConfigurableEnvironment configurableEnvironment) throws Exception {
        List<DemandeDevis> result = new ArrayList<>();
        String baseUrl = new Config().getErpUrl(configurableEnvironment);

        List<JsonNode> devisList = fetchDevisList(baseUrl, entity);
        for (JsonNode devisNode : devisList) {
            DemandeDevis demandeDevis1 = processDevisDetail(baseUrl, devisNode, supplierName, entity);
            if (demandeDevis1 != null) {
                result.add(demandeDevis1);
            }
        }

        return result;
    }
}

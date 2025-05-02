package mg.erp.entities;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class BonCommande {
    private String name;
    private String supplier;
    private String supplier_name;
    private String transaction_date;
    private String schedule_date;
    private String status;
    private double total_qty;
    private double grand_total;
    private String currency;
    private String in_words;
    private String company;
    // Getters & Setters


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getSchedule_date() {
        return schedule_date;
    }

    public void setSchedule_date(String schedule_date) {
        this.schedule_date = schedule_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotal_qty() {
        return total_qty;
    }

    public void setTotal_qty(double total_qty) {
        this.total_qty = total_qty;
    }

    public double getGrand_total() {
        return grand_total;
    }

    public void setGrand_total(double grand_total) {
        this.grand_total = grand_total;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIn_words() {
        return in_words;
    }

    public void setIn_words(String in_words) {
        this.in_words = in_words;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<BonCommande> fetchAndFilterBonsDeCommande(String fournisseurName, String url, HttpEntity<String> entity) throws Exception {
        List<BonCommande> result = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

        JsonNode bonList = response.getBody().path("data");
        for (JsonNode bon : bonList) {
            if (fournisseurName.equalsIgnoreCase(bon.path("supplier_name").asText())) {
                result.add(mapJsonToBonCommande(bon));
            }
        }
        return result;
    }


    private BonCommande mapJsonToBonCommande(JsonNode bon) {
        BonCommande commande = new BonCommande();
        commande.setName(bon.path("name").asText());
        commande.setSupplier(bon.path("supplier").asText());
        commande.setSupplier_name(bon.path("supplier_name").asText());
        commande.setTransaction_date(bon.path("transaction_date").asText());
        commande.setSchedule_date(bon.path("schedule_date").asText());
        commande.setStatus(bon.path("status").asText());
        commande.setTotal_qty(bon.path("total_qty").asDouble());
        commande.setGrand_total(bon.path("grand_total").asDouble());
        commande.setCurrency(bon.path("currency").asText());
        commande.setIn_words(bon.path("in_words").asText());
        commande.setCompany(bon.path("company").asText());
        return commande;
    }
}


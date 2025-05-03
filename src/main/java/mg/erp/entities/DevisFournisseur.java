package mg.erp.entities;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class DevisFournisseur {
    private String name;
    private String status;
    private String supplier;
    private String company;
    private String transactionDate;
    private String validTill;
    private String currency;
    private double totalQty;
    private double total;
    private double grandTotal;
    private String supplierAddress;
    private String shippingAddress;
    private String billingAddress;


    // Getters et Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getValidTill() {
        return validTill;
    }

    public void setValidTill(String validTill) {
        this.validTill = validTill;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(double totalQty) {
        this.totalQty = totalQty;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }

    public void setSupplierAddress(String supplierAddress) {
        this.supplierAddress = supplierAddress;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

//    ------------------------------------- DEVIS FOURNISSEUR -------------------------------------
    public List<DevisFournisseur> fetchDevisFournisseurs(String fournisseurName, String url, HttpEntity<String> entity) {
        RestTemplate restTemplate = new RestTemplate();
        List<DevisFournisseur> result = new ArrayList<>();

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

        JsonNode data = response.getBody().path("data");

        for (JsonNode bon : data) {
            if (fournisseurName.equalsIgnoreCase(bon.path("supplier").asText())) {
                DevisFournisseur devis = mapJsonToDevisFournisseur(bon);
                result.add(devis);
            }
        }
        return result;
    }

    private DevisFournisseur mapJsonToDevisFournisseur(JsonNode bon) {
        DevisFournisseur devis = new DevisFournisseur();
        devis.setName(bon.path("name").asText());
        devis.setStatus(bon.path("status").asText());
        devis.setSupplier(bon.path("supplier").asText());
        devis.setCompany(bon.path("company").asText());
        devis.setTransactionDate(bon.path("transaction_date").asText());
        devis.setValidTill(bon.path("valid_till").asText());
        devis.setCurrency(bon.path("currency").asText());
        devis.setTotalQty(bon.path("total_qty").asDouble());
        devis.setTotal(bon.path("total").asDouble());
        devis.setGrandTotal(bon.path("grand_total").asDouble());
        devis.setSupplierAddress(bon.path("supplier_address").asText());
        devis.setShippingAddress(bon.path("shipping_address").asText());
        devis.setBillingAddress(bon.path("billing_address").asText());

        return devis;
    }
}

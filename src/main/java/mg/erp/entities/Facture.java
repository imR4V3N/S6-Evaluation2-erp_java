package mg.erp.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

public class Facture {
    private String name;
    private String supplier;
    private String posting_date;
    private String due_date;
    private String status;
    private double outstanding_amount;
    private double grand_total;
    private String currency;

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

    public String getPosting_date() {
        return posting_date;
    }

    public void setPosting_date(String posting_date) {
        this.posting_date = posting_date;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getOutstanding_amount() {
        return outstanding_amount;
    }

    public void setOutstanding_amount(double outstanding_amount) {
        this.outstanding_amount = outstanding_amount;
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

//    --------------------------- FACTURE ---------------------------

    public List<Facture> fetchFactures(String url, HttpEntity<String> entity) throws HttpClientErrorException {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        JsonNode factureList = response.getBody().path("data");

        return mapJsonToFactures(factureList);
    }

    private List<Facture> mapJsonToFactures(JsonNode factureList) {
        List<Facture> factures = new ArrayList<>();
        for (JsonNode factureNode : factureList) {
            Facture facture = new Facture();
            facture.setName(factureNode.path("name").asText());
            facture.setSupplier(factureNode.path("supplier").asText());
            facture.setPosting_date(factureNode.path("posting_date").asText());
            facture.setDue_date(factureNode.path("due_date").asText());
            facture.setStatus(factureNode.path("status").asText());
            facture.setOutstanding_amount(factureNode.path("outstanding_amount").asDouble());
            facture.setGrand_total(factureNode.path("grand_total").asDouble());
            facture.setCurrency(factureNode.path("currency").asText());
            factures.add(facture);
        }
        return factures;
    }


//    ------------------------- PAYER LA FACTURE -------------------------
    public JsonNode fetchPaymentEntry(RestTemplate restTemplate, ObjectMapper mapper, String baseUrl, HttpHeaders headers, String factureName) throws Exception {
        ObjectNode body = mapper.createObjectNode();
        body.put("dt", "Purchase Invoice");
        body.put("dn", factureName);

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + "/api/method/erpnext.accounts.doctype.payment_entry.payment_entry.get_payment_entry",
                HttpMethod.POST,
                entity,
                JsonNode.class
        );

        JsonNode message = response.getBody().path("message");
        if (message.isMissingNode()) {
            throw new IllegalStateException("Aucun message retourné par l'API.");
        }
        return message;
    }

    public void modifyPaymentEntry(JsonNode paymentEntry, double montantPartiel) {
        ((ObjectNode) paymentEntry).put("name", "new-payment-entry-qxksrrpydn");
        ((ObjectNode) paymentEntry).put("__last_sync_on", "2025-05-02T05:45:45.900Z");
        ((ObjectNode) paymentEntry).put("reference_no", "0706890");

        ((ObjectNode) paymentEntry).put("paid_amount", montantPartiel);
        ((ObjectNode) paymentEntry).put("paid_amount_after_tax", montantPartiel);
        ((ObjectNode) paymentEntry).put("base_paid_amount", montantPartiel);
        ((ObjectNode) paymentEntry).put("base_paid_amount_after_tax", montantPartiel);
        ((ObjectNode) paymentEntry).put("received_amount", montantPartiel);
        ((ObjectNode) paymentEntry).put("received_amount_after_tax", montantPartiel);
        ((ObjectNode) paymentEntry).put("base_received_amount", montantPartiel);
        ((ObjectNode) paymentEntry).put("base_received_amount_after_tax", montantPartiel);
        ((ObjectNode) paymentEntry).put("total_allocated_amount", montantPartiel);
        ((ObjectNode) paymentEntry).put("base_total_allocated_amount", montantPartiel);
        ((ObjectNode) paymentEntry).put("unallocated_amount", 0.0);
        ((ObjectNode) paymentEntry).put("difference_amount", 0.0);

        for (JsonNode ref : paymentEntry.path("references")) {
            ((ObjectNode) ref).put("__islocal", 1);
            ((ObjectNode) ref).put("parent", "new-payment-entry-qxksrrpydn");
            ((ObjectNode) ref).put("name", "new-payment-entry-reference-ietpmvfluv");
            ((ObjectNode) ref).put("allocated_amount", montantPartiel);
        }
    }

    public void saveAndSubmitPaymentEntry(RestTemplate restTemplate, ObjectMapper mapper, String baseUrl, HttpHeaders headers, JsonNode paymentEntry) throws Exception {
        ObjectNode saveBody = mapper.createObjectNode();
        saveBody.put("doc", mapper.writeValueAsString(paymentEntry));

        // Sauvegarde
        saveBody.put("action", "Save");
        HttpEntity<String> saveEntity = new HttpEntity<>(saveBody.toString(), headers);
        ResponseEntity<JsonNode> response1 = restTemplate.exchange(
                baseUrl + "/api/method/frappe.desk.form.save.savedocs",
                HttpMethod.POST,
                saveEntity,
                JsonNode.class
        );


        // Soumission
        saveBody.put("action", "Submit");
        HttpEntity<String> submitEntity = new HttpEntity<>(saveBody.toString(), headers);
        ResponseEntity<JsonNode> response2 = restTemplate.exchange(
                baseUrl + "/api/method/frappe.desk.form.save.savedocs",
                HttpMethod.POST,
                submitEntity,
                JsonNode.class
        );
    }

}

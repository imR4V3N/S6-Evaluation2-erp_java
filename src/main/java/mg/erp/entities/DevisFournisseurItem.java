package mg.erp.entities;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class DevisFournisseurItem {
    private String itemCode;
    private String itemName;
    private double qty;
    private double rate;
    private double amount;
    private String uom;
    private String devisName;


    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getDevisName() {
        return devisName;
    }

    public void setDevisName(String devisName) {
        this.devisName = devisName;
    }

    public List<DevisFournisseurItem> fetchItemsForDevis(String devisName, HttpEntity<String> entity, String baseUrl) {
        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + "/api/resource/Supplier Quotation/" + devisName + "?fields=[\"*\"]";

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        JsonNode itemsNode = response.getBody().path("data").path("items");

        List<DevisFournisseurItem> items = new ArrayList<>();
        for (JsonNode itemNode : itemsNode) {
            DevisFournisseurItem item = new DevisFournisseurItem();
            item.setItemCode(itemNode.path("item_code").asText());
            item.setItemName(itemNode.path("item_name").asText());
            item.setQty(itemNode.path("qty").asDouble());
            item.setRate(itemNode.path("rate").asDouble());
            item.setAmount(itemNode.path("amount").asDouble());
            item.setUom(itemNode.path("uom").asText());
            item.setDevisName(itemNode.path("parent").asText());
            items.add(item);
        }

        return items;
    }

}


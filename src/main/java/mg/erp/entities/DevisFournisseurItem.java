package mg.erp.entities;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpSession;
import mg.erp.utils.Config;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, Object> fetchItemsForDevis(String devisName, HttpEntity<String> entity, String baseUrl) {
        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + "/api/resource/Supplier Quotation/" + devisName + "?fields=[\"*\"]";

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        JsonNode itemsNode = response.getBody().path("data").path("items");

        String supplier = response.getBody().path("data").path("supplier").asText();

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

        Map<String, Object> result = new HashMap<>();
        result.put("supplier", supplier);
        result.put("items", items);

        return result;
    }


//    ------------------------------ MODIFIER PRIX -------------------------------
    public String modifierPrixItemDevis(String devisName, String itemCode, double nouveauPrix, HttpSession session, ConfigurableEnvironment configurableEnvironment) {
        Auth user = (Auth) session.getAttribute("user");

        // Vérifier si l'utilisateur est authentifié
        if (user.getSid() == null) {
            return "redirect:/";
        }

        // Initialisation du RestTemplate pour effectuer des appels API
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", "sid=" + user.getSid());

        String erpUrl = new Config().getErpUrl(configurableEnvironment);

        // 1. Récupérer le devis et ses détails (y compris fournisseur et items)
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                erpUrl + "/api/resource/Supplier Quotation/" + devisName + "?fields=[\"name\", \"supplier\", \"items\", \"docstatus\"]",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        JsonNode devis = response.getBody().path("data");
        String supplier = devis.path("supplier").asText();
        JsonNode items = devis.path("items");
        int docstatus = devis.path("docstatus").asInt();
        String nouveauDevisName = devisName;

        // Si le devis est annulé, on ne peut pas le modifier
        if (docstatus == 2) {
            throw new RuntimeException("Le devis " + devisName + " est annulé et ne peut pas être modifié.");
        }

        // 2. Récupérer tous les bons de commande (sans filtre sur supplier_quotation)
        ResponseEntity<JsonNode> poCheck = restTemplate.exchange(
                erpUrl + "/api/resource/Purchase Order?fields=[\"name\", \"ref_sq\", \"docstatus\"]",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );

        // Parcourir les bons de commande pour trouver ceux associés au devis fournisseur
        boolean poFound = false;
        for (JsonNode po : poCheck.getBody().path("data")) {
            if (po.path("ref_sq").asText().equals(devisName)) {
                poFound = true;
                String poName = po.path("name").asText();
                int poDocStatus = po.path("docstatus").asInt();

                // Annuler le bon de commande uniquement si aucune réception ou facture n'est associée
                if (poDocStatus == 1) {
                    Map<String, Object> cancelPOPayload = Map.of("docstatus", 2);
                    restTemplate.exchange(
                            erpUrl + "/api/resource/Purchase Order/" + poName,
                            HttpMethod.PUT,
                            new HttpEntity<>(cancelPOPayload, headers),
                            String.class
                    );
                }
            }
        }

        if (!poFound) {
            System.out.println("Aucun bon de commande lié à ce devis fournisseur.");
        }

        // Vérifier si l'item existe dans le devis et récupérer l'ID complet de l'item
        String itemId = null;
        for (JsonNode item : items) {
            if (item.path("item_code").asText().equals(itemCode)) {
                itemId = item.path("name").asText();  // Récupérer l'ID complet de l'item
                break;
            }
        }

        if (itemId == null) {
            throw new RuntimeException("Item " + itemCode + " non trouvé dans le devis " + devisName);
        }

        // Si le devis est soumis, il faut d'abord l'annuler
        if (docstatus == 1) {
            // 2. Annuler le devis actuel
            Map<String, Object> cancelPayload = Map.of("docstatus", 2);
            restTemplate.exchange(
                    erpUrl + "/api/resource/Supplier Quotation/" + devisName,
                    HttpMethod.PUT,
                    new HttpEntity<>(cancelPayload, headers),
                    String.class
            );

            // 3. Créer une nouvelle version amendée avec les informations manquantes
            Map<String, Object> amendPayload = Map.of(
                    "doctype", "Supplier Quotation",
                    "amended_from", devisName,
                    "supplier", supplier, // Ajouter le fournisseur
                    "items", items // Ajouter les items
            );
            ResponseEntity<JsonNode> amendResponse = restTemplate.postForEntity(
                    erpUrl + "/api/resource/Supplier Quotation",
                    new HttpEntity<>(amendPayload, headers),
                    JsonNode.class
            );
            nouveauDevisName = amendResponse.getBody().path("data").path("name").asText();
        }

        // 4. Re-récupérer le devis (amendé) pour trouver le bon item à modifier
        ResponseEntity<JsonNode> newDevisResponse = restTemplate.exchange(
                erpUrl + "/api/resource/Supplier Quotation/" + nouveauDevisName + "?fields=[\"items\"]",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        JsonNode newItems = newDevisResponse.getBody().path("data").path("items");

        String newItemId = null;
        for (JsonNode item : newItems) {
            if (item.path("item_code").asText().equals(itemCode)) {
                newItemId = item.path("name").asText();  // <- le nouvel item
                break;
            }
        }

        if (newItemId == null) {
            throw new RuntimeException("Item " + itemCode + " non trouvé dans le nouveau devis " + nouveauDevisName);
        }

        // Modifier le nouvel item avec le nouveau prix
        Map<String, Object> updateItemPayload = Map.of("rate", nouveauPrix);
        try {
            restTemplate.exchange(
                    erpUrl + "/api/resource/Supplier Quotation Item/" + newItemId,
                    HttpMethod.PUT,
                    new HttpEntity<>(updateItemPayload, headers),
                    String.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'item : " + e.getMessage());
        }

        // 5. Soumettre le nouveau devis
        if (!nouveauDevisName.equals(devisName)) {
            Map<String, Object> submitPayload = Map.of("docstatus", 1);
            restTemplate.exchange(
                    erpUrl + "/api/resource/Supplier Quotation/" + nouveauDevisName,
                    HttpMethod.PUT,
                    new HttpEntity<>(submitPayload, headers),
                    String.class
            );
        }

        if (nouveauDevisName.equals(devisName) && docstatus == 0) {
            restTemplate.exchange(
                    erpUrl + "/api/resource/Supplier Quotation/" + nouveauDevisName,
                    HttpMethod.PUT,
                    new HttpEntity<>(Map.of(), headers),
                    String.class
            );
            restTemplate.exchange(
                    erpUrl + "/api/resource/Supplier Quotation/" + nouveauDevisName,
                    HttpMethod.PUT,
                    new HttpEntity<>(Map.of("docstatus", 1), headers),
                    String.class
            );
        }

        return nouveauDevisName;
    }

}

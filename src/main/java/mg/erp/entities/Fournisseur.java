package mg.erp.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class Fournisseur {
    private String name;
    private String supplier_name;
    private String supplier_type;
    private String supplier_group;
    private String country;
    private String quote_status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public String getSupplier_type() {
        return supplier_type;
    }

    public void setSupplier_type(String supplier_type) {
        this.supplier_type = supplier_type;
    }

    public String getSupplier_group() {
        return supplier_group;
    }

    public void setSupplier_group(String supplier_group) {
        this.supplier_group = supplier_group;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getQuote_status() {
        return quote_status;
    }

    public void setQuote_status(String quote_status) {
        this.quote_status = quote_status;
    }

    private List<Fournisseur> mapToFournisseurs(JsonNode root) {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        JsonNode data = root.path("data");

        if (data.isArray()) {
            for (JsonNode node : data) {
                Fournisseur fournisseur = new Fournisseur();
                fournisseur.setName(node.path("name").asText());
                fournisseur.setSupplier_name(node.path("supplier_name").asText());
                fournisseur.setSupplier_type(node.path("supplier_type").asText());
                fournisseur.setSupplier_group(node.path("supplier_group").asText());
                fournisseur.setCountry(node.path("country").asText());
                fournisseurs.add(fournisseur);
            }
        }
        return fournisseurs;
    }


    public ResponseEntity<String> executeFournisseurRequest(String url, HttpEntity<String> httpEntity) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
    }

    public List<Fournisseur> parseFournisseursFromResponse(String responseBody) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBody);
        return mapToFournisseurs(root);
    }
}

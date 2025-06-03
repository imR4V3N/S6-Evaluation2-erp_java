package mg.erp.entities.rh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class Designation {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Designation> getDesignation(HttpEntity<String> entity, String url) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = mapper.readTree(response.getBody()).path("data");
        List<Designation> designations = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                Designation designation = new Designation();
                designation.setName(node.path("designation_name").asText());

                designations.add(designation);
            }
        }

        return designations;
    }
}

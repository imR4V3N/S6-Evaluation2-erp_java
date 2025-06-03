package mg.erp.entities.rh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class Company {
    String name;
    String abbr;
    String currency;
    String country;
    String default_holiday_list;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDefault_holiday_list() {
        return default_holiday_list;
    }

    public void setDefault_holiday_list(String default_holiday_list) {
        this.default_holiday_list = default_holiday_list;
    }

    public List<Company> getCompanys(HttpEntity<String> entity, String url) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = mapper.readTree(response.getBody()).path("data");
        List<Company> company = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                Company c = new Company();
                c.setName(node.path("company_name").asText());
                c.setAbbr(node.path("abbr").asText());
                c.setCurrency(node.path("default_currency").asText());
                c.setCountry(node.path("country").asText());
                c.setDefault_holiday_list(node.path("default_holiday_list").asText());

                company.add(c);
            }
        }

        return company;
    }
}

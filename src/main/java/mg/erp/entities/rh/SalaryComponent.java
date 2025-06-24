package mg.erp.entities.rh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class SalaryComponent {
    String name;
    String salary_component;
    String salary_component_abbr;
    String type;
    String formula;
    String amount_based_on_formula;
    String depends_on_payment_days;
    String company;
    double amount;

    public SalaryComponent() {}
    public SalaryComponent(String salary_component, double amount, String formula) {
        this.salary_component = salary_component;
        this.formula = formula;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalary_component() {
        return salary_component;
    }

    public void setSalary_component(String salary_component) {
        this.salary_component = salary_component;
    }

    public String getSalary_component_abbr() {
        return salary_component_abbr;
    }

    public void setSalary_component_abbr(String salary_component_abbr) {
        this.salary_component_abbr = salary_component_abbr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }


    public String getAmount_based_on_formula() {
        return amount_based_on_formula;
    }

    public void setAmount_based_on_formula(String amount_based_on_formula) {
        this.amount_based_on_formula = amount_based_on_formula;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepends_on_payment_days() {
        return depends_on_payment_days;
    }

    public void setDepends_on_payment_days(String depends_on_payment_days) {
        this.depends_on_payment_days = depends_on_payment_days;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<SalaryComponent> getSalaryComponents(HttpEntity<String> entity, String url) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = mapper.readTree(response.getBody()).path("data");
        List<SalaryComponent> results = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                SalaryComponent sc = new SalaryComponent();
                sc.setName(node.path("name").asText());
                sc.setSalary_component(node.path("salary_component").asText());
                sc.setSalary_component_abbr(node.path("salary_component_abbr").asText());
                sc.setFormula(node.path("formula").asText());
                sc.setType(node.path("type").asText());
                sc.setAmount_based_on_formula(node.path("amount_based_on_formula").asText());

                results.add(sc);
            }
        }

        return results;
    }
}

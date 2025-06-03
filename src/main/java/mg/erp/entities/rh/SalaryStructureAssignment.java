package mg.erp.entities.rh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class SalaryStructureAssignment {
    String name;
    String employee;
    String employee_name;
    String salary_structure;
    String from_date;
    String company;
    String currency;
    double base;
    double variable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public String getSalary_structure() {
        return salary_structure;
    }

    public void setSalary_structure(String salary_structure) {
        this.salary_structure = salary_structure;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public double getVariable() {
        return variable;
    }

    public void setVariable(double variable) {
        this.variable = variable;
    }

    public List<SalaryStructureAssignment> getSalaryStructureAssignments(HttpEntity<String> entity, String url) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = mapper.readTree(response.getBody()).path("data");
        List<SalaryStructureAssignment> results = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                SalaryStructureAssignment ss = new SalaryStructureAssignment();
                ss.setName(node.path("name").asText());
                ss.setCompany(node.path("company").asText());
                ss.setCurrency(node.path("currency").asText());
                ss.setEmployee(node.path("employee").asText());
                ss.setEmployee_name(node.path("employee_name").asText());
                ss.setBase(node.path("base").asDouble());
                ss.setVariable(node.path("variable").asDouble());
                ss.setFrom_date(node.path("from_date").asText());

                results.add(ss);
            }
        }

        return results;
    }
}

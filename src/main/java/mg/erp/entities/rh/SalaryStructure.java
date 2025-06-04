package mg.erp.entities.rh;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class SalaryStructure {
    String name;
    String company;
    String is_active;
    String currency;
    String payroll_frequency;
    String salary_component;
    double total_earning;
    double total_deduction;
    @JsonIgnore
    List<SalaryComponent> earings = new ArrayList<>();
    @JsonIgnore
    List<SalaryComponent> deductions = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPayroll_frequency() {
        return payroll_frequency;
    }

    public void setPayroll_frequency(String payroll_frequency) {
        this.payroll_frequency = payroll_frequency;
    }

    public String getSalary_component() {
        return salary_component;
    }

    public void setSalary_component(String salary_component) {
        this.salary_component = salary_component;
    }

    public double getTotal_earning() {
        return total_earning;
    }

    public void setTotal_earning(double total_earning) {
        this.total_earning = total_earning;
    }

    public double getTotal_deduction() {
        return total_deduction;
    }

    public void setTotal_deduction(double total_deduction) {
        this.total_deduction = total_deduction;
    }

    public List<SalaryComponent> getEarings() {
        return earings;
    }

    public void setEarings(List<SalaryComponent> earings) {
        this.earings = earings;
    }

    public List<SalaryComponent> getDeductions() {
        return deductions;
    }

    public void setDeductions(List<SalaryComponent> deductions) {
        this.deductions = deductions;
    }

    public List<SalaryStructure> getSalaryStructures(HttpEntity<String> entity, String url) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = mapper.readTree(response.getBody()).path("data");
        List<SalaryStructure> results = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                SalaryStructure ss = new SalaryStructure();
                ss.setName(node.path("name").asText());
                ss.setCompany(node.path("company").asText());
                ss.setCurrency(node.path("currency").asText());
                ss.setIs_active(node.path("is_active").asText());
                ss.setSalary_component(node.path("salary_component").asText());
                ss.setPayroll_frequency(node.path("payroll_frequency").asText());
                ss.setTotal_earning(node.path("total_earning").asDouble());
                ss.setTotal_deduction(node.path("total_deduction").asDouble());

                results.add(ss);
            }
        }

        return results;
    }
}

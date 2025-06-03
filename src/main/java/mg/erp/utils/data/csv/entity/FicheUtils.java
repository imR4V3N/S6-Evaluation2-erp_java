package mg.erp.utils.data.csv.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import mg.erp.entities.rh.*;
import mg.erp.utils.data.csv.utils.DateUtils;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FicheUtils {
    List<SalaryStructureAssignment> salaryStructureAssignments = new ArrayList<>();
    List<FichePaye> salarySlips = new ArrayList<>();

    public List<SalaryStructureAssignment> getSalaryStructureAssignments() {
        return salaryStructureAssignments;
    }

    public void setSalaryStructureAssignments(List<SalaryStructureAssignment> salaryStructureAssignments) {
        this.salaryStructureAssignments = salaryStructureAssignments;
    }

    public List<FichePaye> getSalarySlips() {
        return salarySlips;
    }

    public void setSalarySlips(List<FichePaye> salarySlips) {
        this.salarySlips = salarySlips;
    }

    public String assignementToJson(ResponseEntity<String> responseEntity) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode docsArray = mapper.createArrayNode();

        List<Employee> employees = new Employee().getEmployees(responseEntity.getBody());

        for (SalaryStructureAssignment sal : salaryStructureAssignments) {
            if (sal == null) continue;

            Employee temp = employees.stream()
                    .filter(emp -> emp.getEmployee_name().equalsIgnoreCase(sal.getEmployee_name()))
                    .findFirst()
                    .orElse(null);

            sal.setEmployee(temp.getName());

            ObjectNode empNode = mapper.valueToTree(sal);
            empNode.remove(Arrays.asList("name", "employee_name", "currency", "variable"));

            empNode.put("doctype", "Salary Structure Assignment");
            docsArray.add(empNode);
        }

        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.set("docs", docsArray);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBody);
    }

    public String saveAssignement(String sid, String baseUrl, ResponseEntity<String> responseEntity) throws Exception {
        String jsonBody = assignementToJson(responseEntity);
        HttpResponse<String> response = Unirest.post(baseUrl+"/api/method/frappe.client.insert_many")
                .header("Content-Type", "application/json")
                .header("Cookie", "sid=" + sid)
                .body(jsonBody)
                .asString();

        if (!response.isSuccess()) {
            throw new Exception("Erreur lors de l'insertion des Salary Structure Assignement");
        }

        return response.getBody();
    }

    public List<SalaryStructureAssignment> readAssignement(List<HashMap<String, Object>> data, List<Employee> employees) throws Exception {
        List<SalaryStructureAssignment> result = new ArrayList<>();
        int line = 1;

        for (HashMap<String, Object> map : data) {
            try {
                SalaryStructureAssignment ssa = createSalaryStructureAssignment(map, employees);

                result.add(ssa);
                salaryStructureAssignments.add(ssa);

            } catch (Exception e) {
                throw new Exception(e.getMessage() + " à la ligne " + line , e);
            }
            line++;
        }

        return result;
    }

    private SalaryStructureAssignment createSalaryStructureAssignment(HashMap<String, Object> map, List<Employee> employees) throws Exception {
        String ref = (String) map.get("Ref Employe");
        String employeeName = employees.stream().filter(employee -> employee.getName().equalsIgnoreCase(ref)).map(Employee::getEmployee_name).findFirst().orElse(null);
        if (employeeName != null) {
            SalaryStructureAssignment sc = new SalaryStructureAssignment();
            sc.setFrom_date(DateUtils.formatterDate(((String) map.get("Mois")).trim(), "dd/MM/yyyy"));
            sc.setCompany(employees.get(0).getCompany());
            sc.setBase(Double.parseDouble(((String) map.get("Salaire Base")).trim()));
            sc.setSalary_structure((String) map.get("Salaire"));
            sc.setEmployee_name(employeeName);

            return sc;
        }

        return null;
    }

    public List<FichePaye> readSlip(List<SalaryStructureAssignment> assignments) {
        List<FichePaye> slips = new ArrayList<>();

        for (SalaryStructureAssignment ssa : assignments) {
            if (ssa == null || ssa.getEmployee_name() == null) continue;

            FichePaye slip = new FichePaye();
            slip.setEmployee(ssa.getEmployee());
            slip.setEmployee_name(ssa.getEmployee_name());
            slip.setCompany(ssa.getCompany());
            slip.setSalary_structure(ssa.getSalary_structure());
            LocalDate date = LocalDate.parse(ssa.getFrom_date());
            slip.setStart_date(formatStartDate(date));
            slip.setEnd_date(formatEndDate(date));
            slip.setPayroll_frequency("Monthly");

            slips.add(slip);
            salarySlips.add(slip);
        }

        return slips;
    }

    public String slipToJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode docsArray = mapper.createArrayNode();

        for (FichePaye sal : salarySlips) {
            if (sal == null) continue;

            ObjectNode empNode = mapper.valueToTree(sal);
            empNode.remove(Arrays.asList("name", "designation", "employee_name", "gross_pay", "yearMonth", "net_pay", "departement", "company"));

            empNode.put("doctype", "Salary Structure Assignment");
            docsArray.add(empNode);
        }

        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.set("docs", docsArray);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBody);
    }

    public String saveSlip(String sid, String baseUrl) throws Exception {
        String jsonBody = slipToJson();
        HttpResponse<String> response = Unirest.post(baseUrl+"/api/method/frappe.client.insert_many")
                .header("Content-Type", "application/json")
                .header("Cookie", "sid=" + sid)
                .body(jsonBody)
                .asString();

        if (!response.isSuccess()) {
            throw new Exception("Erreur lors de l'insertion des Salary Slip");
        }

        return response.getBody();
    }

    private String formatStartDate(LocalDate date) {
        return date.withDayOfMonth(1).toString(); // yyyy-MM-01
    }

    private String formatEndDate(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth()).toString(); // yyyy-MM-30 ou 31
    }

}

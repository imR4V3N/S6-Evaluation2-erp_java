package mg.erp.utils.data.csv.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import mg.erp.entities.rh.Employee;
import mg.erp.utils.data.csv.utils.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EmployeUtils {
    private List<Employee> employees = new ArrayList<>();

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public String employeeToJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode docsArray = mapper.createArrayNode();

        for (Employee emp : employees) {
            if (emp == null) continue;

            ObjectNode empNode = mapper.valueToTree(emp);
            empNode.remove(Arrays.asList("name"));

            empNode.put("doctype", "Employee");
            docsArray.add(empNode);
        }

        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.set("docs", docsArray);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBody);
    }

    public String saveEmployee(String sid, String baseUrl) throws Exception {
        String jsonBody = employeeToJson();
        HttpResponse<String> response = Unirest.post(baseUrl+"/api/method/frappe.client.insert_many")
                .header("Content-Type", "application/json")
                .header("Cookie", "sid=" + sid)
                .body(jsonBody)
                .asString();

        if (!response.isSuccess()) {
            throw new Exception("Erreur lors de l'insertion des Employees");
        }

        return response.getBody();
    }

    public List<Employee> read(List<HashMap<String, Object>> data) throws Exception {
        List<Employee> result = new ArrayList<>();
        int line = 1;
        for (HashMap<String, Object> map : data) {
            try {
                Employee employee = createEmployee(map);
                employees.add(employee);
                result.add(employee);
            } catch (Exception e) {
                throw new Exception(e.getMessage() + " a la ligne " + line, e);
            }
            line++;
        }
        return result;
    }

    private Employee createEmployee(HashMap<String, Object> map) throws Exception {
        Employee employee = new Employee();
        employee.setName((String) map.get("Ref"));
        employee.setLast_name((String) map.get("Nom"));
        employee.setFirst_name((String) map.get("Prenom"));
        employee.setGender(createGenre((String) map.get("genre")));
        employee.setDate_of_birth(DateUtils.formatterDate(((String) map.get("date naissance")).trim(), "dd/MM/yyyy"));
        employee.setDate_of_joining(DateUtils.formatterDate(((String) map.get("Date embauche")).trim(), "dd/MM/yyyy"));
        employee.setCompany((String) map.get("company"));
        employee.setStatus("Active");
        return employee;
    }

    private String createGenre(String name) throws Exception {
        if (name.equalsIgnoreCase("Masculin")) {
            return "Male";
        } else if (name.equalsIgnoreCase("Feminin")) {
            return "Female";
        }
        return "Other";
    }
}

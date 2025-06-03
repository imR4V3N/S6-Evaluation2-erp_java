package mg.erp.utils.data.csv.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import mg.erp.entities.rh.SalaryComponent;
import mg.erp.entities.rh.SalaryStructure;
import mg.erp.utils.toolkit.io.Utils;

import java.util.*;

public class SalaryUtils {
    List<SalaryStructure> salaryStructures = new ArrayList<>();
    List<SalaryComponent> salaryComponents = new ArrayList<>();

    public List<SalaryStructure> getSalaryStructures() {
        return salaryStructures;
    }

    public void setSalaryStructures(List<SalaryStructure> salaryStructures) {
        this.salaryStructures = salaryStructures;
    }

    public List<SalaryComponent> getSalaryComponents() {
        return salaryComponents;
    }

    public void setSalaryComponents(List<SalaryComponent> salaryComponents) {
        this.salaryComponents = salaryComponents;
    }

    public String componentToJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode docsArray = mapper.createArrayNode();

        for (SalaryComponent component : salaryComponents) {
            ObjectNode empNode = mapper.valueToTree(component);
            empNode.remove(Arrays.asList("name"));
            empNode.put("doctype", "Salary Component");

            // Ajout du champ "accounts"
            ArrayNode accountsArray = mapper.createArrayNode();
            ObjectNode accountNode = mapper.createObjectNode();
            accountNode.put("account", "Cash - MC");
            accountNode.put("company", "My Company");
            accountsArray.add(accountNode);

            empNode.set("accounts", accountsArray);
            docsArray.add(empNode);
        }

        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.set("docs", docsArray);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBody);
    }

    public String structureToJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, ObjectNode> structureMap = new LinkedHashMap<>();

        for (SalaryStructure structure : salaryStructures) {
            if (structure == null) continue;
            String name = structure.getName();
            if (!structureMap.containsKey(name)) {
                ObjectNode salaryStructureJson = mapper.valueToTree(structure);
                salaryStructureJson.remove(Arrays.asList("salary_component"));
                salaryStructureJson.put("doctype", "Salary Structure");
                salaryStructureJson.set("earnings", mapper.createArrayNode());
                salaryStructureJson.set("deductions", mapper.createArrayNode());

                structureMap.put(name, salaryStructureJson);
            }

            ObjectNode existingStructure = structureMap.get(name);
            ArrayNode earningsArray = (ArrayNode) existingStructure.get("earnings");
            for (SalaryComponent e : structure.getEarings()) {
                if (e == null) continue;
                ObjectNode compNode = mapper.valueToTree(e);
                compNode.remove(Arrays.asList("name"));
                compNode.put("doctype", "Salary Detail");
                compNode.put("depends_on_payment_days", "0");
                earningsArray.add(compNode);
            }

            ArrayNode deductionArray = (ArrayNode) existingStructure.get("deductions");
            for (SalaryComponent d : structure.getDeductions()) {
                if (d == null) continue;
                ObjectNode compNode = mapper.valueToTree(d);
                compNode.remove(Arrays.asList("name"));
                compNode.put("doctype", "Salary Detail");
                compNode.put("depends_on_payment_days", "0");
                deductionArray.add(compNode);
            }
        }

        ArrayNode docsArray = mapper.createArrayNode();
        for (ObjectNode structureNode : structureMap.values()) {
            docsArray.add(structureNode);
        }

        ObjectNode body = mapper.createObjectNode();
        body.set("docs", docsArray);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
    }



    public List<SalaryStructure> read(List<HashMap<String, Object>> data) throws Exception {
        List<SalaryStructure> result = new ArrayList<>();
        int line = 1;

        for (HashMap<String, Object> map : data) {
            try {
                // Création du SalaryComponent
                SalaryComponent sc = createSalaryComponent(map);
                SalaryStructure structure = createSalaryStructure(map, sc);

                result.add(structure);
                salaryComponents.add(sc);
                salaryStructures.add(structure);

            } catch (Exception e) {
                throw new Exception("Erreur à la ligne " + line + ": " + e.getMessage(), e);
            }
            line++;
        }

        return result;
    }

    public String saveComponent(String sid, String baseUrl) throws Exception {
        String jsonBody = componentToJson();
        System.out.println("COMPONENT : \n" + jsonBody);
        HttpResponse<String> response = Unirest.post(baseUrl+"/api/method/frappe.client.insert_many")
                .header("Content-Type", "application/json")
                .header("Cookie", "sid=" + sid)
                .body(jsonBody)
                .asString();
        if (!response.isSuccess()) {
            throw new Exception("Erreur lors de l'insertion des Salary Component");
        }

        return response.getBody();
    }

    public String saveStructure(String sid, String baseUrl) throws Exception {
        String jsonBody = structureToJson();
        System.out.println("STRUCTURE : \n" + jsonBody);
        HttpResponse<String> response = Unirest.post(baseUrl+"/api/method/frappe.client.insert_many")
                .header("Content-Type", "application/json")
                .header("Cookie", "sid=" + sid)
                .body(jsonBody)
                .asString();

        if (!response.isSuccess()) {
            throw new Exception("Erreur lors de l'insertion des Salary Structure");
        }

        return response.getBody();
    }


    private SalaryComponent createSalaryComponent(HashMap<String, Object> map) throws Exception {
        SalaryComponent sc = new SalaryComponent();
        sc.setSalary_component((String) map.get("name"));
        sc.setSalary_component_abbr((String) map.get("Abbr"));
        sc.setType(Utils.toUpperCase((String) map.get("type")));
        sc.setAmount_based_on_formula("1");
        sc.setDepends_on_payment_days("0");
        sc.setFormula((String) map.get("valeur"));
        sc.setCompany("My Company");

        return sc;
    }

    private SalaryStructure createSalaryStructure(HashMap<String, Object> map, SalaryComponent sc) throws Exception {
        Map<String, SalaryStructure> salaryStructureMap = new LinkedHashMap<>();
        // Clé = nom de la salary structure
        String structureName = (String) map.get("salary structure");
        String company = (String) map.get("company");

        // Créer ou récupérer la salary structure correspondante
        SalaryStructure structure = salaryStructureMap.computeIfAbsent(structureName, name -> {
            SalaryStructure s = new SalaryStructure();
            s.setName(name);
            s.setCompany(company);
            s.setIs_active("Yes");
            s.setCurrency("USD");
            s.setPayroll_frequency("Monthly");
            return s;
        });

        // Ajouter le component au bon type (earning ou deduction)
        String type = ((String) map.get("type")).toLowerCase();
        if (type.equals("earning")) {
            structure.getEarings().add(sc);
        } else if (type.equals("deduction")) {
            structure.getDeductions().add(sc);
        }

        return structure;
    }

}

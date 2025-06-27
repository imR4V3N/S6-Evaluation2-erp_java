package mg.erp.services.salary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpSession;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import mg.erp.entities.Auth;
import mg.erp.entities.rh.Employee;
import mg.erp.entities.rh.FichePaye;
import mg.erp.entities.rh.SalaryComponent;
import mg.erp.entities.rh.SalarySummary;
import mg.erp.utils.Config;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SalaryService {
    public SalarySummary getDernierSalarySummaryAvant(Map<YearMonth, List<SalarySummary>> parEmploye, String nameEmp, YearMonth debut) {
        SalarySummary dernier = null;
        YearMonth dernierMois = null;

        for (Map.Entry<YearMonth, List<SalarySummary>> entry : parEmploye.entrySet()) {
            YearMonth mois = entry.getKey();
            if (mois.isBefore(debut)) {
                for (SalarySummary summary : entry.getValue()) {

                    if (summary.getIdEmployee().equalsIgnoreCase(nameEmp)) {
                        // On garde le plus récent
                        if (dernierMois == null || mois.isAfter(dernierMois)) {
                            dernier = summary;
                            dernierMois = mois;
                        }
                    }
                }
            }
        }

        return dernier;
    }

    public Double getDernierSalaireBaseAvant(SalarySummary salaire) {
        List<SalaryComponent> result = new ArrayList<>();
        result.addAll(salaire.getComponentEarnings());
        result.addAll(salaire.getComponentDeductions());
        for (SalaryComponent component : result) {
            if (component.getFormula().equalsIgnoreCase("base")) {
                return component.getAmount();
            }
        }
        return null;
    }

    public List<YearMonth> genererMoisManquantsPourEmploye(YearMonth debut, YearMonth fin, String nameEmp, Map<YearMonth, List<SalarySummary>> parEmploye) {
        List<YearMonth> moisManquants = new ArrayList<>();
        YearMonth courant = debut;

        while (!courant.isAfter(fin)) {

            List<SalarySummary> summaries = parEmploye.getOrDefault(courant, Collections.emptyList());

            boolean dejaPresent = summaries.stream()
                    .anyMatch(s -> s.getIdEmployee().equalsIgnoreCase(nameEmp));

            if (!dejaPresent) {
                moisManquants.add(courant);
            }

            courant = courant.plusMonths(1);
        }

        return moisManquants;
    }

    public List<YearMonth> genererMois(YearMonth debut, YearMonth fin) {
        List<YearMonth> moisManquants = new ArrayList<>();
        YearMonth courant = debut;

        while (!courant.isAfter(fin)) {
            moisManquants.add(courant);
            courant = courant.plusMonths(1);
        }

        return moisManquants;
    }

    // SAVE Salary -------------------------------------------------------------------------------

    public ObjectNode createSalaryStructureAssignmentJson(Employee emp, String salaryStructure, YearMonth date, double baseSalary) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("doctype", "Salary Structure Assignment");
        node.put("employee", emp.getName());
        node.put("employee_name", emp.getEmployee_name());
        node.put("company", emp.getCompany());
        node.put("salary_structure", salaryStructure);
        node.put("from_date", date + "-01");
        node.put("base", baseSalary);
        node.put("docstatus", 1);

    //        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        return node;
    }

    public ObjectNode createSalarySlipJsonFromStructure(Employee emp, YearMonth date, String salaryStructure) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("doctype", "Salary Slip");
        node.put("employee", emp.getName());
        node.put("employee_name", emp.getEmployee_name());
        node.put("company", emp.getCompany());
        node.put("payroll_frequency", "Monthly");
        node.put("salary_structure", salaryStructure);
        node.put("start_date", date + "-01");
        node.put("posting_date", date + "-01");
        LocalDate endOfMonth = date.atEndOfMonth();
        node.put("end_date", endOfMonth.toString());
        node.put("docstatus", 1); // 0 pour brouillon, 1 pour soumis

//        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        return node;
    }

    public String toJson(List<ObjectNode> nodes) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        for (ObjectNode node : nodes) {
            arrayNode.add(node);
        }
        ObjectNode body = mapper.createObjectNode();
        body.set("docs", arrayNode);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
    }

    public String save(String sid, String baseUrl, String...json) throws Exception{
        if (sid == null || sid.equals("Guest")) {
            throw new Exception("Erreur de session : utilisateur non authentifié");
        }

        HttpResponse<String> response = Unirest.post(baseUrl + "/api/method/hrms.data.insert_controller.create_salary_documents")
                .header("Content-Type", "application/json")
                .header("Cookie", "sid=" + sid)
                .body("{\"assignment_json\": " + json[0] + ", \"slip_json\": " + json[1] + "}")
                .asString();

        if (!response.isSuccess()) {
            Pattern p = Pattern.compile("Salaire deja existante pour les mois demandees.");
            Matcher m = p.matcher(response.getBody());
            if (m.find()) {
                throw new Exception("Erreur ERPNext : " + m.group());
            }
            throw new Exception("Erreur ERPNext : " + response.getBody());
        }

        Pattern p = Pattern.compile("Les documents ont ete inseres avec succes.");
        Matcher m = p.matcher(response.getBody());
        String message = response.getBody();
        if (m.find()) {
            message = m.group();
        }
        return message;
    }

    public String generationDeSalaire(Employee emp, String salaryStructureName, List<YearMonth> moisGenerer, double value, String baseUrl, String sid) throws Exception {
        List<ObjectNode> assNodes = new ArrayList<>();
        List<ObjectNode> slipNodes = new ArrayList<>();

        for (YearMonth moisGenere : moisGenerer) {
            ObjectNode assNode = this.createSalaryStructureAssignmentJson(emp, salaryStructureName, moisGenere, value);
            assNodes.add(assNode);
            ObjectNode slipNode = this.createSalarySlipJsonFromStructure(emp, moisGenere, salaryStructureName);
            slipNodes.add(slipNode);
        }

        String assJson = this.toJson(assNodes);
        String slipJson = this.toJson(slipNodes);

        return this.save(sid, baseUrl, assJson, slipJson);
    }

    public String annulerSalaire(List<FichePaye> summaries, HttpSession session, ConfigurableEnvironment configEnv) throws Exception {
        Auth user = (Auth) session.getAttribute("user");
        if (user.getSid() == null) return "redirect:/";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", "sid=" + user.getSid());

        String erpUrl = new Config().getErpUrl(configEnv);

        for (FichePaye summary : summaries) {
            String slipName = summary.getName();

            // 1. Récupérer le Salary Slip
            ResponseEntity<JsonNode> slipResp = restTemplate.exchange(
                    erpUrl + "/api/resource/Salary Slip/" + slipName,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );

            JsonNode slipData = slipResp.getBody().path("data");
            String employee = slipData.path("employee").asText();
            String salaryStructure = slipData.path("salary_structure").asText();
            String startDate = slipData.path("start_date").asText();
            int docstatus = slipData.path("docstatus").asInt();

            // 2. Trouver le Salary Structure Assignment correspondant
            ResponseEntity<JsonNode> assResp = restTemplate.exchange(
                    erpUrl + "/api/resource/Salary Structure Assignment?fields=[\"name\", \"from_date\"]&filters=[[\"employee\",\"=\",\"" + employee + "\"],[\"salary_structure\",\"=\",\"" + salaryStructure + "\"],[\"docstatus\",\"!=\",2]]",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );
            JsonNode assList = assResp.getBody().path("data");
            JsonNode correctAssignment = null;
            for (JsonNode a : assList) {
                if (startDate.equals(a.path("from_date").asText())) {
                    correctAssignment = a;
                    break;
                }
            }
            if (correctAssignment == null)
                throw new Exception("Aucun Salary Structure Assignment trouvé pour " + slipName);

            String assignmentName = correctAssignment.path("name").asText();

            // 3. Annuler ou supprimer l'ancien Salary Slip
            if (docstatus == 1) {
                // Annuler si soumis
                restTemplate.exchange(
                        erpUrl + "/api/resource/Salary Slip/" + slipName,
                        HttpMethod.PUT,
                        new HttpEntity<>(Map.of("docstatus", 2), headers),
                        String.class
                );
            } else if (docstatus == 0) {
                // Supprimer si draft
                restTemplate.exchange(
                        erpUrl + "/api/resource/Salary Slip/" + slipName,
                        HttpMethod.DELETE,
                        new HttpEntity<>(headers),
                        String.class
                );
            }

            // 4. Annuler l'ancien Assignment
            restTemplate.exchange(
                    erpUrl + "/api/resource/Salary Structure Assignment/" + assignmentName,
                    HttpMethod.PUT,
                    new HttpEntity<>(Map.of("docstatus", 2), headers),
                    String.class
            );
        }

        System.out.println("Annulation des salaires terminée avec succès.");

        return "Annulation des salaires terminée avec succès.";
    }


    //    UPDATE Salary *---------------------------------------------------------------------------------------
    public List<SalaryComponent> mergeComponent(List<SalaryComponent> componentEarnings, List<SalaryComponent> componentDeductions) {
        List<SalaryComponent> allComponents = new ArrayList<>();
        allComponents.addAll(componentEarnings);
        allComponents.addAll(componentDeductions);

        return allComponents;
    }

    public void remplacer(List<SalaryComponent> earnings, SalaryComponent nouveau, String label) {
        for (int i = 0; i < earnings.size(); i++) {
            SalaryComponent current = earnings.get(i);
            if (label.equals(current.getSalary_component())) {
                earnings.set(i, nouveau);
                break; // On sort après remplacement
            }
        }
    }

    public SalarySummary setSalaireBase(SalarySummary salarySummary , String addition, double pourcentage) {
        List<SalaryComponent> earnings = salarySummary.getComponentEarnings();

        if (addition.equalsIgnoreCase("+")) {
            String label = "Salaire Base";
            Double salaireBase = 0.0;
            for (SalaryComponent component : earnings) {
                if (component.getFormula().equalsIgnoreCase("base")) {
                    salaireBase = component.getAmount();
                    label = component.getSalary_component();
                    break;
                }
            }
            Double resultat = salaireBase + (pourcentage/100 * salaireBase);
            SalaryComponent nouveau = new SalaryComponent();
            nouveau.setSalary_component(label);
            nouveau.setAmount(resultat);
            nouveau.setFormula("base");
            this.remplacer(earnings, nouveau, label);
        } else if (addition.equalsIgnoreCase("-")) {
            String label = "Salaire Base";
            Double salaireBase = 0.0;
            for (SalaryComponent component : earnings) {
                if (component.getFormula().equalsIgnoreCase("base")) {
                    salaireBase = component.getAmount();
                    label = component.getSalary_component();
                    break;
                }
            }
            Double resultat = salaireBase - (pourcentage/100 * salaireBase);
            SalaryComponent nouveau = new SalaryComponent();
            nouveau.setSalary_component(label);
            nouveau.setAmount(resultat);
            nouveau.setFormula("base");
            this.remplacer(earnings, nouveau, label);
        }

        salarySummary.setComponentEarnings(earnings);
        return salarySummary;
    }

    public Double getOrDefault(List<SalaryComponent> allComponents, String element) {
        for (SalaryComponent component : allComponents) {
            if (component.getSalary_component().equalsIgnoreCase(element)) {
                return component.getAmount();
            }
        }
        return 0.0; // Si l'élément n'est pas trouvé, retourne null
    }

    public List<SalarySummary> getSalarySummaryCondition(Map<YearMonth, List<SalarySummary>> parEmploye,
                                                         String element, String comparaison, double montant,
                                                         String addition, double pourcentage) {
        List<SalarySummary> results = new ArrayList<>();

        for (Map.Entry<YearMonth, List<SalarySummary>> entry : parEmploye.entrySet()) {
            for (SalarySummary summary : entry.getValue()) {
                List<SalaryComponent> allComponents = this.mergeComponent(summary.getComponentEarnings(), summary.getComponentDeductions());
                Double value = this.getOrDefault(allComponents,element);

                if (value != null) {
                    if (comparaison.equalsIgnoreCase("<")){
                        if (value <= montant) {
                            summary = this.setSalaireBase(summary, addition, pourcentage);
                            results.add(summary);
                        }
                    }
                    if (comparaison.equalsIgnoreCase(">")){
                        if (value >= montant) {
                            summary = this.setSalaireBase(summary, addition, pourcentage);
                            results.add(summary);
                        }
                    }
                }
            }
        }

        return results;
    }

    public List<SalarySummary> getSalarySummaryCondition2(Map<YearMonth, List<SalarySummary>> parEmploye,
                                                         String element, String comparaison, double montant) {
        List<SalarySummary> results = new ArrayList<>();

        for (Map.Entry<YearMonth, List<SalarySummary>> entry : parEmploye.entrySet()) {
            for (SalarySummary summary : entry.getValue()) {
                List<SalaryComponent> allComponents = this.mergeComponent(summary.getComponentEarnings(), summary.getComponentDeductions());
                Double value = this.getOrDefault(allComponents,element);

                if (value != null) {
                    if (comparaison.equalsIgnoreCase("<")){
                        if (value < montant) {
                            results.add(summary);
                        }
                    }
                    if (comparaison.equalsIgnoreCase(">")){
                        if (value > montant) {
                            results.add(summary);
                        }
                    }
                    if (comparaison.equalsIgnoreCase("<=")){
                        if (value <= montant) {
                            results.add(summary);
                        }
                    }
                    if (comparaison.equalsIgnoreCase(">=")){
                        if (value >= montant) {
                            results.add(summary);
                        }
                    }
                }
            }
        }

        return results;
    }

    public String modifierBaseSalaire(List<SalarySummary> summaries, HttpSession session, ConfigurableEnvironment configEnv) throws Exception {
        Auth user = (Auth) session.getAttribute("user");
        if (user.getSid() == null) return "redirect:/";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", "sid=" + user.getSid());

        String erpUrl = new Config().getErpUrl(configEnv);

        for (SalarySummary summary : summaries) {
            String slipName = summary.getSalarySlip();
            Double newBase = this.getDernierSalaireBaseAvant(summary);
            if (newBase == null) {
                throw new Exception("Aucun salaire de base trouvé pour " + summary.getIdEmployee() + " dans le résumé de salaire.");
            }

            // 1. Récupérer le Salary Slip
            ResponseEntity<JsonNode> slipResp = restTemplate.exchange(
                    erpUrl + "/api/resource/Salary Slip/" + slipName,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );
            JsonNode slipData = slipResp.getBody().path("data");
            String employee = slipData.path("employee").asText();
            String company = slipData.path("company").asText();
            String salaryStructure = slipData.path("salary_structure").asText();
            String startDate = slipData.path("start_date").asText();
            String endDate = slipData.path("end_date").asText();
            int docstatus = slipData.path("docstatus").asInt();

            // 2. Trouver le Salary Structure Assignment correspondant
            ResponseEntity<JsonNode> assResp = restTemplate.exchange(
                    erpUrl + "/api/resource/Salary Structure Assignment?fields=[\"name\", \"from_date\"]&filters=[[\"employee\",\"=\",\"" + employee + "\"],[\"salary_structure\",\"=\",\"" + salaryStructure + "\"],[\"docstatus\",\"!=\",2]]",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );
            JsonNode assList = assResp.getBody().path("data");
            JsonNode correctAssignment = null;
            for (JsonNode a : assList) {
                if (startDate.equals(a.path("from_date").asText())) {
                    correctAssignment = a;
                    break;
                }
            }
            if (correctAssignment == null)
                throw new Exception("Aucun Salary Structure Assignment trouvé pour " + slipName);

            String assignmentName = correctAssignment.path("name").asText();

            // 3. Annuler ou supprimer l'ancien Salary Slip
            if (docstatus == 1) {
                // Annuler si soumis
                restTemplate.exchange(
                        erpUrl + "/api/resource/Salary Slip/" + slipName,
                        HttpMethod.PUT,
                        new HttpEntity<>(Map.of("docstatus", 2), headers),
                        String.class
                );
            } else if (docstatus == 0) {
                // Supprimer si draft
                restTemplate.exchange(
                        erpUrl + "/api/resource/Salary Slip/" + slipName,
                        HttpMethod.DELETE,
                        new HttpEntity<>(headers),
                        String.class
                );
            }

            // 4. Annuler l'ancien Assignment
            restTemplate.exchange(
                    erpUrl + "/api/resource/Salary Structure Assignment/" + assignmentName,
                    HttpMethod.PUT,
                    new HttpEntity<>(Map.of("docstatus", 2), headers),
                    String.class
            );

            // 5. Créer une version amendée du Salary Structure Assignment
            Map<String, Object> amendAss = Map.of(
                    "doctype", "Salary Structure Assignment",
                    "amended_from", assignmentName,
                    "employee", employee,
                    "company", company,
                    "salary_structure", salaryStructure,
                    "from_date", startDate,
                    "base", newBase
            );
            ResponseEntity<JsonNode> newAssResp = restTemplate.postForEntity(
                    erpUrl + "/api/resource/Salary Structure Assignment",
                    new HttpEntity<>(amendAss, headers),
                    JsonNode.class
            );
            String newAssName = newAssResp.getBody().path("data").path("name").asText();

            // 6. Soumettre le nouvel Assignment
            restTemplate.exchange(
                    erpUrl + "/api/resource/Salary Structure Assignment/" + newAssName,
                    HttpMethod.PUT,
                    new HttpEntity<>(Map.of("docstatus", 1), headers),
                    String.class
            );

            // 7. Recréer le Salary Slip
            Map<String, Object> newSlip = Map.of(
                    "doctype", "Salary Slip",
                    "employee", employee,
                    "company", company,
                    "salary_structure", salaryStructure,
                    "payroll_frequency", "Monthly",
                    "start_date", startDate,
                    "from_date", startDate,
                    "end_date", endDate
            );
            ResponseEntity<JsonNode> newSlipResp = restTemplate.postForEntity(
                    erpUrl + "/api/resource/Salary Slip",
                    new HttpEntity<>(newSlip, headers),
                    JsonNode.class
            );
            String newSlipName = newSlipResp.getBody().path("data").path("name").asText();

            // 8. Soumettre le nouveau Salary Slip
            restTemplate.exchange(
                    erpUrl + "/api/resource/Salary Slip/" + newSlipName,
                    HttpMethod.PUT,
                    new HttpEntity<>(Map.of("docstatus", 1), headers),
                    String.class
            );
        }

        return "Modification de salaire de base terminée avec succès.";
    }

}

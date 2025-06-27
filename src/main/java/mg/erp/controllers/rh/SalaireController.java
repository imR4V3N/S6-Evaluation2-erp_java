package mg.erp.controllers.rh;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.erp.entities.Auth;
import mg.erp.entities.rh.*;
import mg.erp.services.salary.SalaryService;
import mg.erp.utils.Config;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("/salaire")
@Controller
public class SalaireController {
    private final ConfigurableEnvironment configurableEnvironment;
    private final FichePaye fichePaye = new FichePaye();
    private final SalarySummary salarySummary = new SalarySummary();
    private final Gson gson = new Gson();
    private final Employee employee = new Employee();
    private final SalaryService salaryService = new SalaryService();
    private final SalaryComponent salaryComponent = new SalaryComponent();
    private final SalaryStructureAssignment assignment = new SalaryStructureAssignment();

    public SalaireController(ConfigurableEnvironment confixgurableEnvironment) {
        this.configurableEnvironment = confixgurableEnvironment;
    }

    private HttpEntity<String> buildHttpEntityWithSid(String sid) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        return new HttpEntity<>(headers);
    }

    @GetMapping("/statistique")
    public String statistique(HttpSession session, HttpServletRequest request, @RequestParam("annee") String annee) {
        Auth user = (Auth) session.getAttribute("user");
        String url = new Config().getErpUrl(configurableEnvironment) + "/api/resource/Salary Slip?filters=[[\"docstatus\",\"!=\",\"2\"]]&fields=[\"*\"]&limit_page_length=2000000";
        HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());

        try {
            List<FichePaye> fichePayes = salarySummary.fetchFichePayes(url, entity, fichePaye, configurableEnvironment);
            Map<YearMonth, SalarySummary> parAnnee = salarySummary.regrouperFichesParMoisAnnee(fichePayes, Integer.parseInt(annee));
            List<SalarySummary> summaries = new ArrayList<>(parAnnee.values());

            request.setAttribute("summaries", summaries);
            request.setAttribute("annee", annee);

            salarySummary.initializeStatistics(request, summaries, annee, gson);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "rh/salaire/salaireTotal";
    }

    @GetMapping("/data")
    public String dataGenererPage(HttpSession session, HttpServletRequest request) {
        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        String empUrl = new Config().getErpUrl(configurableEnvironment) + "/api/resource/Employee?fields=[\"*\"]&limit_page_length=2000000";

        HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(empUrl, HttpMethod.GET, entity, String.class);

            List<Employee> allEmp = employee.getEmployees(response.getBody());

            request.setAttribute("employees", allEmp);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }

        return "rh/salaire/generer";
    }

    @GetMapping("/modif")
    public String modifPage(HttpSession session, HttpServletRequest request) {
        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        String url = new Config().getErpUrl(configurableEnvironment) + "/api/resource/Salary Component?fields=[\"*\"]&limit_page_length=2000000";

        HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());
        try {

            List<SalaryComponent> salaryComponents = salaryComponent.getSalaryComponents(entity, url);

            request.setAttribute("elements", salaryComponents);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }

        return "rh/salaire/modification";
    }

    @PostMapping("/generer")
    public String generer(HttpSession session, HttpServletRequest request, RedirectAttributes redirectAttributes,
                          @RequestParam("employee") String nameEmp, @RequestParam("debut") YearMonth debut, @RequestParam("fin") YearMonth fin, @RequestParam("montant") double montant,
                          @RequestParam("ecraser") String ecraser, @RequestParam("moyenne") String moyenne) {
        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        String baseUrl = new Config().getErpUrl(configurableEnvironment);

        String url = baseUrl + "/api/resource/Salary Slip?filters=[[\"docstatus\",\"!=\",\"2\"]]&fields=[\"*\"]&limit_page_length=2000000";
        String assUrl = baseUrl + "/api/resource/Salary Structure Assignment?filters=[[\"docstatus\",\"!=\",\"2\"]]&fields=[\"*\"]&limit_page_length=2000000";
        String empUrl = baseUrl + "/api/resource/Employee?fields=[\"*\"]&limit_page_length=2000000";

        HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            RestTemplate restTemplate2 = new RestTemplate();
            ResponseEntity<String> response2 = restTemplate2.exchange(empUrl, HttpMethod.GET, entity, String.class);

            List<Employee> allEmp = employee.getEmployees(response2.getBody());
            Employee emp = allEmp.stream().filter(e -> e.getName().equalsIgnoreCase(nameEmp)).findFirst().orElse(null);

            List<FichePaye> fichePayes = fichePaye.getFiches(response.getBody(), baseUrl, entity);
            Map<YearMonth, List<SalarySummary>> parEmploye = salarySummary.regrouperFichesParMoisEtParEmploye2(fichePayes);
            SalarySummary dernier = salaryService.getDernierSalarySummaryAvant(parEmploye, nameEmp, debut);
            List<YearMonth> moisGenerer = new ArrayList<>();
            List<FichePaye> salaireEcraser = new ArrayList<>();

            // Ecraser 0 = Oui ------- 1 = Non
            if (ecraser.equalsIgnoreCase("1")) {
                moisGenerer = salaryService.genererMoisManquantsPourEmploye(debut, fin, nameEmp, parEmploye);
            } else {
                moisGenerer = salaryService.genererMois(debut, fin);
                salaireEcraser = fichePaye.getByDateAndEmp(nameEmp, fichePayes, moisGenerer);
                salaryService.annulerSalaire(salaireEcraser, session, configurableEnvironment);
            }
            String salaryStructureName = "";
            if (fichePayes != null && fichePayes.size() > 0) {
                salaryStructureName = fichePayes.get(0).getSalary_structure().getName();
            }

            List<SalaryStructureAssignment> salaryStructureAssignments = assignment.getSalaryStructureAssignments(entity, assUrl);
            double sommeMoyenne = assignment.moyenne(salaryStructureAssignments);

            double value = 0.0;
//          Moyenne 0 = Oui ------- 1 = Non
            if (moyenne.equalsIgnoreCase("0")) {
                value = sommeMoyenne;
                System.out.println("VALUE : " + sommeMoyenne);
            } else{
//              Montant == 0 = Dernier salaire de base ---------- =! 0 = Montant
                if (montant == 0) {
                    if (dernier != null) {
                        Double dernierSalaireBase = salaryService.getDernierSalaireBaseAvant(dernier);
                        if (dernierSalaireBase != null) {
                            value = dernierSalaireBase;
                        } else {
                            throw new Exception("Aucun salaire de base trouvé pour l'employé " + nameEmp + " avant le mois " + debut);
                        }
                    } else {
                        throw new Exception("Aucun salaire trouvé pour l'employé " + nameEmp + " avant le mois " + debut);
                    }
                } else {
                    value = montant;
                }
            }


            String res = salaryService.generationDeSalaire(emp, salaryStructureName, moisGenerer, value, baseUrl, user.getSid());
            redirectAttributes.addFlashAttribute("success", res);

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/salaire/data";
        }

        return "redirect:/salaire/data";
    }

    @PostMapping("/modifier")
    public String modifier(HttpSession session, RedirectAttributes redirectAttributes,
                           @RequestParam("element") String element, @RequestParam("comparaison") String comparaison, @RequestParam("montant") double montant,
                           @RequestParam("addition") String addition, @RequestParam("pourcentage") double pourcentage) {

        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        String baseUrl = new Config().getErpUrl(configurableEnvironment);

        String url = baseUrl + "/api/resource/Salary Slip?filters=[[\"docstatus\",\"!=\",\"2\"]]&fields=[\"*\"]&limit_page_length=2000000";

        HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            List<FichePaye> fichePayes = fichePaye.getFiches(response.getBody(), baseUrl, entity);
            Map<YearMonth, List<SalarySummary>> parEmploye = salarySummary.regrouperFichesParMoisEtParEmploye2(fichePayes);
            List<SalarySummary> summaries = salaryService.getSalarySummaryCondition(parEmploye, element, comparaison, montant, addition, pourcentage);

            String res = salaryService.modifierBaseSalaire(summaries, session, configurableEnvironment);
            redirectAttributes.addFlashAttribute("success", res);

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/salaire/modif";
        }

        return "redirect:/salaire/modif";
    }

    @GetMapping("/element")
    public String element(HttpSession session, HttpServletRequest request,
                          @RequestParam("element") String element, @RequestParam("comparaison") String comparaison, @RequestParam("montant") double montant) {
        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        String url = new Config().getErpUrl(configurableEnvironment) + "/api/resource/Salary Component?fields=[\"*\"]&limit_page_length=2000000";
        String urlSlip = new Config().getErpUrl(configurableEnvironment) + "/api/resource/Salary Slip?filters=[[\"docstatus\",\"!=\",\"2\"]]&fields=[\"*\"]&limit_page_length=2000000";
        HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(urlSlip, HttpMethod.GET, entity, String.class);

            List<SalaryComponent> salaryComponents = salaryComponent.getSalaryComponents(entity, url);
            List<FichePaye> fichePayes = fichePaye.getFiches(response.getBody(), new Config().getErpUrl(configurableEnvironment), entity);
            Map<YearMonth, List<SalarySummary>> parEmploye = salarySummary.regrouperFichesParMoisEtParEmploye2(fichePayes);
            List<SalarySummary> summaries = parEmploye.get(YearMonth.now());
            if (!element.equalsIgnoreCase("null") && !comparaison.equalsIgnoreCase("null") && montant != -1) {
                summaries = salaryService.getSalarySummaryCondition2(parEmploye, element, comparaison, montant);
            }

            request.setAttribute("elements", salaryComponents);
            request.setAttribute("summaries", summaries);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }

        return "rh/employee/salaireParElement";
    }

}

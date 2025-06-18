package mg.erp.controllers.rh;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mg.erp.entities.Auth;
import mg.erp.entities.rh.*;
import mg.erp.utils.Config;
import mg.erp.utils.YearMonthAdapter;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RequestMapping("/employee")
@Controller
public class EmployeeController {
    private final ConfigurableEnvironment configurableEnvironment;
    private final Employee employee = new Employee();
    private final Designation designation = new Designation();
    private final Genre genre = new Genre();
    private final FichePaye fichePaye = new FichePaye();
    private final SalarySummary salarySummary = new SalarySummary();

    public EmployeeController(ConfigurableEnvironment configurableEnvironment) {
        this.configurableEnvironment = configurableEnvironment;
    }

    private HttpEntity<String> buildHttpEntityWithSid(String sid) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        return new HttpEntity<>(headers);
    }

    @GetMapping
    public String employee(HttpSession session, HttpServletRequest request) {
        String nom = request.getParameter("nom");
        String ageMinStr = request.getParameter("ageMin");
        String ageMaxStr = request.getParameter("ageMax");
        String gender = request.getParameter("genre");
        String poste = request.getParameter("poste");

        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        String empUrl = new Config().getErpUrl(configurableEnvironment) + "/api/resource/Employee?fields=[\"*\"]&limit_page_length=2000000";
        String desUrl = new Config().getErpUrl(configurableEnvironment) + "/api/resource/Designation?fields=[\"designation_name\"]&limit_page_length=2000000";
        String genreUrl = new Config().getErpUrl(configurableEnvironment) + "/api/resource/Gender?fields=[\"gender\"]&limit_page_length=2000000";

        HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(empUrl, HttpMethod.GET, entity, String.class);

            List<Designation> designations = designation.getDesignation(entity, desUrl);
            List<Genre> genres = genre.getGenres(entity, genreUrl);
            List<Employee> allEmp = employee.getEmployees(response.getBody());
            List<Employee> employees = employee.filtre(allEmp, ageMinStr, ageMaxStr, nom, gender, poste);

            request.setAttribute("genres", genres);
            request.setAttribute("designations", designations);
            request.setAttribute("employees", employees);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }

        return "rh/employee/list";
    }

    @GetMapping("/fiche-paye")
    public String fichePaye(HttpSession session, HttpServletRequest request, @RequestParam("idEmp") String idEmp, @RequestParam("mois") YearMonth mois) {
        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        String baseUrl = new Config().getErpUrl(configurableEnvironment);
        String url = baseUrl + "/api/resource/Salary Slip?filters=[[\"employee\",\"=\",\""+idEmp+"\"], [\"docstatus\",\"!=\",\"2\"]]&fields=[\"*\"]&limit_page_length=2000000";
        HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            List<FichePaye> fichePayes = fichePaye.getFiches(response.getBody(), baseUrl, entity);
            List<FichePaye> ficheParMois = fichePaye.regrouperFichesParMois(fichePayes);
            FichePaye fichePaye1 = ficheParMois.stream().filter(f -> f.getYearMonth().equals(mois))
                    .findFirst()
                    .orElse(null);

            request.setAttribute("idEmp", idEmp);
            request.setAttribute("mois", mois);
            request.setAttribute("fichePaye", fichePaye1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "rh/employee/fichePaye";
    }

    @GetMapping("/fiche-paye/export/pdf")
    public void exportPdf(HttpSession session, HttpServletRequest request, HttpServletResponse response,
                            @RequestParam("idEmp") String idEmp, @RequestParam("mois") YearMonth mois, @RequestParam("data") String data) {
        Auth user = (Auth) session.getAttribute("user");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(YearMonth.class, new YearMonthAdapter())
                .create();

        try {
            FichePaye fichePaye1 = gson.fromJson(data, FichePaye.class);
            fichePaye.genererPdfFichePaie(fichePaye1, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/fiche")
    public String fiche(HttpSession session, HttpServletRequest request, @RequestParam("mois") String mois) {
        Auth user = (Auth) session.getAttribute("user");

        String url = new Config().getErpUrl(configurableEnvironment) + "/api/resource/Salary Slip?filters=[[\"docstatus\",\"!=\",\"2\"]]&fields=[\"*\"]&limit_page_length=2000000";
        HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            List<FichePaye> fichePayes = fichePaye.getFiches(response.getBody(), new Config().getErpUrl(configurableEnvironment), entity);
            Map<String, List<SalarySummary>> parEmploye = salarySummary.regrouperFichesParMoisEtParEmploye(fichePayes);
            request.setAttribute("summaries", parEmploye.get(mois));
            request.setAttribute("mois", mois);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "rh/employee/resumeFiche";
    }
}

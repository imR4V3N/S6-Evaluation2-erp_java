package mg.erp.controllers.rh;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.erp.entities.Auth;
import mg.erp.entities.rh.*;
import mg.erp.utils.Config;
import mg.erp.utils.Date;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/salaire")
@Controller
public class SalaireController {
    private final ConfigurableEnvironment configurableEnvironment;
    private final FichePaye fichePaye = new FichePaye();
    private final SalarySummary salarySummary = new SalarySummary();
    private final Gson gson = new Gson();

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
        String url = new Config().getErpUrl(configurableEnvironment) + "/api/resource/Salary Slip?fields=[\"*\"]&limit_page_length=2000000";
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
}

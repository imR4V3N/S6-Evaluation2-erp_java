package mg.erp.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.erp.entities.Auth;
import mg.erp.entities.Facture;
import mg.erp.utils.Config;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Controller
@RequestMapping("/comptable")
public class ComptableController {
    private final ConfigurableEnvironment configurableEnvironment;
    private final Facture facture = new Facture();

    public ComptableController(ConfigurableEnvironment configurableEnvironment) {
        this.configurableEnvironment = configurableEnvironment;
    }

    //    ---------------------------------------------- FACTURE ----------------------------------------
    private String buildFactureApiUrl() {
        return new Config().getErpUrl(configurableEnvironment) +
                "/api/resource/Purchase Invoice?fields=[\"name\",\"supplier\",\"supplier_name\",\"posting_date\",\"due_date\",\"status\",\"outstanding_amount\",\"grand_total\",\"currency\"]";
    }

    private HttpEntity<String> buildHttpEntityWithSid(String sid) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        return new HttpEntity<>(headers);
    }

    private String getRequestMessage(HttpServletRequest request) {
        return request.getAttribute("message") != null ? request.getAttribute("message").toString() : "";
    }

    // Handle the request for the facture page
    @GetMapping("/factures")
    public String factures(HttpServletRequest request, HttpSession session) {
        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        try {
            String apiUrl = buildFactureApiUrl();
            HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());

            List<Facture> factures = facture.fetchFactures(apiUrl, entity);
            request.setAttribute("factures", factures);
            request.setAttribute("message", getRequestMessage(request));
            return "comptable/facture";
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return "redirect:/fournisseur";
        }
    }

    // ------------------------------------ PAYER LA FACTURE ------------------------------------
    private HttpHeaders createAuthHeaders(String sid) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @PostMapping("/facture/payer")
    public String validerPaiement(@RequestParam("factureName") String factureName, @RequestParam("payement") String payement, HttpSession session) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        String baseUrl = new Config().getErpUrl(configurableEnvironment);
        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        try {
            HttpHeaders headers = createAuthHeaders(user.getSid());

            // Étape 1 : Appel à l'API get_payment_entry
            JsonNode paymentEntry = facture.fetchPaymentEntry(restTemplate, mapper, baseUrl, headers, factureName);

            // Étape 2 : Modification des données du message
            facture.modifyPaymentEntry(paymentEntry, Double.valueOf(payement));

            // Étape 3 : Sauvegarde et soumission
            facture.saveAndSubmitPaymentEntry(restTemplate, mapper, baseUrl, headers, paymentEntry);

            System.out.println("Facture payée avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/comptable/factures";
    }
}

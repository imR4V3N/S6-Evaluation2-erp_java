package mg.erp.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.erp.entities.Auth;
import mg.erp.entities.BonCommande;
import mg.erp.entities.DemandeDevis;
import mg.erp.entities.Fournisseur;
import mg.erp.utils.Config;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/fournisseur")
public class FournisseurController {
    private final ConfigurableEnvironment configurableEnvironment;
    private final Fournisseur fournisseur = new Fournisseur();
    private final DemandeDevis demandeDevis = new DemandeDevis();

    public FournisseurController(ConfigurableEnvironment configurableEnvironment) {
        this.configurableEnvironment = configurableEnvironment;
    }

    private String buildFournisseurUrl() {
        return new Config().getErpUrl(configurableEnvironment) + "/api/resource/Supplier?fields=[\"name\",\"supplier_name\",\"supplier_type\",\"supplier_group\",\"country\"]";
    }

    private HttpEntity<String> buildHttpEntityWithSid(String sid) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        return new HttpEntity<>(headers);
    }

    // Handle the request for the fournisseur page
    @GetMapping
    public String fournisseur(HttpSession session, HttpServletRequest request) {
        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        String url = buildFournisseurUrl();
        HttpEntity<String> httpEntity = buildHttpEntityWithSid(user.getSid());

        try {
            ResponseEntity<String> response = fournisseur.executeFournisseurRequest(url, httpEntity);
            List<Fournisseur> fournisseurs = fournisseur.parseFournisseursFromResponse(response.getBody());

            request.setAttribute("fournisseurs", fournisseurs);
            return "fournisseur/list";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
    }

    @GetMapping("/devis")
    public String devis(@RequestParam("name") String name, HttpServletRequest request, HttpSession session) {
        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());

        try {
            List<DemandeDevis> result = demandeDevis.fetchDevisForSupplier(name, user.getSid(), entity, configurableEnvironment);
            request.setAttribute("devis", result);
            request.setAttribute("fournisseur", name);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur de récupération des devis : " + e.getMessage());
        }

        return "demandeDevis";
    }


    @GetMapping("/bon-commandes")
    public String bonsDeCommande(@RequestParam("name") String name, HttpServletRequest request, HttpSession session) {
        List<BonCommande> result = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        String url = new Config().getErpUrl(configurableEnvironment);
        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        try {
            // 1. Récupération de tous les bons de commande
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", "sid=" + user.getSid());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // 2. Appel de l'API
            String apiUrl = url + "/api/resource/Purchase Order" +
                    "?fields=[\"name\",\"supplier\",\"supplier_name\",\"transaction_date\",\"schedule_date\",\"status\",\"total_qty\",\"grand_total\",\"currency\",\"in_words\",\"company\"]";

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );
            JsonNode bonList = response.getBody().path("data");

            // 2. Filtrage par nom de fournisseur
            for (JsonNode bon : bonList) {
                String nomFournisseur = bon.path("supplier_name").asText();
                if (name.equalsIgnoreCase(nomFournisseur)) {
                    BonCommande commande = new BonCommande();
                    commande.setName(bon.path("name").asText());
                    commande.setSupplier(bon.path("supplier").asText());
                    commande.setSupplier_name(nomFournisseur);
                    commande.setTransaction_date(bon.path("transaction_date").asText());
                    commande.setSchedule_date(bon.path("schedule_date").asText());
                    commande.setStatus(bon.path("status").asText());
                    commande.setTotal_qty(bon.path("total_qty").asDouble());
                    commande.setGrand_total(bon.path("grand_total").asDouble());
                    commande.setCurrency(bon.path("currency").asText());
                    commande.setIn_words(bon.path("in_words").asText());
                    commande.setCompany(bon.path("company").asText());

                    result.add(commande);
                }
            }

            request.setAttribute("bons", result);
            request.setAttribute("fournisseur", name);
            return "fournisseur/bonCommande"; // la vue JSP/HTML
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return "redirect:/fournisseur"; // redirection en cas d'erreur
        }
    }

}

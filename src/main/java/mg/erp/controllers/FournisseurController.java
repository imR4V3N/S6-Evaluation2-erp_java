package mg.erp.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.erp.entities.*;
import mg.erp.utils.Config;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/fournisseur")
public class FournisseurController {
    private final ConfigurableEnvironment configurableEnvironment;
    private final Fournisseur fournisseur = new Fournisseur();
    private final DevisFournisseurItem devisFournisseurItem = new DevisFournisseurItem();
    private final DevisFournisseur devisFournisseur = new DevisFournisseur();
    private final BonCommande bonCommande = new BonCommande();

    public FournisseurController(ConfigurableEnvironment configurableEnvironment) {
        this.configurableEnvironment = configurableEnvironment;
    }

//    -------------------------------------------- FOURNISSEUR ----------------------------------------
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


//    -------------------------------------------- DEMANDE DEVIS --------------------------------------------------

//    @GetMapping("/demandeDevis")
//    public String demandeDevis(@RequestParam("name") String name, HttpServletRequest request, HttpSession session) {
//        Auth user = (Auth) session.getAttribute("user");
//
//        if (user.getSid() == null) {
//            return "redirect:/";
//        }
//
//        HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());
//
//        try {
//            List<DemandeDevis> result = demandeDevis.fetchDevisForSupplier(name, user.getSid(), entity, configurableEnvironment);
//            request.setAttribute("demandeDevis", result);
//            request.setAttribute("fournisseur", name);
//        } catch (Exception e) {
//            e.printStackTrace();
//            request.setAttribute("error", "Erreur de récupération des devis : " + e.getMessage());
//        }
//
//        return "fournisseur/demandeDevis";
//    }
//
//    @GetMapping("/demandeDevis/{devisName}/produits")
//    public String produitsParDevis(@PathVariable("devisName") String devisName, HttpServletRequest request, HttpSession session) {
//        RestTemplate restTemplate = new RestTemplate();
//        String url = new Config().getErpUrl(configurableEnvironment);
//        Auth user = (Auth) session.getAttribute("user");
//
//        if (user.getSid() == null) {
//            return "redirect:/";
//        }
//        // Auth headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cookie", "sid=" + user.getSid());
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        try {
//            String urlDetail = url + "/api/resource/Request for Quotation/" + devisName;
//            ResponseEntity<JsonNode> detailResponse = restTemplate.exchange(urlDetail, HttpMethod.GET, entity, JsonNode.class);
//            JsonNode data = detailResponse.getBody().path("data");
//
//            // 2. Récupérer les fournisseurs (concaténés)
//            String fournisseurs = StreamSupport.stream(data.path("suppliers").spliterator(), false)
//                    .map(supp -> supp.path("supplier_name").asText())
//                    .collect(Collectors.joining(", "));
//
//            // 3. Remplir la liste des produits
//            List<Produit> produits = new ArrayList<>();
//            for (JsonNode item : data.path("items")) {
//                Produit p = new Produit();
//                p.setName(item.path("name").asText());
//                p.setItem_code(item.path("item_code").asText());
//                p.setItem_name(item.path("item_name").asText());
//                p.setQty(item.path("qty").asDouble());
//                p.setRate(item.path("rate").asText());
//                p.setDescription(item.path("description").asText());
//                p.setUom(item.path("uom").asText());
//                p.setSchedule_date(item.path("schedule_date").asText());
//                p.setDevis_name(devisName);
//                p.setFournisseur(fournisseurs);
//
//                System.out.println("Produit: " + p.getName() + ", Code: " + p.getItem_code() + ", Fournisseur: " + fournisseurs + ", Devis: " + devisName + ", Date: " + p.getSchedule_date() + ", UOM: " + p.getUom() + ", Description: " + p.getDescription() + ", Qty: " + p.getQty() + ", Rate: " + p.getRate() + ", Total: " + (p.getRate() != null ? Double.parseDouble(p.getRate()) * p.getQty() : 0) + ", Grand Total: " + (p.getRate() != null ? Double.parseDouble(p.getRate()) * p.getQty() : 0));
//
//                produits.add(p);
//            }
//
//            request.setAttribute("produits", produits);
//            request.setAttribute("devis", devisName);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            request.setAttribute("error", "Erreur lors de la récupération des produits : " + e.getMessage());
//        }
//
//        return "fournisseur/detailsDevis";
//    }

// ----------------------------------------- DEVIS FOURNISSEUR ---------------------------------------------------------
    private String buildDevisFournisseurUrl(String fournisseurName) {
        return new Config().getErpUrl(configurableEnvironment) + "/api/resource/Supplier Quotation?filters=[[\"supplier\",\"=\",\"" + fournisseurName + "\"]]&" +
                "fields=[\"name\",\"status\",\"supplier\",\"company\",\"transaction_date\",\"valid_till\",\"currency\",\"total_qty\",\"total\",\"grand_total\",\"supplier_address\",\"shipping_address\",\"billing_address\"]";
    }

    @GetMapping("/devis-fournisseur")
    public String devisFournisseur(HttpSession session, @RequestParam("name") String fournisseurName, HttpServletRequest request) {
        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        String url = buildDevisFournisseurUrl(fournisseurName);
        HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());

        List<DevisFournisseur> devisFournisseurs = devisFournisseur.fetchDevisFournisseurs(fournisseurName, url, entity);


        request.setAttribute("fournisseur", fournisseurName);
        request.setAttribute("devisFournisseurs", devisFournisseurs);

        return "fournisseur/devisFournisseur";
    }

//    ----------------------------------------- DEVIS FOURNISSEUR ITEM ---------------------------------------------------
    @GetMapping("/devis-fournisseur/{name}/items")
    public String devisFournisseurItem(HttpSession session, @PathVariable("name") String name, HttpServletRequest request) {
        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        String baseUrl = new Config().getErpUrl(configurableEnvironment);
        HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());

        List<DevisFournisseurItem> devisFournisseurItems = devisFournisseurItem.fetchItemsForDevis(name, entity, baseUrl);


        request.setAttribute("devisName", name);
        request.setAttribute("devisFournisseurItems", devisFournisseurItems);

        return "fournisseur/produitDevisFournisseur";
    }


//    -------------------------------------------- BON COMMANDE --------------------------------------------------------

    private String buildBonCommandeApiUrl() {
        return new Config().getErpUrl(configurableEnvironment) +
                "/api/resource/Purchase Order?fields=[\"name\",\"supplier\",\"supplier_name\",\"transaction_date\",\"schedule_date\",\"status\",\"total_qty\",\"grand_total\",\"currency\",\"in_words\",\"company\",\"per_received\"]";
    }

    @GetMapping("/bon-commandes")
    public String bonsDeCommande(@RequestParam("name") String name, HttpServletRequest request, HttpSession session) {
        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }

        try {
            String apiUrl = buildBonCommandeApiUrl();
            HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());

            List<BonCommande> bons = bonCommande.fetchAndFilterBonsDeCommande(name, apiUrl, entity, new Config().getErpUrl(configurableEnvironment));
            request.setAttribute("bons", bons);
            request.setAttribute("fournisseur", name);
            return "fournisseur/bonCommande";
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la récupération des bons de commande : " + e.getMessage());
            return "redirect:/fournisseur";
        }
    }



}

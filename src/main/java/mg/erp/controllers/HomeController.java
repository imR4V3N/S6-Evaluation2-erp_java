package mg.erp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.erp.entities.Auth;
import mg.erp.utils.Config;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Controller
public class HomeController {
    private final ConfigurableEnvironment configurableEnvironment;

    public HomeController(ConfigurableEnvironment configurableEnvironment) {
        this.configurableEnvironment = configurableEnvironment;
    }

    @GetMapping("/")
    public String index(HttpServletRequest request) {
        request.setAttribute("error", null);
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpServletRequest request,
                        HttpSession session) {

        String url = new Config().getErpUrl(configurableEnvironment) +"/api/method/login";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("usr", username);
        body.add("pwd", password);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers);

        try {
            // Execute login request
            ResponseEntity<Auth> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    Auth.class
            );

            // ✅ Récupérer l'objet Auth
            Auth auth = response.getBody();

            // ✅ Récupérer les headers pour extraire le cookie "sid"
            List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            String sid = null;

            if (cookies != null) {
                for (String cookie : cookies) {
                    if (cookie.startsWith("sid=")) {
                        sid = cookie.split(";")[0].split("=")[1];
                        break;
                    }
                }
            }

            // ✅ Si login réussi
            if (auth != null && "Logged In".equals(auth.getMessage())) {
                auth.setSid(sid);
                session.setAttribute("user", auth);
                return "redirect:/fournisseur";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("error", "Identifiants incorrects");
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "index";
    }
}

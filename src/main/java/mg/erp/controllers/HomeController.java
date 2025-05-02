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
    private final Auth auth = new Auth();

    public HomeController(ConfigurableEnvironment configurableEnvironment) {
        this.configurableEnvironment = configurableEnvironment;
    }

//    -------------------------------------- LOGIN ----------------------------------------
    private String buildLoginUrl() {
        return new Config().getErpUrl(configurableEnvironment) + "/api/method/login";
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

        String url = buildLoginUrl();
        HttpEntity<MultiValueMap<String, String>> httpEntity = auth.buildHttpEntity(username, password);

        try {
            ResponseEntity<Auth> response = auth.executeLoginRequest(url, httpEntity);

            Auth auth = response.getBody();
            String sid = auth.extractSidFromCookies(response.getHeaders());

            if (auth.isLoginSuccessful(auth, sid)) {
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

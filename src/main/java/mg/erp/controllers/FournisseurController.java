package mg.erp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/fournisseur")
public class FournisseurController {
    // Handle the request for the fournisseur page
    @GetMapping
    public String fournisseur() {
        return "fournisseur/list";
    }
}

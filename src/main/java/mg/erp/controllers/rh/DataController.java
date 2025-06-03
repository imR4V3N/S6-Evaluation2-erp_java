package mg.erp.controllers.rh;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mg.erp.entities.Auth;
import mg.erp.models.data.ImportResponse;
import mg.erp.services.data.ImportService;
import mg.erp.utils.Config;
import mg.erp.utils.data.csv.Import;
import mg.erp.utils.data.csv.utils.File;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;

@RequestMapping("data")
@Controller
public class DataController {
    private final ConfigurableEnvironment configurableEnvironment;
    @Autowired
    private ImportService importService;

    public DataController(ConfigurableEnvironment configurableEnvironment) {
        this.configurableEnvironment = configurableEnvironment;
    }

    @GetMapping("/page-import")
     public String page() {
         return "rh/data/import";
     }

    @GetMapping("/import/api")
    public String importApi(@RequestParam("file1") MultipartFile file1,
                            @RequestParam("file2") MultipartFile file2,
                            @RequestParam("file3") MultipartFile file3,
                            HttpSession session,
                            HttpServletRequest request) {

        String dir = "/home/raven/Documents/csv";
        Auth user = (Auth) session.getAttribute("user");

        if (user.getSid() == null) {
            return "redirect:/";
        }
        String baseUrl = new Config().getErpUrl(configurableEnvironment);

        try {
            ImportResponse response = importService.importCsvFiles(
                    file1, file2, file3, baseUrl, user.getSid());

            if ("success".equals(response.getStatus()) || response.isSuccess()) {
                request.setAttribute("success", response.getMessageAsString());
                request.setAttribute("insertedRecords", response.getInserted_records());
            } else {
                request.setAttribute("error", response.getMessageAsString());
                request.setAttribute("validationErrors", response.getValidation_errors() != null ? response.getValidation_errors() : new java.util.ArrayList<>());
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return "redirect:/";
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while importing CSV files: " + e.getMessage());
            request.setAttribute("validationErrors", new java.util.ArrayList<>());
        }
        return "rh/data/import";
    }

    private File createFile(MultipartFile multipartFile, String dir, HashMap<String, Class<?>> types) {
        File file = new File();
        String name = multipartFile.getOriginalFilename();
        file.setName(name);
        file.setTypes(types);
        String path = file.upload(multipartFile, dir);
        file.setPath(path);
        return file;
    }

    private HttpEntity<String> buildHttpEntityWithSid(String sid) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        return new HttpEntity<>(headers);
    }

    @PostMapping("/import/csv")
    public String importCsv(@RequestParam("file1") MultipartFile file1,
                            @RequestParam("file2") MultipartFile file2,
                            @RequestParam("file3") MultipartFile file3,
                            HttpSession session,
                            HttpServletRequest request) {
         String dir = "/home/raven/Documents/csv";
         Auth user = (Auth) session.getAttribute("user");

         if (user.getSid() == null) {
             return "redirect:/";
         }
         String baseUrl = new Config().getErpUrl(configurableEnvironment);
         String empUrl = baseUrl + "/api/resource/Employee?fields=[\"*\"]";
         HttpEntity<String> entity = buildHttpEntityWithSid(user.getSid());
         RestTemplate restTemplate = new RestTemplate();
         ResponseEntity<String> response = restTemplate.exchange(empUrl, HttpMethod.GET, entity, String.class);

         File fileEmp = createFile(file1, dir, new HashMap<>() {{
             put("Ref", String.class);
             put("Nom", String.class);
             put("Prenom", String.class);
             put("genre", String.class);
             put("Date embauche", String.class);
             put("date naissance", String.class);
             put("company", String.class);
         }});

         File fileStruct = createFile(file2, dir, new HashMap<>() {{
             put("salary structure", String.class);
             put("name", String.class);
             put("Abbr", String.class);
             put("type", String.class);
             put("valeur", String.class);
             put("company", String.class);
         }});

         File fileSlip = createFile(file3, dir, new HashMap<>() {{
             put("Mois", String.class);
             put("Ref Employe", String.class);
             put("Salaire Base", String.class);
             put("Salaire", String.class);
         }});

         Import csvImport = new Import();
         csvImport.addFile(fileEmp);
         csvImport.addFile(fileStruct);
         csvImport.addFile(fileSlip);

         try {
             csvImport.importCsv(",", user.getSid(), baseUrl, response);
             request.setAttribute("success", "Donnees importer avec succes!");
         } catch (Exception e) {
             e.printStackTrace();
             request.setAttribute("error", e.getMessage());
         }

        return "rh/data/import";
     }
}

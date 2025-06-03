package mg.erp.services.data;

import jakarta.servlet.http.HttpSession;
import mg.erp.models.data.ImportRequest;
import mg.erp.models.data.ImportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

@Service
public class ImportService {
    @Autowired
    private WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ImportResponse importCsvFiles(MultipartFile file1,
                                         MultipartFile file2,
                                         MultipartFile file3,
                                         String baseUrl, String sid) throws Exception {

        if (sid == null) {
            throw new IllegalStateException("User is not authenticated");
        }


        ImportRequest request = new ImportRequest();
        if (file1 != null && !file1.isEmpty()) {
            request.setFile1(Base64.getEncoder().encodeToString(file1.getBytes()));
            System.out.println("File 1 encoded, size: " + file1.getSize());
        }
        if (file2 != null && !file2.isEmpty()) {
            request.setFile2(Base64.getEncoder().encodeToString(file2.getBytes()));
            System.out.println("File 2 encoded, size: "+ file2.getSize());
        }
        if (file3 != null && !file3.isEmpty()) {
            request.setFile3(Base64.getEncoder().encodeToString(file3.getBytes()));
            System.out.println("File 3 encoded, size: "+ file3.getSize());
        }

        String url = baseUrl + "/hrms.data.import_controller.import_csv";
        WebClient client = webClientBuilder.baseUrl(url).build();

        try {
            String requestBody = objectMapper.writeValueAsString(request);
            System.out.println("Sending request to {} with body: "+ requestBody);

            ResponseEntity<String> response = client.post()
                    .cookie("sid", sid)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            if (response != null && response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                System.out.println("Received response: " + response.getBody());
                ImportResponse importResponse = objectMapper.readValue(response.getBody(), ImportResponse.class);
                return importResponse;
            }

            System.out.println("Received null or unsuccessful response:"+ response);
            ImportResponse errorResponse = new ImportResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to import CSV files");
            return errorResponse;

        } catch (Exception e) {
            ImportResponse errorResponse = new ImportResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error occurred: " + e.getMessage());
            return errorResponse;
        }
    }
}

package mg.erp.entities;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class Auth {
    private String message;
    private String full_name;
    private String sid;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public HttpEntity<MultiValueMap<String, String>> buildHttpEntity(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("usr", username);
        body.add("pwd", password);

        return new HttpEntity<>(body, headers);
    }

    public ResponseEntity<Auth> executeLoginRequest(String url, HttpEntity<MultiValueMap<String, String>> httpEntity) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, Auth.class);
    }

    public String extractSidFromCookies(HttpHeaders headers) {
        List<String> cookies = headers.get(HttpHeaders.SET_COOKIE);
        if (cookies != null) {
            for (String cookie : cookies) {
                if (cookie.startsWith("sid=")) {
                    return cookie.split(";")[0].split("=")[1];
                }
            }
        }
        return null;
    }

    public boolean isLoginSuccessful(Auth auth, String sid) {
        return auth != null && "Logged In".equals(auth.getMessage()) && sid != null;
    }
}

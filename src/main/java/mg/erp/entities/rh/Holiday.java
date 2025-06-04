package mg.erp.entities.rh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class Holiday {
    String holiday_list_name;
    String from_date;
    String to_date;

    public String getHoliday_list_name() {
        return holiday_list_name;
    }

    public void setHoliday_list_name(String holiday_list_name) {
        this.holiday_list_name = holiday_list_name;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public List<Holiday> getHolidays(HttpEntity<String> entity, String url) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = mapper.readTree(response.getBody()).path("data");
        List<Holiday> results = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                Holiday holiday = new Holiday();
                holiday.setHoliday_list_name(node.path("holiday_list_name").asText());
                holiday.setFrom_date(node.path("from_date").asText());
                holiday.setTo_date(node.path("to_date").asText());

                results.add(holiday);
            }
        }

        return results;
    }
}

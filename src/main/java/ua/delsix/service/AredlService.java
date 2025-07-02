package ua.delsix.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AredlService {
    private final String DEMON_SEARCH_URL = "https://api.aredl.net/v2/api/aredl/levels/";
    private final RestTemplate restTemplate = new RestTemplate();

    public int getPositionForLevel(Integer id) throws NullPointerException {
        if (id == null) {
            throw new NullPointerException("id is null or empty");
        }

        String uri = DEMON_SEARCH_URL;
        uri = uri.concat(String.valueOf(id));

        try {
            String json = restTemplate.getForObject(uri, String.class);
            JSONObject jsonObject = new JSONObject(json);

            return jsonObject.getInt("position");
        } catch (Exception e) {
            System.err.println("Error fetching from GDDL API: " + e.getMessage());
            return -1;
        }
    }
}

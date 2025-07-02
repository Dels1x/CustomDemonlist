package ua.delsix.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class GddlService {
    private final String DEMON_SEARCH_URL = "https://gdladder.com/api/level/search";
    private final RestTemplate restTemplate = new RestTemplate();

    public JSONObject searchLevel(String name, String author) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(DEMON_SEARCH_URL)
                .queryParam("limit", 10);

        if (name != null && !name.isEmpty() && !name.startsWith("Demon #")) {
            uriBuilder.queryParam("name", name);
        }

        if (author != null && !author.isEmpty() && !author.equals("Author")) {
            uriBuilder.queryParam("creator", author);
        }

        URI uri = uriBuilder.build().encode().toUri();

        try {
            String json = restTemplate.getForObject(uri, String.class);
            JSONObject root = new JSONObject(json);
            JSONArray levels = root.getJSONArray("levels");

            if (!levels.isEmpty()) {
                return levels.getJSONObject(0); // Return first level
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error fetching from GDDL API: " + e.getMessage());
            return null;
        }
    }
}

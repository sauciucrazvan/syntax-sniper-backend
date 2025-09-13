package dev.razvansauciuc.plg.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GitHubClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    public static final String BASE_URL = "https://api.github.com/repos/TheRenegadeCoder/sample-programs/contents/archive";
    private static final String GITHUB_TOKEN;

    static {
        Dotenv dotenv = Dotenv.load();
        GITHUB_TOKEN = dotenv.get("GITHUB_TOKEN");
    }

    private final Map<String, JsonNode> jsonCache = new HashMap<>();
    public final Map<String, String> codeCache = new HashMap<>();
    public List<String> cachedLanguagePaths = null;

    public JsonNode getJsonCached(String url) throws Exception {
        if (jsonCache.containsKey(url)) return jsonCache.get(url);
        JsonNode result = getJson(url);
        jsonCache.put(url, result);
        return result;
    }

    private JsonNode getJson(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "Java HttpClient")
                .header("Authorization", "Bearer " + GITHUB_TOKEN)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(response.body());
    }

    public String getText(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Java HttpClient")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public List<JsonNode> getAllFilesCached(String path) throws Exception {
        String fullPath = BASE_URL + "/" + path;
        JsonNode content = getJsonCached(fullPath);
        List<JsonNode> files = new ArrayList<>();

        for (JsonNode node : content) {
            String type = node.path("type").asText();
            String name = node.path("name").asText();
            if ("file".equals(type)) {
                files.add(node);
            } else if ("dir".equals(type)) {
                files.addAll(getAllFilesCached(path + "/" + name));
            }
        }
        return files;
    }

}

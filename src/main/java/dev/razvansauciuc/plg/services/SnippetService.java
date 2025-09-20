package dev.razvansauciuc.plg.services;

import com.fasterxml.jackson.databind.JsonNode;
import dev.razvansauciuc.plg.clients.GitHubClient;
import dev.razvansauciuc.plg.responses.SnippetResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static dev.razvansauciuc.plg.clients.GitHubClient.BASE_URL;
import static dev.razvansauciuc.plg.utils.SnippetUtils.getRandomSnippet;

@Service
public class SnippetService {
    private static final List<String> EXCLUDED_EXTENSIONS = List.of(".md", ".yml", ".yaml", ".json");

    private final GitHubClient githubClient;

    public SnippetService(GitHubClient githubClient) {
        this.githubClient = githubClient;
    }

    public SnippetResponse generateSnippet() throws Exception {
        if (githubClient.cachedLanguagePaths == null) {
            githubClient.cachedLanguagePaths = new ArrayList<>();
            JsonNode archiveRoot = githubClient.getJsonCached(BASE_URL);

            for (JsonNode letterFolder : archiveRoot) {
                String letter = letterFolder.path("name").asText(null);
                if (letter == null || !"dir".equals(letterFolder.path("type").asText())) continue;

                JsonNode letterContent = githubClient.getJsonCached(BASE_URL + "/" + letter);
                for (JsonNode langFolder : letterContent) {
                    String langName = langFolder.path("name").asText(null);
                    if (langName != null && "dir".equals(langFolder.path("type").asText())) {
                        githubClient.cachedLanguagePaths.add(letter + "/" + langName);
                    }
                }
            }
        }

        if (githubClient.cachedLanguagePaths.isEmpty()) {
            throw new IllegalStateException("No programming languages found in the repository.");
        }

        String chosenPath = null;
        List<JsonNode> fileNodes = new ArrayList<>();
        String code = null;
        JsonNode codeFile = null;
        String downloadUrl = null;

        // Keep trying until we get a valid code snippet
        while (code == null) {
            // Pick a random language path
            chosenPath = githubClient.cachedLanguagePaths.get(new Random().nextInt(githubClient.cachedLanguagePaths.size()));
            fileNodes = githubClient.getAllFilesCached(chosenPath);

            if (fileNodes.isEmpty()) continue;

            // Try random files until we find a valid one
            for (int i = 0; i < fileNodes.size(); i++) {
                JsonNode candidate = fileNodes.get(new Random().nextInt(fileNodes.size()));
                downloadUrl = candidate.path("download_url").asText(null);

                if (downloadUrl == null || EXCLUDED_EXTENSIONS.stream().anyMatch(downloadUrl::endsWith)) {
                    continue;
                }

                if (githubClient.codeCache.containsKey(downloadUrl)) {
                    code = githubClient.codeCache.get(downloadUrl);
                } else {
                    try {
                        code = githubClient.getText(downloadUrl);
                        githubClient.codeCache.put(downloadUrl, code);
                    } catch (Exception e) {
                        System.err.println("Failed to fetch code from: " + downloadUrl);
                        code = null;
                    }
                }

                if (code != null) {
                    codeFile = candidate;
                    break;
                }
            }
        }

        String snippet = getRandomSnippet(code, new Random().nextInt(21) + 5);

        List<String> options = new ArrayList<>();
        for (String path : githubClient.cachedLanguagePaths) {
            String lang = path.substring(path.indexOf("/") + 1);
            if (!path.equals(chosenPath)) {
                options.add(lang);
            }
        }

        String chosenLang = chosenPath.substring(chosenPath.indexOf("/") + 1);
        Collections.shuffle(options);
        List<String> multipleChoice = new ArrayList<>(options.subList(0, Math.min(3, options.size())));
        multipleChoice.add(chosenLang);
        Collections.shuffle(multipleChoice);

        return new SnippetResponse(snippet, chosenLang, multipleChoice.toArray(new String[0]));
    }
}
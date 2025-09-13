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

        List<JsonNode> fileNodes = new ArrayList<>();
        String chosenPath = null;

        while (fileNodes.isEmpty()) {
            chosenPath = githubClient.cachedLanguagePaths.get(new Random().nextInt(githubClient.cachedLanguagePaths.size()));
            fileNodes = githubClient.getAllFilesCached(chosenPath);
        }

        JsonNode codeFile = null;
        String downloadUrl = null;
        String code = null;

        for (int i = 0; i < fileNodes.size(); i++) {
            JsonNode candidate = fileNodes.get(new Random().nextInt(fileNodes.size()));
            downloadUrl = candidate.path("download_url").asText(null);

            if (downloadUrl != null) {
                boolean isExcluded = EXCLUDED_EXTENSIONS.stream().anyMatch(downloadUrl::endsWith);
                if (isExcluded) {
                    continue;
                }

                if (githubClient.codeCache.containsKey(downloadUrl)) {
                    code = githubClient.codeCache.get(downloadUrl);
                } else {
                    code = githubClient.getText(downloadUrl);
                    githubClient.codeCache.put(downloadUrl, code);
                }

                codeFile = candidate;
                break;
            }
        }

        if (code == null) {
            throw new IllegalStateException("No downloadable file found for selected language.");
        }

        String snippet = getRandomSnippet(code, new Random().nextInt(25 - 5 + 1) + 5);

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
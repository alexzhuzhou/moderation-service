package com.ecs160.hw2.Controllers;

import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@RestController
@RequestMapping("/moderation")
public class ModerationController {
    private static final List<String> BANNED_WORDS = List.of(
            "illegal", "fraud", "scam", "exploit", "dox", "swatting", "hack", "crypto", "bots"
    );

    private static final String HASHTAGGING_SERVICE_URL = "http://localhost:30001/hashtag";
    @PostMapping("/check")
    public ModerationResponse moderate(@RequestBody ModerationRequest request) {
        if (request == null || request.getPostContent() == null || request.getPostContent().isBlank()) {
            return new ModerationResponse("ERROR", "Invalid post content");
        }

        for (String word : BANNED_WORDS) {
            if (request.getPostContent().toLowerCase().contains(word)) {
                return new ModerationResponse("FAILED", request.getPostContent());
            }
        }

        String hashtaggedContent = forwardToHashtaggingService(request.getPostContent());

        return new ModerationResponse("PASSED", hashtaggedContent);
    }




    private String forwardToHashtaggingService(String postContent) {
        HttpClient client = HttpClient.newHttpClient();


        String jsonPayload = "{\"postContent\": \"" + escapeJson(postContent) + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(HASHTAGGING_SERVICE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            return "{\"status\": \"ERROR\", \"postContent\": \"Failed to reach Hashtagging Service\"}";
        }
    }

    private static String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"").replace("\n", "\\n");
    }


    static class ModerationRequest {
        private String postContent;
        public String getPostContent() { return postContent; }
        public void setPostContent(String postContent) { this.postContent = postContent; }
    }

    static class ModerationResponse {
        private String status;
        private String postContent;

        public ModerationResponse(String status, String postContent) {
            this.status = status;
            this.postContent = postContent;
        }

        public String getStatus() { return status; }
        public String getPostContent() { return postContent; }
    }
}

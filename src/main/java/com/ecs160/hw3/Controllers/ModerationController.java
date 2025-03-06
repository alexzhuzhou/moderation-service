package com.ecs160.hw3.Controllers;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moderation") // Base URL for all moderation endpoints
public class ModerationController {
    private static final List<String> BANNED_WORDS = List.of(
            "illegal", "fraud", "scam", "exploit", "dox", "swatting", "hack", "crypto", "bots"
    );

    @PostMapping("/check")
    public ModerationResponse moderate(@RequestBody ModerationRequest request) {
        for (String word : BANNED_WORDS) {
            if (request.getPostContent().toLowerCase().contains(word)) {
                return new ModerationResponse("FAILED", request.getPostContent());
            }
        }
        return new ModerationResponse("PASSED", request.getPostContent());
    }

    // DTO for handling the incoming JSON request
    static class ModerationRequest {
        private String postContent;

        public String getPostContent() { return postContent; }
        public void setPostContent(String postContent) { this.postContent = postContent; }
    }

    // DTO for returning a structured JSON response
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

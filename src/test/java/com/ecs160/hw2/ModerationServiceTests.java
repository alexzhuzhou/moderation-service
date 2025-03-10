package com.ecs160.hw2;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ecs160.hw2.Controllers.ModerationController;
import com.ecs160.hw2.Controllers.ModerationController.ModerationRequest;
import com.ecs160.hw2.Controllers.ModerationController.ModerationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.net.http.HttpClient;

import java.net.http.HttpResponse;

@SpringBootTest(classes = ModerationController.class)
public class ModerationServiceTests {

	private ModerationController moderationController;

	private HttpClient httpClient;

	private HttpResponse<String> httpResponse;

	@BeforeEach
	void setUp() {
		httpClient = mock(HttpClient.class);
		httpResponse = mock(HttpResponse.class);
		moderationController = new ModerationController();
	}

	@Test
	void testContextLoads() {
		assertNotNull(moderationController);
	}

	@Test
	void testModerationFailsForBannedWords() {
		ModerationRequest request = new ModerationRequest();
		request.setPostContent("This is a scam!");

		ModerationResponse response = moderationController.moderate(request);
		assertEquals("FAILED", response.getStatus());
		assertEquals("This is a scam!", response.getPostContent());
	}

	@Test
	void testModerationPassesForValidPost() {
		String testContent = "This is a normal post";
		ModerationRequest request = new ModerationRequest();
		request.setPostContent(testContent);

		ModerationResponse response = moderationController.moderate(request);

		System.out.println("Moderation Response: " + response.getStatus());

		assertTrue("PASSED".equals(response.getStatus()), "Expected moderation to pass but it failed!");
	}






}

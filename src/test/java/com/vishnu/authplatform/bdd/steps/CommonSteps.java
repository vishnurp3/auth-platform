package com.vishnu.authplatform.bdd.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class CommonSteps {

    private final SharedContext sharedContext;
    private final ObjectMapper objectMapper;

    @Given("the system is running")
    public void theSystemIsRunning() {
    }

    @Then("the response status should be {int}")
    public void responseStatusShouldBe(int code) {
        ResponseEntity<String> response = sharedContext.getLastResponse();
        assertNotNull(response, "No response captured");
        assertEquals(code, response.getStatusCode().value());
    }

    public String readField(String field) {
        ResponseEntity<String> response = sharedContext.getLastResponse();
        assertNotNull(response, "No response captured");
        String body = response.getBody();
        assertNotNull(body, "No response body");
        try {
            Map<String, Object> payload = objectMapper.readValue(body, new TypeReference<>() {
            });
            Object value = payload.get(field);
            return value != null ? String.valueOf(value) : null;
        } catch (Exception e) {
            fail("Failed to parse response body: " + e.getMessage());
            return null;
        }
    }
}

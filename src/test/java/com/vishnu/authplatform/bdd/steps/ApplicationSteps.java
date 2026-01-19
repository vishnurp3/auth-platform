package com.vishnu.authplatform.bdd.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class ApplicationSteps {

    private static final String API_KEY_HEADER = "X-Admin-Api-Key";
    private static final String APPLICATIONS_ENDPOINT = "/api/v1/admin/applications";

    private final TestRestTemplate rest;
    private final ObjectMapper objectMapper;
    private final SharedContext sharedContext;

    @Value("${app.admin.api-key}")
    private String validAdminApiKey;

    private String currentApiKey;

    @Before
    public void resetApplicationScenarioState() {
        currentApiKey = null;
    }

    @Given("I am authenticated as admin")
    public void iAmAuthenticatedAsAdmin() {
        currentApiKey = validAdminApiKey;
    }

    @Given("I am authenticated with invalid admin key")
    public void iAmAuthenticatedWithInvalidAdminKey() {
        currentApiKey = "invalid-api-key";
    }

    @When("I create an application with code {string} and name {string}")
    public void iCreateApplicationWithCodeAndName(String code, String name) {
        Map<String, Object> body = new HashMap<>();
        body.put("applicationCode", normalizeInput(code));
        body.put("name", normalizeInput(name));
        sharedContext.setLastResponse(postJsonWithAuth(APPLICATIONS_ENDPOINT, body, currentApiKey));
    }

    @When("I create an application with code {string} name {string} and description {string}")
    public void iCreateApplicationWithCodeNameAndDescription(String code, String name, String description) {
        Map<String, Object> body = new HashMap<>();
        body.put("applicationCode", normalizeInput(code));
        body.put("name", normalizeInput(name));
        body.put("description", normalizeInput(description));
        sharedContext.setLastResponse(postJsonWithAuth(APPLICATIONS_ENDPOINT, body, currentApiKey));
    }

    @When("I create an application with code {string} name {string} and status {string}")
    public void iCreateApplicationWithCodeNameAndStatus(String code, String name, String status) {
        Map<String, Object> body = new HashMap<>();
        body.put("applicationCode", normalizeInput(code));
        body.put("name", normalizeInput(name));
        body.put("status", normalizeInput(status));
        sharedContext.setLastResponse(postJsonWithAuth(APPLICATIONS_ENDPOINT, body, currentApiKey));
    }

    @When("I create an application without authentication with code {string} and name {string}")
    public void iCreateApplicationWithoutAuthentication(String code, String name) {
        Map<String, Object> body = new HashMap<>();
        body.put("applicationCode", normalizeInput(code));
        body.put("name", normalizeInput(name));
        sharedContext.setLastResponse(postJsonWithAuth(APPLICATIONS_ENDPOINT, body, null));
    }

    @Then("the application code should be {string}")
    public void applicationCodeShouldBe(String expected) {
        assertEquals(expected, readField("applicationCode"));
    }

    @Then("the application name should be {string}")
    public void applicationNameShouldBe(String expected) {
        assertEquals(expected, readField("name"));
    }

    @Then("the application description should be {string}")
    public void applicationDescriptionShouldBe(String expected) {
        assertEquals(expected, readField("description"));
    }

    @Then("the application status should be {string}")
    public void applicationStatusShouldBe(String expected) {
        assertEquals(expected, readField("status"));
    }

    @Then("the application should have a valid id")
    public void applicationShouldHaveValidId() {
        String id = readField("applicationId");
        assertNotNull(id);
        assertFalse(id.isBlank());
        assertDoesNotThrow(() -> java.util.UUID.fromString(id));
    }

    @Then("the application should have timestamps")
    public void applicationShouldHaveTimestamps() {
        String createdAt = readField("createdAt");
        String updatedAt = readField("updatedAt");
        assertNotNull(createdAt);
        assertNotNull(updatedAt);
        assertFalse(createdAt.isBlank());
        assertFalse(updatedAt.isBlank());
    }

    private String readField(String field) {
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

    private ResponseEntity<String> postJsonWithAuth(String path, Map<String, Object> body, String apiKey) {
        try {
            String payload = objectMapper.writeValueAsString(body);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (apiKey != null) {
                headers.set(API_KEY_HEADER, apiKey);
            }
            return rest.exchange(path, HttpMethod.POST, new HttpEntity<>(payload, headers), String.class);
        } catch (Exception e) {
            fail("Failed to serialize request: " + e.getMessage());
            return null;
        }
    }

    private String normalizeInput(String value) {
        if (value == null) {
            return null;
        }
        if ("<empty>".equalsIgnoreCase(value.trim()) || "<blank>".equalsIgnoreCase(value.trim())) {
            return "";
        }
        return value;
    }
}

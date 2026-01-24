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
public class RoleSteps {

    private static final String API_KEY_HEADER = "X-Admin-Api-Key";
    private static final String APPLICATIONS_ENDPOINT = "/api/v1/admin/applications";

    private final TestRestTemplate rest;
    private final ObjectMapper objectMapper;
    private final SharedContext sharedContext;

    @Value("${app.admin.api-key}")
    private String validAdminApiKey;

    private String currentApiKey;

    @Before
    public void resetRoleScenarioState() {
        currentApiKey = validAdminApiKey;
    }

    @Given("an application with code {string} exists")
    public void anApplicationWithCodeExists(String applicationCode) {
        currentApiKey = validAdminApiKey;
        Map<String, Object> body = new HashMap<>();
        body.put("applicationCode", applicationCode);
        body.put("name", "Test Application " + applicationCode);
        postJsonWithAuth(APPLICATIONS_ENDPOINT, body, currentApiKey);
    }

    @Given("an application with code {string} exists with system credentials")
    public void anApplicationWithCodeExistsWithSystemCredentials(String applicationCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("applicationCode", applicationCode);
        body.put("name", "Test Application " + applicationCode);
        postJsonWithAuth(APPLICATIONS_ENDPOINT, body, validAdminApiKey);
    }

    @When("I create a role with code {string} and display name {string} in application {string}")
    public void iCreateRoleWithCodeAndDisplayName(String roleCode, String displayName, String applicationCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("roleCode", normalizeInput(roleCode));
        body.put("displayName", normalizeInput(displayName));
        String path = APPLICATIONS_ENDPOINT + "/" + applicationCode + "/roles";
        sharedContext.setLastResponse(postJsonWithAuth(path, body, currentApiKey));
    }

    @When("I create a role with code {string} display name {string} and description {string} in application {string}")
    public void iCreateRoleWithCodeDisplayNameAndDescription(String roleCode, String displayName, String description, String applicationCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("roleCode", normalizeInput(roleCode));
        body.put("displayName", normalizeInput(displayName));
        body.put("description", normalizeInput(description));
        String path = APPLICATIONS_ENDPOINT + "/" + applicationCode + "/roles";
        sharedContext.setLastResponse(postJsonWithAuth(path, body, currentApiKey));
    }

    @When("I create a role with code {string} display name {string} and status {string} in application {string}")
    public void iCreateRoleWithCodeDisplayNameAndStatus(String roleCode, String displayName, String status, String applicationCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("roleCode", normalizeInput(roleCode));
        body.put("displayName", normalizeInput(displayName));
        body.put("status", normalizeInput(status));
        String path = APPLICATIONS_ENDPOINT + "/" + applicationCode + "/roles";
        sharedContext.setLastResponse(postJsonWithAuth(path, body, currentApiKey));
    }

    @When("I create a role without authentication with code {string} and display name {string} in application {string}")
    public void iCreateRoleWithoutAuthentication(String roleCode, String displayName, String applicationCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("roleCode", normalizeInput(roleCode));
        body.put("displayName", normalizeInput(displayName));
        String path = APPLICATIONS_ENDPOINT + "/" + applicationCode + "/roles";
        sharedContext.setLastResponse(postJsonWithAuth(path, body, null));
    }

    @When("I create a role with invalid API key with code {string} and display name {string} in application {string}")
    public void iCreateRoleWithInvalidApiKey(String roleCode, String displayName, String applicationCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("roleCode", normalizeInput(roleCode));
        body.put("displayName", normalizeInput(displayName));
        String path = APPLICATIONS_ENDPOINT + "/" + applicationCode + "/roles";
        sharedContext.setLastResponse(postJsonWithAuth(path, body, "invalid-api-key"));
    }

    @Then("the role code should be {string}")
    public void roleCodeShouldBe(String expected) {
        assertEquals(expected, readField("roleCode"));
    }

    @Then("the role display name should be {string}")
    public void roleDisplayNameShouldBe(String expected) {
        assertEquals(expected, readField("displayName"));
    }

    @Then("the role description should be {string}")
    public void roleDescriptionShouldBe(String expected) {
        assertEquals(expected, readField("description"));
    }

    @Then("the role status should be {string}")
    public void roleStatusShouldBe(String expected) {
        assertEquals(expected, readField("status"));
    }

    @Then("the role should have a valid id")
    public void roleShouldHaveValidId() {
        String id = readField("roleId");
        assertNotNull(id);
        assertFalse(id.isBlank());
        assertDoesNotThrow(() -> java.util.UUID.fromString(id));
    }

    @Then("the role should belong to application {string}")
    public void roleShouldBelongToApplication(String expectedApplicationCode) {
        String applicationCode = readField("applicationCode");
        assertEquals(expectedApplicationCode, applicationCode);
    }

    @Then("the role should have timestamps")
    public void roleShouldHaveTimestamps() {
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

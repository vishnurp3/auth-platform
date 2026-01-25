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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class MembershipSteps {

    private static final String API_KEY_HEADER = "X-Admin-Api-Key";
    private static final String APPLICATIONS_ENDPOINT = "/api/v1/admin/applications";
    private static final String USERS_ENDPOINT = "/api/v1/users";
    private static final String DEFAULT_PASSWORD = "Str0ngPassw0rd!";

    private final TestRestTemplate rest;
    private final ObjectMapper objectMapper;
    private final SharedContext sharedContext;

    @Value("${app.admin.api-key}")
    private String validAdminApiKey;

    private String currentApiKey;
    private int userCounter = 0;

    @Before
    public void resetMembershipScenarioState() {
        currentApiKey = validAdminApiKey;
        userCounter = 0;
        sharedContext.clearStoredValues();
    }

    @Given("a user exists with id stored as {string}")
    public void aUserExistsWithIdStoredAs(String storageKey) {
        currentApiKey = validAdminApiKey;
        String email = "testuser" + (++userCounter) + "_" + System.currentTimeMillis() + "@example.com";
        ResponseEntity<String> response = registerUser(email, DEFAULT_PASSWORD);
        assertEquals(201, response.getStatusCode().value(), "User registration failed");

        String userId = readFieldFromResponse(response, "userId");
        assertNotNull(userId, "userId not found in response");
        sharedContext.storeValue(storageKey, userId);
    }

    @Given("a user exists with id stored as {string} with system credentials")
    public void aUserExistsWithIdStoredAsWithSystemCredentials(String storageKey) {
        String email = "testuser" + (++userCounter) + "_" + System.currentTimeMillis() + "@example.com";
        ResponseEntity<String> response = registerUser(email, DEFAULT_PASSWORD);
        assertEquals(201, response.getStatusCode().value(), "User registration failed");

        String userId = readFieldFromResponse(response, "userId");
        assertNotNull(userId, "userId not found in response");
        sharedContext.storeValue(storageKey, userId);
    }

    @Given("a role with code {string} exists in application {string}")
    public void aRoleExistsInApplication(String roleCode, String applicationCode) {
        currentApiKey = validAdminApiKey;
        Map<String, Object> body = new HashMap<>();
        body.put("roleCode", roleCode);
        body.put("displayName", "Test Role " + roleCode);
        String path = APPLICATIONS_ENDPOINT + "/" + applicationCode + "/roles";
        ResponseEntity<String> response = postJsonWithAuth(path, body, currentApiKey);
        assertEquals(201, response.getStatusCode().value(), "Role creation failed for " + roleCode);
    }

    @Given("a role with code {string} and status {string} exists in application {string}")
    public void aRoleWithStatusExistsInApplication(String roleCode, String status, String applicationCode) {
        currentApiKey = validAdminApiKey;
        Map<String, Object> body = new HashMap<>();
        body.put("roleCode", roleCode);
        body.put("displayName", "Test Role " + roleCode);
        body.put("status", status);
        String path = APPLICATIONS_ENDPOINT + "/" + applicationCode + "/roles";
        ResponseEntity<String> response = postJsonWithAuth(path, body, currentApiKey);
        assertEquals(201, response.getStatusCode().value(), "Role creation failed for " + roleCode);
    }

    @Given("the application {string} is disabled")
    public void theApplicationIsDisabled(String applicationCode) {
        currentApiKey = validAdminApiKey;
        Map<String, Object> body = new HashMap<>();
        body.put("status", "DISABLED");
        String path = APPLICATIONS_ENDPOINT + "/" + applicationCode + "/status";
        ResponseEntity<String> response = patchJsonWithAuth(path, body, currentApiKey);
        assertEquals(200, response.getStatusCode().value(), "Failed to disable application");
    }

    @When("I create a membership for user {string} in application {string} with roles {string}")
    public void iCreateMembershipWithRoles(String userStorageKey, String applicationCode, String roleCodesStr) {
        String userId = sharedContext.getValue(userStorageKey);
        assertNotNull(userId, "User ID not found for key: " + userStorageKey);

        List<String> roleCodes = Arrays.asList(roleCodesStr.split(","));
        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("roleCodes", roleCodes);

        String path = APPLICATIONS_ENDPOINT + "/" + applicationCode + "/memberships";
        sharedContext.setLastResponse(postJsonWithAuth(path, body, currentApiKey));
    }

    @When("I create a membership for user {string} in application {string} with no roles")
    public void iCreateMembershipWithNoRoles(String userStorageKey, String applicationCode) {
        String userId = sharedContext.getValue(userStorageKey);
        assertNotNull(userId, "User ID not found for key: " + userStorageKey);

        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("roleCodes", List.of());

        String path = APPLICATIONS_ENDPOINT + "/" + applicationCode + "/memberships";
        sharedContext.setLastResponse(postJsonWithAuth(path, body, currentApiKey));
    }

    @When("I create a membership for user {string} in application {string} with status {string}")
    public void iCreateMembershipWithStatus(String userStorageKey, String applicationCode, String status) {
        String userId = sharedContext.getValue(userStorageKey);
        assertNotNull(userId, "User ID not found for key: " + userStorageKey);

        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("roleCodes", List.of());
        body.put("status", status);

        String path = APPLICATIONS_ENDPOINT + "/" + applicationCode + "/memberships";
        sharedContext.setLastResponse(postJsonWithAuth(path, body, currentApiKey));
    }

    @When("I create a membership for non-existent user in application {string} with no roles")
    public void iCreateMembershipForNonExistentUser(String applicationCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("userId", UUID.randomUUID().toString());
        body.put("roleCodes", List.of());

        String path = APPLICATIONS_ENDPOINT + "/" + applicationCode + "/memberships";
        sharedContext.setLastResponse(postJsonWithAuth(path, body, currentApiKey));
    }

    @When("I create a membership without authentication for user {string} in application {string}")
    public void iCreateMembershipWithoutAuth(String userStorageKey, String applicationCode) {
        String userId = sharedContext.getValue(userStorageKey);
        assertNotNull(userId, "User ID not found for key: " + userStorageKey);

        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("roleCodes", List.of());

        String path = APPLICATIONS_ENDPOINT + "/" + applicationCode + "/memberships";
        sharedContext.setLastResponse(postJsonWithAuth(path, body, null));
    }

    @When("I create a membership with invalid API key for user {string} in application {string}")
    public void iCreateMembershipWithInvalidApiKey(String userStorageKey, String applicationCode) {
        String userId = sharedContext.getValue(userStorageKey);
        assertNotNull(userId, "User ID not found for key: " + userStorageKey);

        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("roleCodes", List.of());

        String path = APPLICATIONS_ENDPOINT + "/" + applicationCode + "/memberships";
        sharedContext.setLastResponse(postJsonWithAuth(path, body, "invalid-api-key"));
    }

    @Then("the membership should have a valid id")
    public void membershipShouldHaveValidId() {
        String id = readField("membershipId");
        assertNotNull(id);
        assertFalse(id.isBlank());
        assertDoesNotThrow(() -> UUID.fromString(id));
    }

    @Then("the membership user id should match {string}")
    public void membershipUserIdShouldMatch(String userStorageKey) {
        String expectedUserId = sharedContext.getValue(userStorageKey);
        String actualUserId = readField("userId");
        assertEquals(expectedUserId, actualUserId);
    }

    @Then("the membership application code should be {string}")
    public void membershipApplicationCodeShouldBe(String expected) {
        assertEquals(expected, readField("applicationCode"));
    }

    @Then("the membership status should be {string}")
    public void membershipStatusShouldBe(String expected) {
        assertEquals(expected, readField("status"));
    }

    @Then("the membership should have {int} assigned roles")
    public void membershipShouldHaveAssignedRoles(int expectedCount) {
        ResponseEntity<String> response = sharedContext.getLastResponse();
        assertNotNull(response, "No response captured");
        String body = response.getBody();
        assertNotNull(body, "No response body");
        try {
            Map<String, Object> payload = objectMapper.readValue(body, new TypeReference<>() {
            });
            @SuppressWarnings("unchecked")
            List<Object> assignedRoles = (List<Object>) payload.get("assignedRoles");
            assertNotNull(assignedRoles, "assignedRoles not found in response");
            assertEquals(expectedCount, assignedRoles.size());
        } catch (Exception e) {
            fail("Failed to parse response body: " + e.getMessage());
        }
    }

    @Then("the membership should have timestamps")
    public void membershipShouldHaveTimestamps() {
        String createdAt = readField("createdAt");
        String updatedAt = readField("updatedAt");
        assertNotNull(createdAt);
        assertNotNull(updatedAt);
        assertFalse(createdAt.isBlank());
        assertFalse(updatedAt.isBlank());
    }

    private ResponseEntity<String> registerUser(String email, String password) {
        Map<String, Object> body = Map.of("email", email, "password", password);
        try {
            String payload = objectMapper.writeValueAsString(body);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return rest.exchange(USERS_ENDPOINT + "/register", HttpMethod.POST,
                    new HttpEntity<>(payload, headers), String.class);
        } catch (Exception e) {
            fail("Failed to register user: " + e.getMessage());
            return null;
        }
    }

    private String readField(String field) {
        ResponseEntity<String> response = sharedContext.getLastResponse();
        return readFieldFromResponse(response, field);
    }

    private String readFieldFromResponse(ResponseEntity<String> response, String field) {
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

    private ResponseEntity<String> patchJsonWithAuth(String path, Map<String, Object> body, String apiKey) {
        try {
            String payload = objectMapper.writeValueAsString(body);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (apiKey != null) {
                headers.set(API_KEY_HEADER, apiKey);
            }
            return rest.exchange(path, HttpMethod.PATCH, new HttpEntity<>(payload, headers), String.class);
        } catch (Exception e) {
            fail("Failed to serialize request: " + e.getMessage());
            return null;
        }
    }
}

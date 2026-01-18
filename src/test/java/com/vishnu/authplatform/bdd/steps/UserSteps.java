package com.vishnu.authplatform.bdd.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishnu.authplatform.bdd.TestEmailSender;
import com.vishnu.authplatform.identity.domain.VerificationToken;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class UserSteps {

    private static final String DEFAULT_PASSWORD = "Str0ngPassw0rd!";
    private static final Duration EMAIL_TIMEOUT = Duration.ofSeconds(5);

    private final TestRestTemplate rest;
    private final ObjectMapper objectMapper;
    private final TestEmailSender emailSender;

    private ResponseEntity<String> lastResponse;
    private ResponseEntity<String> resendResponse;
    private String verificationToken;

    @Before
    public void resetScenarioState() {
        emailSender.clear();
        lastResponse = null;
        resendResponse = null;
        verificationToken = null;
    }

    @Given("the system is running")
    public void theSystemIsRunning() {
    }

    @Given("I am a registered user {string}")
    public void iAmARegisteredUser(String email) {
        register(email, DEFAULT_PASSWORD);
        assertEquals(201, lastResponse.getStatusCode().value());
        awaitEmailCount(1, EMAIL_TIMEOUT);
    }

    @When("I register with email {string} and password {string}")
    public void iRegister(String email, String password) {
        register(normalizeInput(email), normalizeInput(password));
    }

    @Then("the response status should be {int}")
    public void responseStatusShouldBe(int code) {
        assertNotNull(lastResponse, "No response captured");
        assertEquals(code, lastResponse.getStatusCode().value());
    }

    @Then("the user status should be {string}")
    public void userStatusShouldBe(String expected) {
        assertEquals(expected, readField(lastResponse, "status"));
    }

    @Then("the verified user status should be {string}")
    public void verifiedUserStatusShouldBe(String expected) {
        assertEquals(expected, readField(lastResponse, "status"));
    }

    @Then("the email outbox size should be {int}")
    public void emailOutboxSizeShouldBe(int expected) {
        awaitEmailCount(expected, EMAIL_TIMEOUT);
    }

    @Then("the last verification email should be sent to {string}")
    public void lastVerificationEmailShouldBeSentTo(String expectedTo) {
        TestEmailSender.SentEmail email = awaitLastEmail(EMAIL_TIMEOUT);
        assertNotNull(email, "Expected verification email to be sent");
        assertEquals(expectedTo, email.to());
        assertTrue(email.link().contains("verify-email?token="));
    }

    @When("I have extracted the verification token from the last email")
    public void extractTokenFromLastEmail() {
        TestEmailSender.SentEmail email = awaitLastEmail(EMAIL_TIMEOUT);
        assertNotNull(email, "No email captured");
        verificationToken = extractToken(email.link());
        assertFalse(verificationToken.isBlank());
    }

    @When("I verify the email using the token")
    public void verifyEmailUsingToken() {
        assertNotNull(verificationToken, "No verification token captured");
        lastResponse = rest.getForEntity("/api/v1/users/verify-email?token=" + verificationToken, String.class);
    }

    @When("I verify the email using token {string}")
    public void verifyEmailUsingToken(String token) {
        String normalized = normalizeInput(token);
        lastResponse = rest.getForEntity("/api/v1/users/verify-email?token=" + normalized, String.class);
    }

    @When("I verify the email using a valid but unknown token")
    public void verifyEmailUsingUnknownToken() {
        VerificationToken token = VerificationToken.of(UUID.randomUUID(), "unknown-secret");
        lastResponse = rest.getForEntity("/api/v1/users/verify-email?token=" + token.encode(), String.class);
    }

    @When("I request a verification email resend for {string}")
    public void requestResend(String email) {
        resendResponse = postJson("/api/v1/users/resend-verification", Map.of("email", normalizeInput(email)));
    }

    @Then("the resend response status should be {int}")
    public void resendStatusShouldBe(int code) {
        assertNotNull(resendResponse, "No resend response captured");
        assertEquals(code, resendResponse.getStatusCode().value());
    }

    @When("I wait {string} milliseconds")
    public void waitMilliseconds(String millis) throws InterruptedException {
        Thread.sleep(Long.parseLong(millis));
    }

    private void register(String email, String password) {
        Map<String, Object> body = Map.of("email", email, "password", password);
        lastResponse = postJson("/api/v1/users/register", body);
    }

    private String readField(ResponseEntity<String> response, String field) {
        assertNotNull(response, "No response captured");
        String body = response.getBody();
        assertNotNull(body, "No response body");
        try {
            Map<String, Object> payload = objectMapper.readValue(body, new TypeReference<>() {
            });
            Object value = payload.get(field);
            assertNotNull(value, "Missing field: " + field);
            return String.valueOf(value);
        } catch (Exception e) {
            fail("Failed to parse response body: " + e.getMessage());
            return null;
        }
    }

    private TestEmailSender.SentEmail awaitLastEmail(Duration timeout) {
        Instant end = Instant.now().plus(timeout);
        while (Instant.now().isBefore(end)) {
            TestEmailSender.SentEmail email = emailSender.lastEmail();
            if (email != null) {
                return email;
            }
            sleepBriefly();
        }
        return null;
    }

    private void awaitEmailCount(int expected, Duration timeout) {
        Instant end = Instant.now().plus(timeout);
        while (Instant.now().isBefore(end)) {
            int count = emailSender.count();
            if (count > expected) {
                fail("Expected at most " + expected + " emails, but found " + count);
            }
            if (count == expected) {
                sleepBriefly();
                if (emailSender.count() == expected) {
                    return;
                }
            }
            sleepBriefly();
        }
        fail("Expected email count " + expected + " but found " + emailSender.count());
    }

    private String extractToken(String link) {
        int idx = link.indexOf("token=");
        assertTrue(idx >= 0, "Token not found in link");
        return link.substring(idx + "token=".length());
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private ResponseEntity<String> postJson(String path, Map<String, Object> body) {
        try {
            String payload = objectMapper.writeValueAsString(body);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
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

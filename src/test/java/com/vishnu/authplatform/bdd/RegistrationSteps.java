package com.vishnu.authplatform.bdd;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class RegistrationSteps {

    private final TestRestTemplate rest;
    private final InMemoryEmailSender emailSender;

    private ResponseEntity<Map> lastResponse;
    private String extractedToken;

    @Given("the system is running")
    public void theSystemIsRunning() {
    }

    @When("I register with email {string} and password {string}")
    public void iRegister(String email, String password) {
        Map<String, Object> body = Map.of("email", email, "password", password);
        lastResponse = rest.postForEntity("/api/v1/users/register", body, Map.class);
    }

    @Then("the response status should be {int}")
    public void statusShouldBe(int code) {
        assertNotNull(lastResponse);
        assertEquals(code, lastResponse.getStatusCode().value());
    }

    @Then("the user status should be {string}")
    public void userStatusShouldBe(String expected) {
        assertNotNull(lastResponse.getBody());
        assertEquals(expected, String.valueOf(lastResponse.getBody().get("status")));
    }

    @Then("a verification email should be sent to {string}")
    public void emailShouldBeSentTo(String expectedTo) {
        InMemoryEmailSender.SentEmail email = awaitEmail(Duration.ofSeconds(3));
        assertNotNull(email, "Expected verification email to be sent");
        assertEquals(expectedTo, email.to());
        assertTrue(email.link().contains("verify-email?token="));
    }

    @When("I extract the verification token from the sent email")
    public void extractToken() {
        InMemoryEmailSender.SentEmail email = awaitEmail(Duration.ofSeconds(3));
        assertNotNull(email, "No email captured");
        String link = email.link();
        int idx = link.indexOf("token=");
        assertTrue(idx > 0, "Token not found in link");
        extractedToken = link.substring(idx + "token=".length());
        assertFalse(extractedToken.isBlank());
    }

    @When("I verify the email using that token")
    public void verifyEmail() {
        assertNotNull(extractedToken);
        lastResponse = rest.getForEntity("/api/v1/users/verify-email?token=" + extractedToken, Map.class);
    }

    @Then("the verified user status should be {string}")
    public void verifiedStatusShouldBe(String expected) {
        assertNotNull(lastResponse.getBody());
        assertEquals(expected, String.valueOf(lastResponse.getBody().get("status")));
    }

    private InMemoryEmailSender.SentEmail awaitEmail(Duration timeout) {
        Instant end = Instant.now().plus(timeout);
        while (Instant.now().isBefore(end)) {
            InMemoryEmailSender.SentEmail email = emailSender.lastEmail();
            if (email != null) return email;
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        return null;
    }
}

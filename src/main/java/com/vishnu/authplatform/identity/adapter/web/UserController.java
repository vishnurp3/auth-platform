package com.vishnu.authplatform.identity.adapter.web;

import com.vishnu.authplatform.identity.application.RegisterUserUseCase;
import com.vishnu.authplatform.identity.application.ResendVerificationEmailUseCase;
import com.vishnu.authplatform.identity.application.VerifyEmailUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user registration and email verification")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final ResendVerificationEmailUseCase resendVerificationEmailUseCase;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with pending verification status. A verification email will be sent to the provided email address."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., invalid email format, weak password)"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        RegisterUserUseCase.Result result =
                registerUserUseCase.execute(new RegisterUserUseCase.Command(req.email(), req.password()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(result.userId(), result.email(), result.status().name()));
    }

    @Operation(
            summary = "Verify email address (via query parameter)",
            description = "Verifies the user's email address using the token sent via email. This activates the user account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully",
                    content = @Content(schema = @Schema(implementation = VerifyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token format"),
            @ApiResponse(responseCode = "409", description = "Token expired, already used, or invalid")
    })
    @GetMapping("/verify-email")
    public ResponseEntity<VerifyResponse> verifyByQuery(@RequestParam("token") String token) {
        VerifyEmailUseCase.Result result = verifyEmailUseCase.execute(new VerifyEmailUseCase.Command(token));
        return ResponseEntity.ok(new VerifyResponse(result.userId(), result.status().name()));
    }

    @Operation(
            summary = "Verify email address (via request body)",
            description = "Verifies the user's email address using the token sent via email. This activates the user account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully",
                    content = @Content(schema = @Schema(implementation = VerifyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token format"),
            @ApiResponse(responseCode = "409", description = "Token expired, already used, or invalid")
    })
    @PostMapping("/verify-email")
    public ResponseEntity<VerifyResponse> verifyByBody(@Valid @RequestBody VerifyRequest req) {
        VerifyEmailUseCase.Result result = verifyEmailUseCase.execute(new VerifyEmailUseCase.Command(req.token()));
        return ResponseEntity.ok(new VerifyResponse(result.userId(), result.status().name()));
    }

    @Operation(
            summary = "Resend verification email",
            description = "Sends a new verification email to the user. Subject to rate limiting."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Verification email resend request accepted"),
            @ApiResponse(responseCode = "400", description = "Invalid email format"),
            @ApiResponse(responseCode = "409", description = "Rate limit exceeded or user not eligible for resend")
    })
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@Valid @RequestBody ResendVerificationRequest req) {
        resendVerificationEmailUseCase.execute(new ResendVerificationEmailUseCase.Command(req.email()));
        return ResponseEntity.accepted().build();
    }

    @Schema(description = "User registration request")
    public record RegisterRequest(
            @Schema(description = "User's email address", example = "user@example.com")
            @NotBlank String email,
            @Schema(description = "Password (minimum 10 characters)", example = "SecureP@ss123")
            @NotBlank String password
    ) {
    }

    @Schema(description = "User registration response")
    public record RegisterResponse(
            @Schema(description = "Unique user identifier") UUID userId,
            @Schema(description = "User's email address") String email,
            @Schema(description = "User account status", example = "PENDING_VERIFICATION") String status
    ) {
    }

    @Schema(description = "Email verification request")
    public record VerifyRequest(
            @Schema(description = "Verification token from email")
            @NotBlank String token
    ) {
    }

    @Schema(description = "Email verification response")
    public record VerifyResponse(
            @Schema(description = "Unique user identifier") UUID userId,
            @Schema(description = "Updated user account status", example = "ACTIVE") String status
    ) {
    }

    @Schema(description = "Resend verification email request")
    public record ResendVerificationRequest(
            @Schema(description = "User's email address", example = "user@example.com")
            @NotBlank String email
    ) {
    }
}

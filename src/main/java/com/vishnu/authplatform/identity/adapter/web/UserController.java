package com.vishnu.authplatform.identity.adapter.web;

import com.vishnu.authplatform.identity.adapter.web.request.RegisterRequest;
import com.vishnu.authplatform.identity.adapter.web.request.ResendVerificationRequest;
import com.vishnu.authplatform.identity.adapter.web.request.VerifyEmailRequest;
import com.vishnu.authplatform.identity.adapter.web.response.RegisterResponse;
import com.vishnu.authplatform.identity.adapter.web.response.VerifyEmailResponse;
import com.vishnu.authplatform.identity.application.RegisterUserUseCase;
import com.vishnu.authplatform.identity.application.ResendVerificationEmailUseCase;
import com.vishnu.authplatform.identity.application.VerifyEmailUseCase;
import com.vishnu.authplatform.identity.application.command.RegisterUserCommand;
import com.vishnu.authplatform.identity.application.command.ResendVerificationEmailCommand;
import com.vishnu.authplatform.identity.application.command.VerifyEmailCommand;
import com.vishnu.authplatform.identity.application.result.RegisterUserResult;
import com.vishnu.authplatform.identity.application.result.VerifyEmailResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        RegisterUserResult result =
                registerUserUseCase.execute(new RegisterUserCommand(req.email(), req.password()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(result.userId(), result.email(), result.status().name()));
    }

    @Operation(
            summary = "Verify email address (via query parameter)",
            description = "Verifies the user's email address using the token sent via email. This activates the user account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully",
                    content = @Content(schema = @Schema(implementation = VerifyEmailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token format"),
            @ApiResponse(responseCode = "409", description = "Token expired, already used, or invalid")
    })
    @GetMapping("/verify-email")
    public ResponseEntity<VerifyEmailResponse> verifyByQuery(@RequestParam("token") String token) {
        VerifyEmailResult result = verifyEmailUseCase.execute(new VerifyEmailCommand(token));
        return ResponseEntity.ok(new VerifyEmailResponse(result.userId(), result.status().name()));
    }

    @Operation(
            summary = "Verify email address (via request body)",
            description = "Verifies the user's email address using the token sent via email. This activates the user account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully",
                    content = @Content(schema = @Schema(implementation = VerifyEmailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token format"),
            @ApiResponse(responseCode = "409", description = "Token expired, already used, or invalid")
    })
    @PostMapping("/verify-email")
    public ResponseEntity<VerifyEmailResponse> verifyByBody(@Valid @RequestBody VerifyEmailRequest req) {
        VerifyEmailResult result = verifyEmailUseCase.execute(new VerifyEmailCommand(req.token()));
        return ResponseEntity.ok(new VerifyEmailResponse(result.userId(), result.status().name()));
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
        resendVerificationEmailUseCase.execute(new ResendVerificationEmailCommand(req.email()));
        return ResponseEntity.accepted().build();
    }
}

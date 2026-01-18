package com.vishnu.authplatform.identity.adapter.web;

import com.vishnu.authplatform.identity.application.RegisterUserUseCase;
import com.vishnu.authplatform.identity.application.ResendVerificationEmailUseCase;
import com.vishnu.authplatform.identity.application.VerifyEmailUseCase;
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
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final ResendVerificationEmailUseCase resendVerificationEmailUseCase;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        RegisterUserUseCase.Result result =
                registerUserUseCase.execute(new RegisterUserUseCase.Command(req.email(), req.password()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(result.userId(), result.email(), result.status().name()));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<VerifyResponse> verifyByQuery(@RequestParam("token") String token) {
        VerifyEmailUseCase.Result result = verifyEmailUseCase.execute(new VerifyEmailUseCase.Command(token));
        return ResponseEntity.ok(new VerifyResponse(result.userId(), result.status().name()));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<VerifyResponse> verifyByBody(@Valid @RequestBody VerifyRequest req) {
        VerifyEmailUseCase.Result result = verifyEmailUseCase.execute(new VerifyEmailUseCase.Command(req.token()));
        return ResponseEntity.ok(new VerifyResponse(result.userId(), result.status().name()));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@Valid @RequestBody ResendVerificationRequest req) {
        resendVerificationEmailUseCase.execute(new ResendVerificationEmailUseCase.Command(req.email()));
        return ResponseEntity.accepted().build();
    }

    public record RegisterRequest(@NotBlank String email, @NotBlank String password) {
    }

    public record RegisterResponse(UUID userId, String email, String status) {
    }

    public record VerifyRequest(@NotBlank String token) {
    }

    public record VerifyResponse(UUID userId, String status) {
    }

    public record ResendVerificationRequest(@NotBlank String email) {
    }
}

package com.vishnu.authplatform.identity.application.result;

import com.vishnu.authplatform.identity.domain.EmailVerificationToken;
import com.vishnu.authplatform.identity.domain.VerificationToken;

public record IssuedTokenPair(EmailVerificationToken token, VerificationToken verificationToken) {
}

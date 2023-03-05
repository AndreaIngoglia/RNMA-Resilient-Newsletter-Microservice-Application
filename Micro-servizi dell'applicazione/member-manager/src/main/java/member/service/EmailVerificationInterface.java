package member.service;

public interface EmailVerificationInterface {
    void checkIdentity(String email, String token, String confirmationId);
}

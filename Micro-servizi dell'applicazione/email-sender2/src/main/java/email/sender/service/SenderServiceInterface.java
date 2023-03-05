package email.sender.service;

public interface SenderServiceInterface {
    void sendEmail(String associateEmail, String confirmationUrl, String correlationId);
}

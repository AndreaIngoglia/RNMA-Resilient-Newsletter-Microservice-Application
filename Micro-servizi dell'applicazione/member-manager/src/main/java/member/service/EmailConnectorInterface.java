package member.service;

public interface EmailConnectorInterface {
    void connect(String email, String confirmationToken, String correlationId);
}

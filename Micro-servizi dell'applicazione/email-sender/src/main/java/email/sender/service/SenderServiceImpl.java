package email.sender.service;

import email.sender.exception.SenderException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Getter
public class SenderServiceImpl implements SenderServiceInterface {
    private final JavaMailSender javaMailSender;
    private static final Logger LOG = LoggerFactory.getLogger(SenderServiceImpl.class);
    private final static String INSTANCE_ID = UUID.randomUUID().toString();

    public void sendEmail(String email, String confirmationUrl, String correlationId) {
        try {
            LOG.info("USER-MANAGER successfully connected to EMAIL-SENDER.");
            LOG.info("The system is creating the validation email.");
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(email);
            mail.setFrom("newsletter.rnma@gmail.com");
            mail.setText(generateConfirmationMessage(confirmationUrl));
            mail.setSubject("Identity confirmation");
            javaMailSender.send(mail);
            LOG.info("The validation email has been successfully sent.");

        } catch (Exception exception) {
            LOG.info("Google email services are currently down. Your request cannot be completed");
            exception.printStackTrace();
            throw new SenderException();
        }
    }

    private String generateConfirmationMessage(String confirmationUrl) {
        String body = "Thanks for your registration. Click on this link to confirm your identity: " + confirmationUrl;
        String instanceMarker = "\nEmail automatically sent by service with id: " + INSTANCE_ID;
        return body + instanceMarker;
    }
}
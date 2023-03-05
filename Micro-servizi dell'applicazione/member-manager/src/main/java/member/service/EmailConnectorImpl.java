package member.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import member.utils.ConfirmationUrl;

@Service
@AllArgsConstructor
public class EmailConnectorImpl implements EmailConnectorInterface {
    private final RestTemplate restTemplate;
    private static final Logger LOG = LoggerFactory.getLogger(EmailConnectorImpl.class);

    public void connect(String email, String confirmationToken, String correlationId){

        ConfirmationUrl url = new ConfirmationUrl(generateConfirmationUrl(email , confirmationToken));
        HttpEntity<ConfirmationUrl> httpEntity = new HttpEntity<>(url);
        LOG.info("USER-MANAGER is trying to connect to EMAIL-SENDER.");
        restTemplate.postForEntity("http://EMAIL-SENDER/send/" + email, httpEntity, String.class);
    }

    private String generateConfirmationUrl(String email , String confirmationToken){
        return String.format("http://localhost:8080/members/%s?token=%s" , email, confirmationToken);
    }
}

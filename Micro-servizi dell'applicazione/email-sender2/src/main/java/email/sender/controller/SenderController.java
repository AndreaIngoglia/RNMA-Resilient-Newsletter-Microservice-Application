package email.sender.controller;

import brave.Tracer;
import email.sender.utils.ConfirmationUrl;
import email.sender.exception.SenderException;
import email.sender.service.SenderServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class SenderController {
    private final SenderServiceImpl sender;
    private Tracer tracer;

    @PostMapping(path = {"/send/{email}"})
    public ResponseEntity<String> sendEmail(@PathVariable String email, @RequestBody ConfirmationUrl url){
        String correlationId = getCorrelationId();
        sender.sendEmail(email , url.getConfirmationUrl(), correlationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler({SenderException.class})
    public ResponseEntity<String> handleException(SenderException e) {
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    private String getCorrelationId(){
        return tracer.currentSpan().context().traceIdString();
    }
}

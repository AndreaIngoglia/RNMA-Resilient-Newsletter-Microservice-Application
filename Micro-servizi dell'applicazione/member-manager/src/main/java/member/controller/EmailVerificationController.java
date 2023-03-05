package member.controller;

import brave.Tracer;
import lombok.AllArgsConstructor;
import member.error.MemberManagerException;
import member.error.TracedResponse;
import member.service.EmailVerificationServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class EmailVerificationController {
    private final EmailVerificationServiceImpl verificationService;
    private Tracer tracer;

    @GetMapping(path = "/members/{email}")
    public ResponseEntity<TracedResponse> checkIdentity(@PathVariable String email, @RequestParam String token){
        String correlationId = generateCorrelationId();
        verificationService.checkIdentity(email, token, correlationId);
        return new ResponseEntity<>(new TracedResponse("Your identity has been successfully verified.", correlationId), HttpStatus.OK);
    }

    @ExceptionHandler({MemberManagerException.class})
    public ResponseEntity<TracedResponse> handleException(MemberManagerException e) {
        return new ResponseEntity<>(new TracedResponse(e.getMessage(), e.getCorrelationId()), HttpStatus.BAD_REQUEST);
    }
    private String generateCorrelationId(){
        return tracer.currentSpan().context().traceIdString();
    }
}

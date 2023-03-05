package member.controller;

import brave.Tracer;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.AllArgsConstructor;
import member.database.entity.MemberEntity;
import member.error.MemberManagerException;
import member.error.TracedResponse;
import member.service.MemberServiceImpl;
import member.utils.InputInformation;
import member.utils.MemberInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class MemberController {
    private final MemberServiceImpl memberService;
    private Tracer tracer;
    private static final Logger LOG = LoggerFactory.getLogger(MemberController.class);

    @PostMapping(path = {"/members/{email}"})
    @RateLimiter(name = "rateLimiterUserManager", fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<TracedResponse> registerMember(@PathVariable String email, @RequestBody  InputInformation input){
        /*Get correlationId from Sleuth for system observability*/
        String correlationId = generateCorrelationId();
        MemberInformation member = new MemberInformation(input.getName(), input.getSurname(), email);
        memberService.registerMember(member, correlationId);
        return new ResponseEntity<>(new TracedResponse("User has been correctly registered.", correlationId), HttpStatus.OK);
    }

    @GetMapping(path = {"/members"})
    public ResponseEntity<List<MemberEntity>> getMembers(){
        String correlationId = generateCorrelationId();
        List<MemberEntity> members = memberService.getMembers(correlationId);
        return new ResponseEntity<>(members , HttpStatus.OK);
    }

    @DeleteMapping(path = {"/members/{email}"})
    public ResponseEntity<TracedResponse> deleteMember(@PathVariable String email, @RequestHeader String confirmationKey){
        String correlationId = generateCorrelationId();
        memberService.deleteMember(email, confirmationKey, correlationId);
        return new ResponseEntity<>(new TracedResponse("User has been successfully deleted.", correlationId), HttpStatus.OK);
    }

    /**
     * @Warning DO NOT use this endpoint: PUT /members/{email} to change a member's email.
     * */
    @PutMapping(path = {"/members/{email}"})
    public ResponseEntity<TracedResponse> updateMember(@RequestBody InputInformation input, @PathVariable String email){
        String correlationId = generateCorrelationId();
        MemberInformation member = new MemberInformation(input.getName(), input.getSurname(), email);
        memberService.updateMember(member, correlationId);
        return new ResponseEntity<>(new TracedResponse("User information has been successfully updated.", correlationId), HttpStatus.OK);
    }

    @GetMapping(path = {"/timeout"})
    public void slowEndpoint() throws InterruptedException {
        Thread.sleep(70000);
    }

    @ExceptionHandler({MemberManagerException.class})
    public ResponseEntity<TracedResponse> handleException(MemberManagerException e) {
        return new ResponseEntity<>(new TracedResponse(e.getMessage() , e.getCorrelationId()), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<TracedResponse> rateLimiterFallback(RequestNotPermitted e){
        LOG.info("The system is receiving too many requests, the rate limiter tripped.");
        return new ResponseEntity<>(new TracedResponse("The system is receiving too many requests, please wait." , generateCorrelationId()), HttpStatus.TOO_MANY_REQUESTS);
    }
    private String generateCorrelationId(){return tracer.currentSpan().context().traceIdString();}
}
package associate.manager.api.gateway.fallback.controller;

import associate.manager.api.gateway.fallback.errors.CircuitBreakerResponse;
import associate.manager.api.gateway.fallback.errors.FallbackMessages;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class FallbackController {
    @RequestMapping(path = {"/fallback"})
    public ResponseEntity<CircuitBreakerResponse> associateManagerFallback(){
        return new ResponseEntity<>(new CircuitBreakerResponse(FallbackMessages.CIRCUIT_BREAKER_MESSAGE.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

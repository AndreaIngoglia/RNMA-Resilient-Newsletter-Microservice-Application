package associate.manager.api.gateway.fallback.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FallbackMessages {
    CIRCUIT_BREAKER_MESSAGE("Circuit Breaker: The service is actually unavailable. Please, contact our development team for further information.");

    private final String message;
}
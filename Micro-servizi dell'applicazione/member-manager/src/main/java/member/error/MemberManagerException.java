package member.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberManagerException extends RuntimeException{
    private final String message;
    private final String correlationId;
}

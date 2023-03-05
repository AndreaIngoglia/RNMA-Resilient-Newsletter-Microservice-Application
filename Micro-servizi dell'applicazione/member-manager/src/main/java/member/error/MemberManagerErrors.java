package member.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberManagerErrors {
    VALIDATION_ERROR("An error occurred during your identity verification."),
    NO_MEMBER_TO_VALIDATE("The member your are trying to validate is not registered in our systems. He may have deleted himself."),
    MEMBER_IS_ALREADY_VALIDATED("This member has already validated his identity."),
    BLANK_EMAIL("Your email cannot be blank."),
    BLANK_NAME("Your name cannot be blank."),
    BLANK_SURNAME("Your surname cannot be blank."),
    MEMBERS_EXISTS("The provided email is already associated with an existing user."),
    NO_MEMBER_FOUND("This research produced no result."),
    NO_MEMBER_TO_DELETE("No member has been found with the provided email."),
    INVALID_CONFIRMATION_KEY("The provided confirmation key is not valid. Operation has not been completed."),
    NO_MEMBER_TO_UPDATE("No registered associates have been found with the provided e-mail. Please, retry.");

    private final String errorMessage;
}

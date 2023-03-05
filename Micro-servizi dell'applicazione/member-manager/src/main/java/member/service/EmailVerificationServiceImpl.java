package member.service;

import lombok.AllArgsConstructor;
import member.database.entity.MemberEntity;
import member.database.repository.ConfirmationRepository;
import member.database.repository.MemberRepository;
import member.error.MemberManagerException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static member.error.MemberManagerErrors.*;

@Service
@AllArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationInterface {
    private ConfirmationRepository confirmationRepository;
    private MemberRepository memberRepository;
    private static final Logger LOG = LoggerFactory.getLogger(EmailVerificationServiceImpl.class);

    public void checkIdentity(String email, String token, String correlationId){
        LOG.info("User with email {} is trying to validate his identity.", email);
        boolean isEntryValid = confirmationRepository.existsByEmailAndConfirmationToken(email, token);

        if(!isEntryValid){
            LOG.info("The provided token:{} does not match with email:{}. Validation failed.", token, email);
            throw new MemberManagerException(VALIDATION_ERROR.getErrorMessage(), correlationId);
        }

        MemberEntity member = memberRepository.findById(email)
                .orElseThrow(()->{
                    LOG.info("The member your are trying to validate is not registered in our systems. He may have deleted himself.");
                    throw new MemberManagerException(NO_MEMBER_TO_VALIDATE.getErrorMessage(), correlationId);
                });

        if (StringUtils.equalsIgnoreCase("Y", member.getIsIdentityConfirmed())){
            LOG.info("This member has already validated his identity.");
            throw new MemberManagerException(MEMBER_IS_ALREADY_VALIDATED.getErrorMessage(), correlationId);
        }

        member.setIsIdentityConfirmed("Y");
        memberRepository.save(member);
        LOG.info("User successfully validated.");
    }
}

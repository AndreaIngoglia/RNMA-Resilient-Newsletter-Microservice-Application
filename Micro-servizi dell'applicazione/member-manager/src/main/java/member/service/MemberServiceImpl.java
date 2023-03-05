package member.service;

import lombok.AllArgsConstructor;
import member.database.entity.ConfirmationEntity;
import member.database.repository.ConfirmationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import member.database.entity.MemberEntity;
import member.database.repository.MemberRepository;
import member.error.MemberManagerErrors;
import member.error.MemberManagerException;
import member.utils.MemberInformation;

import java.util.List;
import java.util.UUID;

import static member.error.MemberManagerErrors.*;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberServiceInterface {
    private final MemberRepository memberRepository;
    private final ConfirmationRepository confirmationRepository;
    private final EmailConnectorImpl connector;
    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);

    public MemberEntity registerMember(MemberInformation member, String correlationId) {
        String email = member.getEmail();
        LOG.info("Registration of user with email {} started." , email);

        boolean isMemberRegistered = memberRepository.existsById(email);
        if(isMemberRegistered){
            LOG.info("User with email {} already exists.", email);
            throw new MemberManagerException(MEMBERS_EXISTS.getErrorMessage(), correlationId);
        }

        validateInputInformation(member, correlationId, true);
        /* If input is valid, then send verification email.*/

        String confirmationToken = UUID.randomUUID().toString();
        /*If connection to the Email-Service registration stops any data is stored.*/
        connector.connect(email ,confirmationToken, correlationId);

        /*If connection succeeds and the verification email is sent, the system stores: the email and the verification token*/
        confirmationRepository.save(new ConfirmationEntity(email , confirmationToken));

        return memberRepository.save(mapMember(member));
    }

    public List<MemberEntity> getMembers(String correlationId){
        List<MemberEntity> members = memberRepository.findAll();

        if(members.isEmpty()){
            LOG.info("No one has subscribed to the newsletter. No data is returned.");
            throw new MemberManagerException(MemberManagerErrors.NO_MEMBER_FOUND.getErrorMessage(), correlationId);
        }
        LOG.info("All the subscribed members have been returned.");
        return members;
    }

    public MemberEntity deleteMember(String email, String safeKey, String correlationId){
        LOG.info("Member with email {} is trying to unsubscribe from the newsletter.", email);

        MemberEntity member = memberRepository.findById(email)
                .orElseThrow((() ->  {
                    LOG.info("No member with email {} can be unsubscribed from the newsletter.", email);
                    return new MemberManagerException(NO_MEMBER_TO_DELETE.getErrorMessage() , correlationId);
                }));

        confirmCancellation(member , safeKey, correlationId);
        return member;
    }

    public MemberEntity updateMember(MemberInformation information, String correlationId){
        String email = information.getEmail();
        LOG.info("User with email {} is trying to change his information.", email);

        MemberEntity member = memberRepository.findById(email)
                .orElseThrow(() -> {
                    LOG.info("User with email {} does not exists.", email);
                    return new MemberManagerException(NO_MEMBER_TO_UPDATE.getErrorMessage(), correlationId);
                });

        validateInputInformation(information, correlationId, false);
        member.setName(information.getName());
        member.setSurname(information.getSurname());

        LOG.info("User information have been updated.");
        return memberRepository.save(member);
    }

    private void validateInputInformation(MemberInformation information, String correlationId, boolean isRegistration){

        /*Email must be verified only if a user is registering to the newsletter*/
        if(isRegistration && information.getEmail().replaceAll("\\s","").isEmpty()){
            LOG.info("E-mail cannot be blank.");
            throw new MemberManagerException(BLANK_EMAIL.getErrorMessage(), correlationId);
        }

        if(information.getName().replaceAll("\\s","").isEmpty()){
            LOG.info("First name cannot be blank.");
            throw new MemberManagerException(BLANK_NAME.getErrorMessage(), correlationId);
        }

        if(information.getSurname().replaceAll("\\s","").isEmpty()){
            LOG.info("Last name cannot be blank.");
            throw new MemberManagerException(BLANK_SURNAME.getErrorMessage(), correlationId);
        }
    }

    private void confirmCancellation(MemberEntity associate, String confirmationKey, String correlationId){
        if(isConfirmationKeyCorrect(confirmationKey)){
            LOG.info("The provided key is correct, user with email {} has been unsubscribed from the newsletter.", associate.getEmail());
            memberRepository.delete(associate);
        }else{
            LOG.info("The provided key is not correct, user with email {} cannot be unsubscribed from the newsletter.", associate.getEmail());
            throw new MemberManagerException(INVALID_CONFIRMATION_KEY.getErrorMessage(), correlationId);
        }
    }

    private boolean isConfirmationKeyCorrect(String safeKey){
        String confirmationKey = "YES";
        return confirmationKey.equals(safeKey);
    }

    private MemberEntity mapMember(MemberInformation input){
        MemberEntity member = new MemberEntity();
        member.setName(input.getName());
        member.setSurname(input.getSurname());
        member.setEmail(input.getEmail());
        return member;
    }
}

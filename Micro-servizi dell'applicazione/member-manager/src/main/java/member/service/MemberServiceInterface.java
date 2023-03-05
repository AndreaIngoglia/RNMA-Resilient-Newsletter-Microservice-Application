package member.service;

import member.database.entity.MemberEntity;
import member.utils.MemberInformation;

import java.util.List;

public interface MemberServiceInterface {
    MemberEntity registerMember(MemberInformation user, String correlationId);
    List<MemberEntity> getMembers(String correlationId);
    MemberEntity deleteMember(String email, String safeKey, String correlationIds);
    MemberEntity updateMember(MemberInformation information, String correlationId);
}

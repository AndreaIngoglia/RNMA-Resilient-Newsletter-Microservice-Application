package member.database.repository;

import member.database.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository <MemberEntity, String> {
}

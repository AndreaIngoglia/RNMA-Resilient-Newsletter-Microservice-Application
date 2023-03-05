package member.database.repository;

import member.database.entity.ConfirmationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, String> {
    boolean existsByEmailAndConfirmationToken(String email, String token);
}

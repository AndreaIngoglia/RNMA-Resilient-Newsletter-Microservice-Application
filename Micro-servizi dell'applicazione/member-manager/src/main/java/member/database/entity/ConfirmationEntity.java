package member.database.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "EMAIL_CONFIRMATION")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ConfirmationEntity {

    @Id
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "TOKEN")
    private String confirmationToken;
}

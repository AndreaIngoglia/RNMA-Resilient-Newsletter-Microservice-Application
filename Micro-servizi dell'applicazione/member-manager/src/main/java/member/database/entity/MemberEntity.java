package member.database.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberEntity {

    @Id
    @Column(name = "EMAIL" , unique = true , nullable = false)
    private String email;

    @Column(name = "NAME" , nullable = false)
    private String name;

    @Column(name = "SURNAME" , nullable = false)
    private String surname;

    @Column(name = "CONFIRMED_IDENTITY")
    private String isIdentityConfirmed = "F";
}

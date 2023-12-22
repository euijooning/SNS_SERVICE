package my.sns.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import my.sns.dto.UserForm;
import my.sns.enums.UserRole;

@Getter
@Setter
@AllArgsConstructor
public class UserJoinResponse {

    private Integer id;
    private String userName;
    private UserRole role;


    public static UserJoinResponse fromUser(UserForm dto) {

        return new UserJoinResponse(
                dto.getId(),
                dto.getUsername(),
                dto.getRole()
        );
    }
}

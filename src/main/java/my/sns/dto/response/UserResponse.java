package my.sns.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import my.sns.dto.UserForm;

@Getter
@AllArgsConstructor
public
class UserResponse {
    private Integer id;
    private String userName;

    public static UserResponse fromUser(UserForm user) {
        return new UserResponse(
                user.getId(),
                user.getUsername()
        );
    }

}
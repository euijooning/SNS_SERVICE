package my.sns.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Dto
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {

    private String userName;
    private String password;
}

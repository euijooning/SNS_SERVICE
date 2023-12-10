package my.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import my.sns.model.entity.UserEntity;
import my.sns.enums.UserRole;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {

    private Integer id;
    private String userName;
    private String password;
    private UserRole role;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;


    public static UserDto fromEntity(UserEntity entity) {

        return new UserDto(
                entity.getId(),
                entity.getUserName(),
                entity.getPassword(),
                entity.getRole(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}

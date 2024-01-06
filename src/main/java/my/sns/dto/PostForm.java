package my.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import my.sns.model.entity.PostEntity;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class PostForm {

    private Integer id;
    private String title;
    private String body;
    private UserForm user;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;


    public static PostForm fromEntity(PostEntity entity) {
        return new PostForm(
                entity.getId(),
                entity.getTitle(),
                entity.getBody(),
                UserForm.fromEntity(entity.getUser()),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}

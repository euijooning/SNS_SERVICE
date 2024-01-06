package my.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import my.sns.model.entity.CommentEntity;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class CommentForm {

    private Integer id;
    private String comment;
    private Integer userId;
    private String userName;
    private Integer postId;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;


    public static CommentForm fromEntity(CommentEntity entity) {
        return new CommentForm(
                entity.getId(),
                entity.getComment(),
                entity.getUser().getId(),
                entity.getUser().getUserName(),
                entity.getPost().getId(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}

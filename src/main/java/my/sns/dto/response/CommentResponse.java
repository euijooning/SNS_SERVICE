package my.sns.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import my.sns.dto.CommentForm;
import java.sql.Timestamp;

@AllArgsConstructor
@Getter
public class CommentResponse {

    private Integer id;
    private String comment;
    private Integer userId;
    private String userName;
    private Integer postId;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;


    public static CommentResponse fromComment(CommentForm comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getComment(),
                comment.getUserId(),
                comment.getUserName(),
                comment.getPostId(),
                comment.getRegisteredAt(),
                comment.getUpdatedAt(),
                comment.getDeletedAt()
        );
    }
}

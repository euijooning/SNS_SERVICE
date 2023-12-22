package my.sns.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import my.sns.dto.PostForm;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class PostResponse {
    private Integer id;
    private String title;
    private String body;
    private UserResponse user;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;


    public static PostResponse fromPost(PostForm post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                UserResponse.fromUser(post.getUser()),
                post.getRegisteredAt(),
                post.getUpdatedAt(),
                post.getDeletedAt()
                );
    }
}

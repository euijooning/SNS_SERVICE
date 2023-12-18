package my.sns.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Dto
@Getter
@AllArgsConstructor
public class PostCreateRequest {

    private String title;
    private String body;
}

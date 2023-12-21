package my.sns.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Dto
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequest {

    private String title;
    private String body;
}

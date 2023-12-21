package my.sns.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostModifyRequest {
    String title;
    String body;
}

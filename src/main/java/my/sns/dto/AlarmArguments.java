package my.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlarmArguments {

    // 알람 발생 원인 제공자
    private Integer fromUserId;

    // 알람 발생 주체 id
    private Integer targetId; // ex. post에 새 코멘트 -> postId이다.
}

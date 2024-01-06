package my.sns.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {

    NEW_COMMENT_ON_POST("new comment!"), // 기본 멘트 - 변화가능성 있으므로 일단 서버에서 관리하게 처리.(DB x)
    NEW_LIKE_ON_POST("new like!")
    ;

    private final String alarmText; // 텍스트 필드도 추가
}

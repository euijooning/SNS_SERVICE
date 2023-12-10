package my.sns.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResultResponse<T> {

    private String resultCode; // 결과에 대한 코드값
    private T result; // 결과


    public static <T> ResultResponse<T> success(T result) {
        return new ResultResponse<>("SUCCESS", result);
    }

    public static ResultResponse<Void> error(String errorCode) {
        return new ResultResponse<>(errorCode, null);
    }
}

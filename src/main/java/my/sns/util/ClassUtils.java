package my.sns.util;

import java.util.Optional;

// 클래스 캐스팅 안전하게 할 수 있는 클래스
public class ClassUtils {
    public static <T> Optional<T> getSafeCastInstance(Object o, Class<T> clazz) {
        return clazz != null && clazz.isInstance(o) ? Optional.of(clazz.cast(o)) : Optional.empty();
    }
}

package my.sns.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.sns.dto.UserForm;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

// 유저 캐싱하는 작업하는 클래스. 클래스이기 때문에 @Repository 어노테이션 붙임.
@RequiredArgsConstructor
@Repository
@Slf4j
public class UserCacheRepository {

    private final RedisTemplate<String, UserForm> userRedisTemplate;
    private final static Duration USER_CACHE_TTL = Duration.ofDays(3); // 3일로 유효기간 설정


    // 유저 세팅하는 메서드
    public void setUser(UserForm user) {
        String key = getKey(user.getUsername());
        log.info("Set User to Redis {}, {}", key, user);
        userRedisTemplate.opsForValue().set(key, user, USER_CACHE_TTL);
    }

    // 유저 가져오는 메서드
    public Optional<UserForm> getUser(String userName) {
        String key = getKey(userName);
        UserForm user = userRedisTemplate.opsForValue().get(key);
        log.info("Get data from Redis {}, {}", key, user);

        return Optional.ofNullable(user);
    }


    /**
     * <키 세팅>
     * Filter에서 유저가 있는지 DB 검사 항상 했던 부분을 캐싱으로 대체하고자 함을 고려.
     * 그 곳에서 사용하는 key는 userName 이다.
     * 주의 : 레디스는 하나의 클러스터로 만들어두고 수많은 것들을 캐싱하기 때문에, 구분을 위해 prefix를 붙이는 게 관례.
     */
    private String getKey(String userName) {
        return "USER:" + userName;
    }

}

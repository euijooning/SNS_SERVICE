package my.sns.repository;

import lombok.RequiredArgsConstructor;
import my.sns.dto.UserForm;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

// 유저 캐싱하는 작업하는 클래스. 클래스이기 때문에 @Repository 어노테이션 붙임.
@RequiredArgsConstructor
@Repository
public class UserCacheRepository {

    private final RedisTemplate<String, UserForm> userRedisTemplate;


    // 유저 세팅하는 메서드
    public void setUser(UserForm user) {
        userRedisTemplate.opsForValue().set(getKey(user.getUsername()), user);
    }

    // 유저 가져오는 메서드
    public UserForm getUser(String userName) {
        userRedisTemplate.opsForValue().get(userName);
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

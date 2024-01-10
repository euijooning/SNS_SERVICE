package my.sns.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//sse가 발생하면 저장을 할 클래스. 인스턴스 자체에 저장.
@Slf4j
@Repository
public class EmitterRepository {

    private Map<String, SseEmitter> emitterMap = new HashMap<>();

    // 레디스와 방식 유사
    public SseEmitter save(Integer userId, SseEmitter sseEmitter) {
        // 유저 id로 하는 이유 -> 알람이 발생했을 때, 그 id로 그 사람이 접속한 브라우저를 찾아야 하므로.
        final String key = getKey(userId);
        emitterMap.put(key, sseEmitter);
        log.info("Set sseEmitter {}", userId);

        return sseEmitter;
    }

    // 실제 접속을 안 했을 수도 있기 때문에 optional 사용 고려
    public Optional<SseEmitter> get(Integer userId) {
        final String key = getKey(userId);
        log.info("Get sseEmitter {}", userId);

        return Optional.ofNullable(emitterMap.get(key));
    }

    // 저장한 경우에는 삭제
    public void delete(Integer userId) {
        emitterMap.remove(getKey(userId));
    }

    private String getKey(Integer userId) {
        return "Emitter:UID:" + userId;
    }
}

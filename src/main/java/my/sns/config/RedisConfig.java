package my.sns.config;

import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import my.sns.dto.UserForm;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// 가장 많이 사용하는 유저 부분을 캐싱함.
@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // RedisURI를 생성하고, Redis 서버 연결 정보를 설정.
        RedisURI redisURI = RedisURI.create(redisProperties.getUrl());

        // LettuceConnectionFactory를 사용하여 Redis 서버 연결을 구성함.
        RedisConfiguration configuration = LettuceConnectionFactory.createRedisConfiguration(redisURI);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);

        // 필수적인 초기화 작업을 수행.
        factory.afterPropertiesSet(); // 이걸 해줘야 initializer 사용 가능

        return factory; // 구성된 RedisConnectionFactory를 반환.
    }

    // 레디스에서 사용하는 커맨드를 간편하게 사용할 수 있게 해줌.
    // setConnectionFactory -> 설정할 실제 레디스 서버 정보를 알고 있는 애
    @Bean
    public RedisTemplate<String, UserForm> userRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // RedisTemplate을 생성하고, 사용할 RedisConnectionFactory를 설정.
        RedisTemplate<String, UserForm> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // Redis 키를 직렬화할 때 사용할 Serializer를 설정.
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Redis 값(UserForm 객체)를 직렬화할 때 사용할 Serializer를 설정.
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(UserForm.class));

        return redisTemplate; // 구성된 RedisTemplate을 반환.
    }
}

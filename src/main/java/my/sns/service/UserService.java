package my.sns.service;

import lombok.RequiredArgsConstructor;
import my.sns.dto.AlarmForm;
import my.sns.dto.UserForm;
import my.sns.exception.CustomErrorCode;
import my.sns.exception.SnsApplicationException;
import my.sns.model.entity.UserEntity;
import my.sns.repository.AlarmEntityRepository;
import my.sns.repository.UserCacheRepository;
import my.sns.repository.UserEntityRepository;
import my.sns.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder encoder;
    private final AlarmEntityRepository alarmEntityRepository;
    private final UserCacheRepository userCacheRepository;


    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredMs;


    public UserForm loadUserByUserName(String userName) {
        // 캐시 활용으로 변경, 없을 경우에는 DB를 체크
        return userCacheRepository.getUser(userName).orElseGet(() ->
                userEntityRepository.findByUserName(userName)
                        .map(UserForm::fromEntity)
                        .orElseThrow(() -> new SnsApplicationException(CustomErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)))
        );
    }


    @Transactional
    public UserForm join(String userName, String password) {
        // 회원가입 된 user 유저 체크
        userEntityRepository.findByUserName(userName)
                .ifPresent(it -> {
                    throw new SnsApplicationException(CustomErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
                });

        // 회원가입 - user 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));
        return UserForm.fromEntity(userEntity);
    }

    public String login(String userName, String password) {
        // 회원가입 여부 체크 -> DB에서 처음부터 체크 x, 캐시가 있다면 먼저 캐시에서 가져오도록.
        UserForm user = loadUserByUserName(userName);
        userCacheRepository.setUser(user); // 로그인 했을 때 캐싱을 함.

        // 비밀번호 체크 - 암호화 된 것과 일치하는지를 비교해야.
        if (!encoder.matches(password, user.getPassword())) {
            throw new SnsApplicationException(CustomErrorCode.INVALID_PASSWORD, "");
        }

        // 토큰 생성
        String token = JwtTokenUtils.generateToken(userName, secretKey, expiredMs);

        return token;
    }


    // 알람 기능
    public Page<AlarmForm> alarmList(Integer userId, Pageable pageable) {
        return alarmEntityRepository.findAllByUserId(userId, pageable).map(AlarmForm::fromEntity);

    }
}

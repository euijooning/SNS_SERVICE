package my.sns.service;

import lombok.RequiredArgsConstructor;
import my.sns.dto.UserForm;
import my.sns.exception.CustomErrorCode;
import my.sns.exception.SnsApplicationException;
import my.sns.model.entity.UserEntity;
import my.sns.repository.UserEntityRepository;
import my.sns.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredMs;

    public UserForm loadUserByUserName(String userName) {
        return userEntityRepository.findByUserName(userName)
                .map(UserForm::fromEntity)
                .orElseThrow(() ->
                        new SnsApplicationException(CustomErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));
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
//        throw new RuntimeException();
        return UserForm.fromEntity(userEntity);
    }

    public String login(String userName, String password) {
        // 회원가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() -> new SnsApplicationException(CustomErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        // 비밀번호 체크 - 암호화 된 것과 일치하는지를 비교해야.
        if (!encoder.matches(password, userEntity.getPassword())) {
            throw new SnsApplicationException(CustomErrorCode.INVALID_PASSWORD, "");
        }

        // 토큰 생성
        String token = JwtTokenUtils.generateToken(userName, secretKey, expiredMs);

        return token;
    }
}

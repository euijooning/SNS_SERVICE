package my.sns.service;

import lombok.RequiredArgsConstructor;
import my.sns.exception.CustomErrorCode;
import my.sns.repository.UserEntityRepository;
import my.sns.exception.SnsApplicationException;
import my.sns.dto.UserDto;
import my.sns.model.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;


    public UserDto join(String userName, String password) {
        // 회원가입 된 user 유저 체크
        userEntityRepository.findByUserName(userName)
                .ifPresent(it -> {
                    throw new SnsApplicationException(CustomErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
                });

        // 회원가입
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, password));

        return UserDto.fromEntity(userEntity);
    }

    public String login(String userName, String password) {
        // 회원가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow();

        // 비밀번호 체크
        if (!userEntity.getPassword().equals(password)) {
            throw new SnsApplicationException(CustomErrorCode.INVALID_PASSWORD, "");
        }

        // 토큰 생성

        return "";
    }
}

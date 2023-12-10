package my.sns.service;

import lombok.RequiredArgsConstructor;
import my.sns.controller.repository.UserEntityRepository;
import my.sns.exception.SnsApplicationException;
import my.sns.model.User;
import my.sns.model.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;


    public User join(String userName, String password) {
        // 회원가입 된 user 유저 체크
        Optional<UserEntity> userEntity = userEntityRepository.findByUserName(userName);

        // 회원가입
        userEntityRepository.save(new UserEntity());


        return new User();
    }

    public String login(String userName, String password) {
        // 회원가입 여부 체크
//        userEntityRepository.findByUserName(userName).orElseThrow(() -> new SnsApplicationException());
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(SnsApplicationException::new);

        // 비밀번호 체크
        if (!userEntity.getPassword().equals(password)) {
            throw new SnsApplicationException();
        }

        // 토큰 생성

        return "";
    }
}

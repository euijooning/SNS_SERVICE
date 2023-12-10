package my.sns.service;

import my.sns.repository.UserEntityRepository;
import my.sns.exception.SnsApplicationException;
import my.sns.fixture.UserEntityFixture;
import my.sns.model.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void t1() {
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName, password);

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty()); // 가입된 적이 없으므로 엠프티
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));

        Assertions.assertDoesNotThrow(() -> userService.join(userName, password));
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 이미 동일한 userName으로 가입한 회원이 있는 경우")
    void t2() {
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName, password);

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(userEntityRepository.save(any())).thenReturn(Optional.of(mock(UserEntity.class)));

        Assertions.assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));
    }


    @Test
    @DisplayName("로그인 성공 테스트")
    void t3() {
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName, password);


        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        Assertions.assertDoesNotThrow(() -> userService.login(userName, password));
    }

    @Test
    @DisplayName("로그인 실패 테스트 - userName으로 가입한 회원이 없는 경우")
    void t4() {
        String userName = "userName";
        String password = "password";

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

        Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));
    }

    @Test
    @DisplayName("로그인 실패 테스트 - userName으로 가입한 회원은 존재하지만, password를 틀린 경우")
    void t5() {
        String userName = "userName";
        String password = "password";
        String wrongPassword = "wrong";

        UserEntity fixture = UserEntityFixture.get(userName, password);

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, wrongPassword));
    }
}
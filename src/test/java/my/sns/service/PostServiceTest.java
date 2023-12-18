package my.sns.service;

import my.sns.exception.CustomErrorCode;
import my.sns.exception.SnsApplicationException;
import my.sns.model.entity.PostEntity;
import my.sns.model.entity.UserEntity;
import my.sns.repository.PostEntityRepository;
import my.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostEntityRepository postEntityRepository;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @DisplayName("포스트 작성 성공")
    @Test
    void t1() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        assertDoesNotThrow(() -> postService.createPost(title, body, userName));

    }

    @DisplayName("포스트 작성 실패 - 유저가 존재하지 않는 경우")
    @Test
    void t2() {

        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        SnsApplicationException error = assertThrows(SnsApplicationException.class, () -> postService.createPost(title, body, userName));
        assertEquals(CustomErrorCode.USER_NOT_FOUND, error.getErrorCode());

    }
}

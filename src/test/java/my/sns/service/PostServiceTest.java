package my.sns.service;

import my.sns.exception.CustomErrorCode;
import my.sns.exception.SnsApplicationException;
import my.sns.fixture.PostEntityFixture;
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

    @DisplayName("포스트 삭제 성공")
    @Test
    void t3() {
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        assertDoesNotThrow(() -> postService.deletePost("userName", 1));
    }


    @DisplayName("포스트 삭제 실패 - 글이 존재하지 않는 경우")
    @Test
    void t4() {
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.deletePost("userName", 1));
        assertEquals(CustomErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @DisplayName("포스트 삭제 실패 - 글 삭제 시 요청자가 권한이 없는 경우")
    @Test
    void t5() {
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.deletePost("userName", 1));
        assertEquals(CustomErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }


}

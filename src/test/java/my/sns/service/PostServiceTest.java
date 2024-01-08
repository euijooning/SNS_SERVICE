package my.sns.service;

import my.sns.exception.CustomErrorCode;
import my.sns.exception.SnsApplicationException;
import my.sns.fixture.PostEntityFixture;
import my.sns.model.entity.PostEntity;
import my.sns.model.entity.UserEntity;
import my.sns.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService 테스트")
class PostServiceTest {

    @Mock
    private PostEntityRepository postEntityRepository;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private AlarmEntityRepository alarmEntityRepository;

    @Mock
    private CommentEntityRepository commentEntityRepository;

    @Mock
    private LikeEntityRepository likeEntityRepository;


    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("포스트 생성 성공")
    void t1() {
        // given
        String title = "title";
        String body = "body";
        String userName = "userName";

        UserEntity userEntity = new UserEntity();
        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.of(userEntity));

        // when
        postService.createPost(title, body, userName);

        // then
        verify(userEntityRepository).findByUserName(userName);
        verify(postEntityRepository).save(any(PostEntity.class));
    }

    @Test
    @DisplayName("포스트 생성 실패 - 유저가 존재하지 않는 경우 글 등록 불가")
    void t2() {
        // given
        String title = "title";
        String body = "body";
        String userName = "userName";

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

        // when
        SnsApplicationException error = assertThrows(SnsApplicationException.class, () -> postService.createPost(title, body, userName));
        assertEquals(CustomErrorCode.USER_NOT_FOUND, error.getErrorCode());

        // then
        verify(postEntityRepository, never()).save(any());
    }

    @Test
    @DisplayName("포스트 삭제 성공")
    void t3() {
        // given
        String userName = "userName";
        Integer postId = 1;

        UserEntity userEntity = mock(UserEntity.class);
        PostEntity postEntity = mock(PostEntity.class);

        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.of(userEntity));
        given(postEntityRepository.findById(postId)).willReturn(Optional.of(postEntity));
        given(postEntity.getUser()).willReturn(userEntity);

        // when
        postService.deletePost(userName, postId);

        // then
        verify(userEntityRepository).findByUserName(userName);
        verify(postEntityRepository).findById(postId);
        verify(postEntityRepository).delete(postEntity);

    }


    @Test
    @DisplayName("포스트 삭제 실패 - 유효하지 않은 사용자일 때")
    void t4() {
        // given
        String userName = "nonExistingUser";
        Integer postId = 1;

        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.empty());

        // when,then
        assertThrows(SnsApplicationException.class, () -> postService.deletePost(userName, postId));
    }

    @Test
    @DisplayName("포스트 삭제 실패 - 글이 존재하지 않을 때")
    void t5() {
        // given
        String userName = "userName";
        Integer postId = 1;

        UserEntity userEntity = mock(UserEntity.class);

        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.of(userEntity));
        given(postEntityRepository.findById(postId)).willReturn(Optional.empty());

        // when
        SnsApplicationException e
                = assertThrows(SnsApplicationException.class, () -> postService.deletePost(userName, postId));

        //then
        assertEquals(CustomErrorCode.POST_NOT_FOUND, e.getErrorCode());
        verify(userEntityRepository).findByUserName(userName);
        verify(postEntityRepository).findById(postId);
//        verifyNoMoreInteractions(postEntityRepository);
    }



    @Test
    @DisplayName("내 피드 목록 조회 성공")
    void t6() {
        // given
        UserEntity user = new UserEntity();
        user.setUserName("userName");

        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findAllByUser(Mockito.eq(user), Mockito.any(Pageable.class))).thenReturn(Page.empty());

        Pageable pageable = Mockito.mock(Pageable.class);

        // when, then
        assertDoesNotThrow(() -> postService.myFeed("userName", pageable));
    }


    @DisplayName("포스트 수정 성공")
    @Test
    void t7() {
        // given
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(postEntityRepository.saveAndFlush(any())).thenReturn(postEntity);

        // when, then
        assertDoesNotThrow(() -> postService.modifyPost(title, body, userName, postId));
    }

    @DisplayName("포스트 수정 실패 - 글이 존재하지 않는 경우")
    @Test
    void t8() {
        // given
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        // when & then
        SnsApplicationException e
                = assertThrows(SnsApplicationException.class, () -> postService.modifyPost(title, body, userName, postId));
        assertEquals(CustomErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }


    @DisplayName("댓글 생성 성공")
    @Test
    public void t9() {
        // given
        Integer postId = 1;
        String userName = "testUser";
        String comment = "Test comment";

        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(new PostEntity()));
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(new UserEntity()));

        // when
        postService.comment(postId, userName, comment);

        // then
        verify(commentEntityRepository, times(1)).save(any());
        verify(alarmEntityRepository, times(1)).save(any());
    }

}
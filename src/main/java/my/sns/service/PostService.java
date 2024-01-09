package my.sns.service;

import lombok.RequiredArgsConstructor;
import my.sns.dto.AlarmArguments;
import my.sns.dto.CommentForm;
import my.sns.dto.PostForm;
import my.sns.enums.AlarmType;
import my.sns.exception.CustomErrorCode;
import my.sns.exception.SnsApplicationException;
import my.sns.model.entity.*;
import my.sns.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;


    @Transactional
    public void createPost(String title, String body, String userName) {
        // 유저 찾아오기
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(CustomErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        // 포스트 저장
        PostEntity saved = postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }


    // 포스트 삭제
    @Transactional
    public void deletePost(String userName, Integer postId) {
        // 유저 찾아오기
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(CustomErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        // 포스트 존재 여부 확인
        PostEntity postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(CustomErrorCode.POST_NOT_FOUND, String.format("%s not founded", userName, postId)));

        // Post Permission
        if (postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(CustomErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        // post에 달린 like, comment도 함께 삭제하기
        likeEntityRepository.deleteAllByPost(postEntity);
        commentEntityRepository.deleteAllByPost(postEntity);

        // 궁극적으로 post 삭제
        postEntityRepository.delete(postEntity);
    }

    // 포스트 수정
    @Transactional
    public PostForm modifyPost(String title, String body, String userName, Integer postId) {
        // 유저 찾아오기
        UserEntity userEntity = getUserEntityOrException(userName);

        // 포스트 존재 여부 확인
        PostEntity postEntity = getPostEntityOrException(postId);

        // Post Permission
        if (postEntity.getUser() != userEntity) { // 권한이 없는 경우임.
            throw new SnsApplicationException(CustomErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        // 저장해주기
        postEntity.setTitle(title);
        postEntity.setBody(body);

        return PostForm.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }



    public Page<PostForm> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(PostForm::fromEntity);
    }

    public Page<PostForm> myFeed(String userName, Pageable pageable) {
        // 유저 찾아오기
        UserEntity userEntity = getUserEntityOrException(userName);

        return postEntityRepository.findAllByUser(userEntity, pageable).map(PostForm::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String userName) {
        // 포스트 존재 여부 확인
        PostEntity postEntity = getPostEntityOrException(postId);

        // 유저 찾아오기
        UserEntity userEntity = getUserEntityOrException(userName);

        // 좋아요 눌렀는지를 체크 -> 이미 눌렀으면  throw
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
            throw new SnsApplicationException(CustomErrorCode.ALREADY_LIKED, String.format("userName %s already like post %d", userName, postId));
        });

        // 좋아요 저장
        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));

        // 알람 발생
        alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_LIKE_ON_POST, new AlarmArguments(userEntity.getId(), postEntity.getId())));
    }

    public Long getLikeCount(Integer postId) {
        PostEntity postEntity = getPostEntityOrException(postId);
        // count like
        return likeEntityRepository.countByPost(postEntity);
    }


    @Transactional
    public void comment(Integer postId, String userName, String comment) {
        // 포스트 존재 여부 확인
        PostEntity postEntity = getPostEntityOrException(postId);

        // 유저 찾아오기
        UserEntity userEntity = getUserEntityOrException(userName);

        // 댓글 save
        commentEntityRepository.save(CommentEntity.of(userEntity, postEntity, comment));
        // 알람이 발생
        alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_COMMENT_ON_POST, new AlarmArguments(userEntity.getId(), postEntity.getId())));
    }

    public Page<CommentForm> getComments(Integer postId, Pageable pageable) {
        // 포스트 존재 여부 확인
        PostEntity postEntity = getPostEntityOrException(postId);
        return commentEntityRepository.findAllByPost(postEntity, pageable).map(CommentForm::fromEntity);
    }


    private PostEntity getPostEntityOrException(Integer postId) {
        // 포스트 존재 여부 확인
        return postEntityRepository.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(CustomErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

    }

    private UserEntity getUserEntityOrException(String userName) {
        // 유저 찾아오기
        return userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(CustomErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }
}

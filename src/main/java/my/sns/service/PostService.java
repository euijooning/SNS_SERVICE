package my.sns.service;

import lombok.RequiredArgsConstructor;
import my.sns.dto.PostForm;
import my.sns.exception.CustomErrorCode;
import my.sns.exception.SnsApplicationException;
import my.sns.model.entity.LikeEntity;
import my.sns.model.entity.PostEntity;
import my.sns.model.entity.UserEntity;
import my.sns.repository.LikeEntityRepository;
import my.sns.repository.PostEntityRepository;
import my.sns.repository.UserEntityRepository;
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


    @Transactional
    public void createPost(String title, String body, String userName) {
        // 유저 찾아오기
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(CustomErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        // 포스트 저장
        PostEntity saved = postEntityRepository.save(PostEntity.of(title, body, userEntity));
        // return
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

        // post 삭제
        postEntityRepository.delete(postEntity);
    }

    // 포스트 수정
    @Transactional
    public PostForm modifyPost(String title, String body, String userName, Integer postId) {
        // 유저 찾아오기
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(CustomErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        // 포스트 존재 여부 확인
        PostEntity postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(CustomErrorCode.POST_NOT_FOUND, String.format("%s not founded", userName, postId)));

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
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(CustomErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        return postEntityRepository.findAllByUser(userEntity, pageable).map(PostForm::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String userName) {
        // 포스트 존재 여부 확인
        PostEntity postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(CustomErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        // 유저 찾아오기
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(CustomErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        // 좋아요 눌렀는지를 체크 -> 이미 눌렀으면  throw
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
            throw new SnsApplicationException(CustomErrorCode.ALREADY_LIKED, String.format("userName %s already like post %d", userName, postId));
        });

        // 좋아요 저장
        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));
    }

    public Integer getLikeCount(Integer postId) {
        PostEntity postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(CustomErrorCode.POST_NOT_FOUND, String.format("postId is %d", postId)));
        List<LikeEntity> likes = likeEntityRepository.findAllByPost(postEntity);
        return likes.size();
    }
}

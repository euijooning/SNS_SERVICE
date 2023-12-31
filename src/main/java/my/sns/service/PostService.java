package my.sns.service;

import lombok.RequiredArgsConstructor;
import my.sns.dto.PostForm;
import my.sns.exception.CustomErrorCode;
import my.sns.exception.SnsApplicationException;
import my.sns.model.entity.PostEntity;
import my.sns.model.entity.UserEntity;
import my.sns.repository.PostEntityRepository;
import my.sns.repository.UserEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;


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
}

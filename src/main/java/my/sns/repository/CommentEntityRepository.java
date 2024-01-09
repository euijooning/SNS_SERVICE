package my.sns.repository;

import my.sns.model.entity.CommentEntity;
import my.sns.model.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentEntityRepository extends JpaRepository<CommentEntity, Integer> {

    Page<CommentEntity> findAllByPost(PostEntity post, Pageable pageable);

    void deleteAllByPost(PostEntity postEntity);
}

package my.sns.repository;

import my.sns.model.entity.LikeEntity;
import my.sns.model.entity.PostEntity;
import my.sns.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {
    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);
}

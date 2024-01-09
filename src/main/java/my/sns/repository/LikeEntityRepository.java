package my.sns.repository;

import my.sns.model.entity.LikeEntity;
import my.sns.model.entity.PostEntity;
import my.sns.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {
    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);


//    @Query(value = "SELECT COUNT(*) FROM LikeEntity entity WHERE entity.post =:post")
//    Integer countByPost(PostEntity post);
    Long countByPost(PostEntity post);

    List<LikeEntity> findAllByPost(@Param("post") PostEntity post);

}

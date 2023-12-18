package my.sns.repository;

import my.sns.model.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {
}

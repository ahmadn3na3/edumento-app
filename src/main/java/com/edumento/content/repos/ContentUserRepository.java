package com.edumento.content.repos;

import com.edumento.content.domain.ContentUser;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/** Created by ahmad on 7/20/16. */
@Repository
public interface ContentUserRepository extends MongoRepository<ContentUser, String> {

  Optional<ContentUser> findByUserIdAndContentId(Long userId, Long contentId);
}

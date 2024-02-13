package com.edumento.content.repos;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.edumento.content.domain.ContentUser;

/** Created by ahmad on 7/20/16. */
@Repository
public interface ContentUserRepository extends MongoRepository<ContentUser, String> {

  Optional<ContentUser> findByUserIdAndContentId(Long userId, Long contentId);
}

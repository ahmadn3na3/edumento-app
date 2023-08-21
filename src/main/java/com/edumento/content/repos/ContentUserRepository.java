package com.edumento.content.repos;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.edumento.content.domain.ContentUser;
import com.edumento.core.repos.AbstractMongoRepository;

/** Created by ahmad on 7/20/16. */
@Repository
public interface ContentUserRepository extends AbstractMongoRepository<ContentUser, String> {

  Optional<ContentUser> findByUserIdAndContentId(Long userId, Long contentId);
}

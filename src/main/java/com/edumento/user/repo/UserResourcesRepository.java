package com.edumento.user.repo;

import java.util.stream.Stream;

import org.springframework.stereotype.Repository;

import com.edumento.core.repos.AbstractMongoRepository;
import com.edumento.user.domain.UserResources;

/** Created by ahmad on 3/1/17. */
@Repository
public interface UserResourcesRepository extends AbstractMongoRepository<UserResources, String> {
  Stream<UserResources> findByUserId(Long UserId);
}

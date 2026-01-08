package com.edumento.user.repo;

import com.edumento.core.repos.AbstractMongoRepository;
import com.edumento.user.domain.UserResources;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

/** Created by ahmad on 3/1/17. */
@Repository
public interface UserResourcesRepository extends AbstractMongoRepository<UserResources, String> {
  Stream<UserResources> findByUserId(Long UserId);
}

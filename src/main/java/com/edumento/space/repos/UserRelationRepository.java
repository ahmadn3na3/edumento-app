package com.edumento.space.repos;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Repository;

import com.edumento.core.constants.UserRelationType;
import com.edumento.core.repos.AbstractRepository;
import com.edumento.space.domain.UserRelation;

/** Created by ahmad on 5/23/16. */
@Repository
public interface UserRelationRepository extends AbstractRepository<UserRelation, Long> {
  Optional<UserRelation> findOneByUserUserNameAndFollowIdAndDeletedFalse(
      String userName, Long userId);

  Integer countByUserUserNameAndFollowIdAndDeletedFalse(String userName, Long userId);

  Stream<UserRelation> findByUserIdAndRelationTypeAndDeletedFalse(
      Long userId, UserRelationType relationType);
}

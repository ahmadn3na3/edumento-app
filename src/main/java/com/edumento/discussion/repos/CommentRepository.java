package com.edumento.discussion.repos;


import com.edumento.core.constants.CommentType;
import com.edumento.core.repos.AbstractMongoRepository;
import com.edumento.discussion.domain.Comment;

import java.util.List;
import java.util.Optional;

/** Created by ayman on 25/08/16. */
public interface CommentRepository extends AbstractMongoRepository<Comment, String> {
  Optional<Comment> findOneByIdAndDeletedFalse(String id);

  List<Comment> findOneByParentIdInAndDeletedFalse(Iterable<String> id);

  Integer countByParentIdAndDeletedFalse(String parentId);

  Integer countByUserIdAndSpaceIdAndTypeAndDeletedFalse(
      Long userId, Long spaceId, CommentType type);

  List<Comment> deleteByParentId(String parentId);

  List<Comment> deleteByParentIdIn(Iterable<String> parentIds);
}

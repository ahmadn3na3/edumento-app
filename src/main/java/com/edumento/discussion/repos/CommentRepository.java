package com.edumento.discussion.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.edumento.core.constants.CommentType;
import com.edumento.discussion.domain.Comment;

/** Created by ayman on 25/08/16. */
@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
	Optional<Comment> findOneByIdAndDeletedFalse(String id);

	List<Comment> findOneByParentIdInAndDeletedFalse(Iterable<String> id);

	Integer countByParentIdAndDeletedFalse(String parentId);

	Integer countByUserIdAndSpaceIdAndTypeAndDeletedFalse(Long userId, Long spaceId, CommentType type);

	List<Comment> deleteByParentId(String parentId);

	List<Comment> deleteByParentIdIn(Iterable<String> parentIds);
}

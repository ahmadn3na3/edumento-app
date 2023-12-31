package com.edumento.discussion.repos.mongodb;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.edumento.discussion.domain.Like;

/** Created by ayman on 25/08/16. */
@Repository
public interface LikeRepository extends MongoRepository<Like, String> {

	Optional<Like> findOneByUserIdAndParentIdAndDeletedFalse(Long userId, String parentId);

	List<Like> findByParentIdInAndDeletedFalse(Iterable<String> parentIds);

	default void deleteByParentId(String parentId) {
		deleteByParentIdIn(Collections.singleton(parentId));
	}

	default void deleteByParentIdIn(Iterable<String> parentIds) {
		List<Like> list = this.findByParentIdInAndDeletedFalse(parentIds);
		if (list != null && !list.isEmpty()) {
			deleteAll(list);
		}
	}
}

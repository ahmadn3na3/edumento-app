package com.edumento.discussion.repos.mongodb;

import com.edumento.core.repos.AbstractMongoRepository;
import com.edumento.discussion.domain.Like;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

/** Created by ayman on 25/08/16. */
@Repository
public interface LikeRepository extends AbstractMongoRepository<Like, String> {

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

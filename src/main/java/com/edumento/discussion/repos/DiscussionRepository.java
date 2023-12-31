package com.edumento.discussion.repos;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.edumento.core.constants.DiscussionType;
import com.edumento.discussion.domain.Discussion;

/** Created by ayman on 25/08/16. */
@Repository
public interface DiscussionRepository extends MongoRepository<Discussion, String> {
	List<Discussion> findBySpaceIdAndDeletedFalseOrderByCreationDateDesc(Long spaceId);

	Page<Discussion> findBySpaceIdAndTypeAndDeletedFalseOrderByCreationDateDesc(Long spaceId, DiscussionType type,
			Pageable pageRequest);

	Page<Discussion> findBySpaceId(Long spaceId, Pageable pageRequest);

	List<Discussion> findBySpaceIdAndDeletedTrueAndDeletedDateAfter(Long spaceId, Date since);

	List<Discussion> findBySpaceIdAndDeletedFalseAndLastModifiedDateAfter(Long spaceId, Date since);

	List<Discussion> findBySpaceIdAndLastModifiedDateIsNullAndDeletedFalseAndCreationDateAfter(Long spaceId,
			Date since);

	Optional<Discussion> findOneByIdAndDeletedFalse(String id);

	Integer countBySpaceIdAndTypeAndOwnerIdAndDeletedFalse(Long SpaceId, DiscussionType typr, Long ownerId);
}

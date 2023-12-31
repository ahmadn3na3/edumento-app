package com.edumento.assessment.repos;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.edumento.assessment.domain.Assessment;
import com.edumento.core.constants.AssessmentStatus;
import com.edumento.core.constants.AssessmentType;
import com.edumento.space.domain.Space;
import com.edumento.user.domain.User;

/** Created by ayman on 13/06/16. */
@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long>, JpaSpecificationExecutor<Assessment> {

	Integer countByOwnerIdAndSpaceIdAndDeletedFalse(Long ownerId, Long spaceId);

	Optional<Assessment> findOneByIdAndDeletedFalse(Long id);

	Optional<Assessment> findOneByIdAndDeletedFalseAndOwnerIdAndAssessmentTypeIn(Long id, Long user,
			AssessmentType... assessmentType);

	Stream<Assessment> findBySpaceInAndDeletedFalse(Iterable<Space> spaces);

	Page<Assessment> findBySpaceIdAndDeletedFalseAndOwnerOrPublishTrue(Long id, User user, Pageable pageable);

	Stream<Assessment> findBySpaceIdAndDeletedDateAfterAndDeletedTrue(Long id, Date date);

	Stream<Assessment> findBySpaceIdAndLastModifiedDateAfterAndDeletedFalse(Long id, Date currentDate);

	Stream<Assessment> findBySpaceIdAndLastModifiedDateIsNullAndCreationDateAfterAndDeletedFalse(Long id, Date date);

	@Override
	Page<Assessment> findAll(Pageable page);

	@Query("select a from Assessment a where a.assessmentType=?1 and a.space.id = ?2 and a.deleted=false and (a.publish=true or a.owner.id=?3 )")
	Page<Assessment> findAllByAssessmentType(AssessmentType type, Long spaceId, Long userId, Pageable page);

	@Query("select a from Assessment a where a.assessmentType=?1 and a.space.id = ?2 and a.deleted=false and a.publish=true and a.owner.id=?3 ")
	Page<Assessment> findAllByAssessmentTypeandOwnerId(AssessmentType type, Long spaceId, Long userId, Pageable page);

	Stream<Assessment> findBySpaceIdAndPublishTrueAndPublishDateNotNullAndDeletedFalseOrderByPublishDateDesc(
			Long spaceId);

	@Query("select count(s), s.assessmentType from Assessment s where s.creationDate between ?1 and ?2 and s.deleted=false and s.space.id=?3 group by s.assessmentType")
	List<Object[]> assessmenttStaticsByTypeAndCreationDateBetween(Date from, Date to, Long spaceId);

	@Query("select count(s), s.assessmentType from Assessment s where s.creationDate between ?1 and ?2 and s.deleted=false and s.owner.id=?3 group by s.assessmentType")
	List<Object[]> assessmentStaticsByOwnerIdTypeAndCreationDateBetween(Date from, Date to, Long ownerId);

	/** Created by A.Alsayed on 05/01/19. */
	@Query("select a from Assessment a join a.challengees u where a.assessmentType=?1 and a.space.id=?2 "
			+ "and (a.owner.id=?3 or u.id = ?3) "
			+ "and (a.assessmentStatus=?4 or (a.assessmentStatus!=?4 and a.dueDate > ?5)) "
			+ "and a.deleted=false "
			+ "order by a.creationDate desc")
	Page<Assessment> getUserChallenges(AssessmentType type, Long spaceId, Long userId, AssessmentStatus status,
			Date currentDate, Pageable page);

	/** Created by A.Alsayed on 10/01/19. */
	List<Assessment> findByDeletedFalseAndAssessmentStatusNotAndAssessmentTypeAndDueDateLessThan(
			AssessmentStatus status, AssessmentType type, Date currentDate);

	/** Created by A.Alsayed on 29/01/19 for practice migration script */
	List<Assessment> findAllByAssessmentTypeAndAssessmentStatusAndDeletedFalse(AssessmentType type,
			AssessmentStatus status);

	/** Created by A.Alsayed on 18/02/19. */
	Optional<Assessment> findOneByIdAndDeletedFalseAndAssessmentType(Long id, AssessmentType type);

	/** Created by A.Alsayed on 19/02/19. */
	@Query("select a from Assessment a join a.challengees u where u.id = ?1 "
			+ "and a.id =?2 "
			+ "and a.assessmentType = 4 "
			+ "and a.deleted=false")
	List<Assessment> findAssessmentByChallengee(Long userId, Long assessmentId);
}

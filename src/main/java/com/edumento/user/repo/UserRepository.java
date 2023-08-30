package com.edumento.user.repo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Organization;
import com.edumento.b2c.domain.CloudPackage;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;
import com.edumento.user.model.GlobalRankingModel;

@Repository
public interface UserRepository extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {

	Optional<User> findOneByActivationKeyAndDeletedFalse(String activationKey);

	Optional<User> findOneByResetKeyAndDeletedFalse(String resetKey);

	Optional<User> findOneByEmailAndDeletedFalse(String email);

	Optional<User> findOneByEmailAndDeletedFalseAndOrganizationIsNullAndFoundationIsNull(String email);

	Optional<User> findOneByEmailAndDeletedFalseAndOrganizationAndFoundation(String email, Organization organization,
			Foundation foundation);

	Optional<User> findOneByIdAndDeletedFalse(Long Id);

	// @Cacheable("users")
	Optional<User> findOneByUserNameAndDeletedFalse(String login);

	Stream<User> findByFoundationAndDeletedFalse(Foundation foundation);

	Stream<User> findByFoundationIdAndDeletedFalse(Long foundationId);

	Stream<User> findByIdInAndOrganizationAndDeletedFalse(List<Long> users, Organization organization);

	Integer countByOrganizationAndDeletedFalse(Organization organization);

	Stream<User> findByOrganizationIdAndDeletedFalseAndOrganizationDeletedFalse(Long orgId);

	Stream<User> findByOrganizationIdAndDeletedFalse(Long orgid);

	Stream<User> findByOrganizationIsNullAndFoundationIsNullAndDeletedFalse();

	Page<User> findByOrganizationIsNullAndFoundationIsNullAndDeletedFalse(Pageable pageable);

	Stream<User> findByGroupsIdInAndOrganizationAndDeletedFalse(Iterable<Long> ids, Organization organization);

	Stream<User> findByRolesIdInAndOrganizationAndDeletedFalse(Iterable<Long> ids, Organization organization);

	Integer countByRolesIdInAndDeletedFalse(Iterable<Long> ids);

	Stream<User> findByIdInAndOrganizationAndTypeAndDeletedFalse(Iterable<Long> id, Organization organization,
			UserType userType);

	Integer countByEmailAndDeletedFalse(String email);

	Integer countByFoundationAndDeletedFalse(Foundation foundation);

	Integer countByCloudPackageAndDeletedFalse(CloudPackage cloudPackage);

	Page<User> findByGroupsIdInAndFoundationAndEmailAndDeletedFalse(Iterable<Long> accessedGroups,
			Foundation organization, String email, Pageable pageable);

	Page<User> findAllByTypeAndDeletedFalse(UserType userType, Pageable pageable);

	Stream<User> findByRolesIdInAndDeletedFalse(Long id);

	Stream<User> findByTimeLockIdAndDeletedFalse(Long timeLockId);

	Stream<User> findByIdInAndDeletedFalse(Iterable<Long> userIdList);

	Stream<User> findByGroupsIdInAndDeletedFalse(Iterable<Long> groupIdList);

	Stream<User> findByOrganizationIdInAndDeletedFalse(Iterable<Long> organizationId);

	/** Created by A.Alsayed on 21/01/2019. */
	/** this method is used for returning User's global ranking */
	@Query(value = "select results.rank from " + "(select u.id As user_id, u.total_score, " + "CASE "
			+ "WHEN @prev_value = u.total_score THEN @curRank "
			+ "WHEN @prev_value\\:=u.total_score THEN @curRank\\:=@curRank + 1 " + "ELSE @curRank\\:=@curRank + 1 "
			+ "END AS rank " + "from user u, (SELECT @curRank\\:=0) r, (SELECT @prev_value\\:=NULL) pv "
			+ "where u.deleted=false " + "order by u.total_score desc) results "
			+ "where results.user_id = ?1", nativeQuery = true)
	Integer getUserGlobalRanking(Long userId);

	/** Created by A.Alsayed on 16/01/2019. */
	@Query("select u.totalScore from User u where u.deleted=false and u.id = ?1")
	Float getUserTotalScore(Long userId);

	/** Created by A.Alsayed on 14/03/2019. */
	/** this method is used for returning top 20 Global Ranking */
	@Query(value = "select u.full_name, u.id, " + "CASE " + "WHEN @prev_value = u.total_score THEN @curRank "
			+ "WHEN @prev_value\\:=u.total_score THEN @curRank\\:=@curRank + 1 " + "ELSE @curRank\\:=@curRank + 1 "
			+ "END as rank, u.school, " + "u.thumbnail, u.total_score "
			+ "from user u, (SELECT @curRank\\:=0) r, (SELECT @prev_value\\:=NULL) pv " + "where u.deleted=false "
			+ "and u.total_score > 0 " + "order by u.total_score desc " + "LIMIT 20", nativeQuery = true)
	List<GlobalRankingModel> getTopUsersRanking();

}

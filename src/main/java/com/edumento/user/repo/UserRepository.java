package com.edumento.user.repo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.edumento.b2c.domain.CloudPackage;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {

	Optional<User> findOneByActivationKeyAndDeletedFalse(String activationKey);

	Optional<User> findOneByResetKeyAndDeletedFalse(String resetKey);

	Optional<User> findOneByEmailAndDeletedFalse(String email);

	Optional<User> findOneByIdAndDeletedFalse(Long Id);

	// @Cacheable("users")
	Optional<User> findOneByUserNameAndDeletedFalse(String login);

	Stream<User> findByDeletedFalse();

	Page<User> findByDeletedFalse(Pageable pageable);

	// Integer countByRolesIdInAndDeletedFalse(Iterable<Long> ids);

	Integer countByEmailAndDeletedFalse(String email);

	Integer countByCloudPackageAndDeletedFalse(CloudPackage cloudPackage);

	Page<User> findAllByTypeAndDeletedFalse(UserType userType, Pageable pageable);

	// Stream<User> findByRolesIdInAndDeletedFalse(Long... id);

	// Stream<User> findByTimeLockIdAndDeletedFalse(Long timeLockId);

	Stream<User> findByIdInAndDeletedFalse(Iterable<Long> userIdList);

	// Stream<User> findByGroupsIdInAndDeletedFalse(Iterable<Long> groupIdList);

	/** Created by A.Alsayed on 21/01/2019. */
	/** this method is used for returning User's global ranking */
	// @Query(value = """
	// select results.rank from
	// (select u.id As user_id, u.total_score,
	// CASE
	// WHEN @prev_value = u.total_score THEN @curRank
	// WHEN @prev_value\\:=u.total_score THEN @curRank\\:=@curRank + 1\
	// ELSE @curRank\\:=@curRank + 1
	// END AS rank
	// from user u, (SELECT @curRank\\:=0) r, (SELECT @prev_value\\:=NULL) pv
	// where u.deleted=false
	// order by u.total_score desc) results
	// where results.user_id = ?1""", nativeQuery = true)
	// Integer getUserGlobalRanking(Long userId);
	//
	// /** Created by A.Alsayed on 16/01/2019. */
	// @Query("select u.totalScore from User u where u.deleted=false and u.id = ?1")
	// Float getUserTotalScore(Long userId);
	//
	// /** Created by A.Alsayed on 14/03/2019. */
	// /** this method is used for returning top 20 Global Ranking */
	// @Query(value = """
	// select u.full_name, u.id,\s\
	// CASE\s\
	// WHEN @prev_value = u.total_score THEN @curRank\s\
	// WHEN @prev_value\\:=u.total_score THEN @curRank\\:=@curRank + 1\s\
	// ELSE @curRank\\:=@curRank + 1\s\
	// END as rank, u.school,\s\
	// u.thumbnail, u.total_score\s\
	// from user u, (SELECT @curRank\\:=0) r, (SELECT @prev_value\\:=NULL) pv\s\
	// where u.deleted=false\s\
	// and u.total_score > 0\s\
	// order by u.total_score desc\s\
	// LIMIT 20""", nativeQuery = true)
	// List<GlobalRankingModel> getTopUsersRanking();

}

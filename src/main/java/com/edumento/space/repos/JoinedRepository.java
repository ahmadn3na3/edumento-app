package com.edumento.space.repos;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edumento.core.constants.SpaceRole;
import com.edumento.space.domain.Joined;
import com.edumento.space.domain.Space;
import com.edumento.user.domain.User;

/** Created by ahmad on 3/21/16. */
@Repository
public interface JoinedRepository extends JpaRepository<Joined, Long> {

  Optional<Joined> findOneByUserIdAndSpaceIdAndDeletedFalse(Long userId, Long spaceId);

  Optional<Joined> findOneByUserIdAndSpaceIdAndFavoriteTrueAndDeletedFalse(
      Long userId, Long spaceId);

  Stream<Joined> findByUserIdAndDeletedFalse(Long userId);

  Page<Joined> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

  Page<Joined> findByUserIdAndSpaceNameIgnoreCaseContainingAndDeletedFalse(
      Long userId, String name, Pageable pageable);

  Page<Joined> findByUserAndSpaceCategoryIdAndDeletedFalse(User user, Long id, Pageable pageable);

  Stream<Joined>
      findByDeletedFalseAndUserIdAndSpaceDeletedFalseAndSpaceLastModifiedDateNotNullAndSpaceLastModifiedDateAfter(
          Long userName, Date time);

  Stream<Joined> findByUserIdAndCreationDateAfterAndDeletedFalse(Long userId, Date time);

  Stream<Joined> findByUserIdAndLastModifiedDateAfterAndDeletedFalse(Long userId, Date time);

  Stream<Joined> findByUserIdAndDeletedTrueAndDeletedDateAfterAndSpaceDeletedFalse(
      Long userId, Date time);

  Stream<Joined> findByUserIdAndDeletedTrueAndSpaceDeletedTrueAndSpaceDeletedDateGreaterThanEqual(
      Long userId, Date time);

  @Query(
      "SELECT avg(j.rating + 0.0 ) as average FROM Joined j where j.space.id  = :spaceId and j.deleted = false")
  Double getAvarageRatingOnSpace(@Param("spaceId") Long spaceId);

  @Query(
      "update Joined j set  j.lastAccessed= current_timestamp(),j.lastModifiedDate=current_timestamp() where j.space = ?1 and j.user=?2")
  @Modifying
  void updateLastAccessed(Space space, User user);

  Stream<Joined> findBySpaceIdAndDeletedFalse(Long spaceId);

  Stream<Joined> findBySpaceIdAndGroupNameAndDeletedFalse(Long spaceId, String groupName);

  /** Created by A.Alsayed on 21/01/2019. */
  /** This method is used to get count of all users for a single space **/
  Integer countBySpaceIdAndDeletedFalse(Long spaceId);

  Stream<Joined> findBySpaceInAndDeletedFalse(Iterable<Space> spaces);

  Stream<Joined> findBySpaceIdAndUserIdInAndDeletedFalse(Long spaceId, Iterable<Long> users);

  Optional<Joined> findOneBySpaceIdAndUserIdAndDeletedFalse(Long spaceId, Long userId);

  Optional<Joined> findOneBySpaceIdAndUserIdAndSpaceRoleInAndDeletedFalse(
      Long spaceId, Long userId, SpaceRole... roles);

  @Query(
      "select j from Joined j where j.space.id= ?1 and ?2 in (select userName from User u where j.user.id=u.id)  AND j.deleted = false")
  Optional<Joined> findOneBySpaceIdAndUserNameAndDeletedFalse(Long spaceId, String userName);

  @Query("Select j From Joined j Where j.space.id = ?1 and j.deleted = false")
  Stream<Joined> getSpaceCommunity(Long spaceId);

  @Query(
      "Select count(distinct j.space.id) from Joined j where j.groupName = ?1 and j.deleted = false ")
  Integer countByDistinctSpaceIdAndGroupName(String groupName);

  Integer countBySpaceIdAndGroupNameAndDeletedFalse(Long spaceId, String groupName);

  Stream<Joined> findByGroupNameAndDeletedFalse(String groupName);

  // implement filters
  @Query("SELECT j FROM Joined j where j.user.id = ?1 AND j.favorite = true AND j.deleted= false")
  Page<Joined> getFavoriteSpaces(Long userId, Pageable pageable);

  @Query("SELECT j FROM Joined j where j.user.id = ?1 AND j.spaceRole = 4 AND j.deleted = false ")
  Page<Joined> getOwnedSpacesByUser(Long userId, Pageable pageable);

  Page<Joined> findByUserIdAndDeletedFalseOrderByLastAccessedDesc(Long userId, Pageable pageable);

  Stream<Joined> findBySpaceIdAndDeletedTrue(Long spaceId);

  @Query(
    value = "select count(*) from joined  where deleted=false and space_id=?1",
    nativeQuery = true
  )
  Integer joinedStatics(Long spaceId);

  /** Created by A.Alsayed on 21/01/2019. */
  /** this method is used for returning User's space rank using userId and Space ID */
  @Query(value = "select results.rank from "
  		+ "(select j.user_id As user_id, j.space_id, j.space_score_points, "
  		+ "CASE "
  		+ "WHEN @prev_value = j.space_score_points THEN @curRank "
  		+ "WHEN @prev_value\\:=j.space_score_points THEN @curRank\\:=@curRank + 1 "
  		+ "ELSE @curRank\\:=@curRank + 1 "
  		+ "END AS rank "
  		+ "from joined j, (SELECT @curRank\\:=0) r, (SELECT @prev_value\\:=NULL) pv "
  		+ "where j.deleted = false "
  		+ "and j.space_id = ?1 "
  		+ "order by j.space_score_points desc) results "
  		+ "where results.user_id = ?2", nativeQuery = true)
  Integer getUserSpaceRank(Long spaceId, Long userId);
}

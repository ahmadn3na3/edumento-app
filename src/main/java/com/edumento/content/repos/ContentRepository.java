package com.edumento.content.repos;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.edumento.content.domain.Content;
import com.edumento.core.constants.ContentStatus;
import com.edumento.core.constants.ContentType;
import com.edumento.core.repos.AbstractRepository;
import com.edumento.space.domain.Space;
import com.edumento.user.domain.User;

/** Created by ahmad on 6/20/16. */
@Repository
public interface ContentRepository
    extends AbstractRepository<Content, Long>, JpaSpecificationExecutor<Content> {

  Optional<Content> findOneByNameAndShelfNameAndOwnerAndSpaceAndDeletedFalse(
      String name, String shelfName, User owner, Space space);

  Optional<Content> findOneByNameAndOwnerAndSpaceAndDeletedFalse(
      String name, User owner, Space space);

  Stream<Content> findBySpaceInAndDeletedFalse(Iterable<Space> spaces);

  Stream<Content> findBySpaceIdAndDeletedFalseAndCreationDateAfter(Long id, Date dateTime);

  Stream<Content> findBySpaceIdAndDeletedFalseAndLastModifiedDateAfter(Long id, Date dateTime);

  Stream<Content> findBySpaceIdAndDeletedTrueAndDeletedDateAfter(Long id, Date dateTime);
  
  Integer countBySpaceIdAndDeletedFalseAndCreationDateAfter(Long id, Date dateTime);

  Optional<Content> findOneByIdAndDeletedFalse(Long contentId);

  List<Content> findByTypeAndDeletedFalseAndStatusIn(
      ContentType type, ContentStatus... contentStatuses);

  Page<Content> findBySpaceId(Long spaceId, Pageable page);

  @Query("Select c.name from Content c where c.id = ?1")
  String findNameByID(Long Id);

  @Query(
      "Select count(c) from Content c where c.space = ?1 and c.status in ( 2 , 3 )and c.type <> 5 and c.deleted=false")
  Integer countBySpace(Space space);

  @Query(
      "Select count(c) from Content c where c.space = ?1 and c.owner = ?2 and c.status in ( 2 , 3 )and c.type <> 5 and c.deleted=false")
  Integer countBySpaceAndUser(Space space, User user);

  @Query(
      "Select distinct c.shelfName from Content c where c.space.id = ?1 and c.type <> 5 and c.deleted=false")
  Stream<String> findDistinctShelfNameBySpaceIdAndDeletedFalse(Long spaceId);

  @Query(
      "select count(s), s.type, sum(s.size) from Content s where s.creationDate between ?1 and ?2 and s.deleted=false and s.space.id=?3 and s.status in ( 2 , 3 )and s.type <> 5 group by s.type")
  List<Object[]> contentStaticsByTypeAndCreationDateBetween(Date from, Date to, Long spaceId);

  @Query(
      "select count(s), s.type from Content s where s.creationDate between ?1 and ?2 and s.deleted=false and s.owner.id=?3 and s.status in ( 2 , 3 )and s.type <> 5 group by s.type")
  List<Object[]> contentStaticsByOwnerIdTypeAndCreationDateBetween(
      Date from, Date to, Long spaceId);
}

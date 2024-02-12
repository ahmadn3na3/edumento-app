package com.edumento.notification.repo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.edumento.notification.domian.Notification;

/** Created by ayman on 02/03/17. */
@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
  Stream<Notification> findByUserIdAndReceivedFalseAndDeletedFalseOrderByCreationDateDesc(Long userid);
  
  Page<Notification> findByUserIdAndDeletedFalseAndNotificationCategoryEqualsOrderByCreationDateDesc(
      Long userid, Pageable pageable, int i);

  Stream<Notification> findByIdInAndUserIdAndReceivedFalseAndDeletedFalseOrderByCreationDateDesc(
      List<String> id, Long userId);

  Stream<Notification> findByIdInAndUserIdAndDeletedFalseOrderByCreationDateDesc(List<String> id, Long userId);

  Optional<Long> countByUserIdAndReceivedFalseAndDeletedFalseAndNotificationCategoryEquals(Long userId, int i);
}

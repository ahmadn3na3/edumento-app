package com.edumento.content.repos;

import java.util.Date;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.edumento.content.domain.Task;
import com.edumento.core.constants.TaskStatus;
import com.edumento.core.constants.TaskType;

/** Created by ahmad on 6/22/16. */
@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
	Task findOneByIdAndType(String id, TaskType taskType);

	Task findOneByUserNameAndContentIdAndType(String userName, Long contentId, TaskType taskType);

	Stream<Task> findByTypeAndStatusAndExpiryDateAfter(TaskType taskType, TaskStatus status, Date date);
}

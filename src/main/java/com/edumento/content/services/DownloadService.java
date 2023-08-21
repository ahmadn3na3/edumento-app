package com.edumento.content.services;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.edumento.content.domain.Content;
import com.edumento.content.domain.Task;
import com.edumento.content.repos.TaskRepository;
import com.edumento.content.util.FileUtil;
import com.edumento.content.util.MultipartFileSender;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.ContentStatus;
import com.edumento.core.constants.TaskStatus;
import com.edumento.core.constants.TaskType;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.ResponseModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Created by ahmad on 6/22/16. */
@Service
public class DownloadService {
  private final Logger log = LoggerFactory.getLogger(DownloadService.class);
  @Autowired TaskRepository taskRepository;
  @Autowired ContentService contentService;

  @Autowired FileUtil fileUtil;

  public ResponseModel startDownload(Long id, HttpServletRequest request) {
    // get content Information by id throw services

    String authorization = request.getHeader("Authorization");

    Content contentModel = contentService.getContentInformation(id);
    // !Files.isRegularFile(fileUtil.createFilePathFromTask(contentModel))
    // ||
    if (contentModel == null) {
      return ResponseModel.error(Code.NOT_FOUND, "content not found");
    }
    if (contentModel.getStatus() == ContentStatus.NOT_UPLOAD
        || contentModel.getStatus() == ContentStatus.UPLOADING) {
      return ResponseModel.error(Code.INVALID, "Invalid Status of Content");
    }
    log.info("start Download Task for content {}", id);
    Task task = new Task(contentModel);
    task.setType(TaskType.DOWNLOAD);
    task.setStatus(TaskStatus.INITIATED);
    this.taskRepository.save(task);

    //////////////////////////////////////////////

    return ResponseModel.done((Object) task.getId());
  }

  public void getRange(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    // get content Information by id throw services
    String downloadId = request.getHeader("did");
    //
    if (downloadId == null) {
      log.info("invald Download Task ");
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      new ObjectMapper().writeValue(response.getOutputStream(), ResponseModel.error(Code.INVALID));
    }

    Task task = this.taskRepository.findOneByIdAndType(downloadId, TaskType.DOWNLOAD);

    //////////////////////////////////////////////
    if (task == null
        || task.getStatus() == TaskStatus.FINISHED
        || task.getStatus() == TaskStatus.CANCELED) {
      log.info("invald Download Task ", downloadId);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      new ObjectMapper()
          .writeValue(response.getOutputStream(), ResponseModel.error(Code.NOT_FOUND));
      return;
    }
    MultipartFileSender.fromPath(fileUtil.createFilePathFromTask(task))
        .with(request)
        .with(response)
        .serveResource();
    if (task.getStatus() == TaskStatus.INITIATED) {
      task.setStartDate(new Date());
      task.setStatus(TaskStatus.STARTED);
      taskRepository.save(task);
    }
  }

  public ResponseModel commit(String id, HttpServletRequest request) {
    log.info("start Commit Task id {}", id);
    Task task = taskRepository.findOneByIdAndType(id, TaskType.DOWNLOAD);
    if (task == null
        || task.getStatus() == TaskStatus.FINISHED
        || task.getStatus() == TaskStatus.CANCELED) {
      throw new NotFoundException();
    }
    task.setFinishedDate(new Date());
    task.setStatus(TaskStatus.FINISHED);
    taskRepository.save(task);
    log.info("end Commit Task id {}", id);
    return ResponseModel.done();
  }

  public ResponseModel cancel(String id, HttpServletRequest request) {
    log.info("start cancel Task id ");
    Task task = taskRepository.findOneByIdAndType(id, TaskType.DOWNLOAD);
    if (task == null
        || task.getStatus() == TaskStatus.FINISHED
        || task.getStatus() == TaskStatus.CANCELED) {
      throw new NotPermittedException();
    }
    task.setStatus(TaskStatus.CANCELED);
    taskRepository.save(task);
    log.info("end cancel Task id {} ", id);
    return ResponseModel.done();
  }
}

package com.edumento.content.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.edumento.content.util.FileUtil;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.ContentType;
import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.security.SecurityUtils;
import com.edumento.user.domain.UserResources;
import com.edumento.user.repo.UserRepository;
import com.edumento.user.repo.UserResourcesRepository;

import jakarta.transaction.Transactional;

/** Created by ahmad on 3/1/17. */
@Service
public class ResourceUploadService {
  private final Logger log = LoggerFactory.getLogger(ResourceUploadService.class);

  @Autowired private UserResourcesRepository userResourcesRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private FileUtil fileUtil;

  @Value("${mint.url}")
  private String url;

  @Transactional
  public ResponseModel addUserResources(MultipartFile file, ContentType type) {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user -> {
              try {
                UserResources userResources = fileUtil.uploadResourceFile(file, type);
                userResources.setUserName(user.getUserName());
                userResources.setUserId(user.getId());
                userResourcesRepository.save(userResources);
                return ResponseModel.done(userResources.getDiskFileName());
              } catch (IOException e) {
                log.error("Exception in upload resource", e);
                throw new RuntimeException(e);
              }
            })
        .orElseThrow(NotPermittedException::new);
  }

  @Transactional
  public ResponseModel uploadAudioFile(MultipartFile file) {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user -> {
              try {
                UserResources userResources = fileUtil.uploadResourceFile(file, ContentType.AUDIO);
                userResources.setUserName(user.getUserName());
                userResources.setUserId(user.getId());
                userResourcesRepository.save(userResources);
                return ResponseModel.done(
                    (Object)
                        userResources
                            .getDiskFileName()
                            .replace(fileUtil.getAudioPath(), url + "/audio")
                            .replace("\\", "/"));
              } catch (IOException e) {
                log.error("Exception in upload resource", e);
                throw new RuntimeException(e);
              }
            })
        .orElseThrow(NotPermittedException::new);
  }

  @Transactional
  public ResponseModel uploadImageAndThumbnail(MultipartFile img, MultipartFile thumb) {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user -> {
              Map<String, String> stringMap = new HashMap<>();
              try {
                if (img == null || img.isEmpty()) {
                  log.warn("file image {} is invalid", img.getName());
                  throw new MintException(Code.INVALID, "invalid image file");
                }
                log.debug("upload image {} ", img.getName());

                UserResources userResources = null;

                userResources = fileUtil.uploadResourceFile(img, ContentType.IMAGE);

                userResources.setUserName(user.getUserName());
                userResources.setUserId(user.getId());
                userResourcesRepository.save(userResources);

                stringMap.put(
                    "image",
                    userResources.getDiskFileName().replace(fileUtil.getImgPath(), url + "/img"));
                if (thumb != null && !thumb.isEmpty()) {
                  userResources = fileUtil.uploadResourceFile(thumb, ContentType.IMAGE);
                  userResources.setUserName(user.getUserName());
                  userResources.setUserId(user.getId());
                  userResourcesRepository.save(userResources);
                  stringMap.put(
                      "thumbnail",
                      userResources.getDiskFileName().replace(fileUtil.getImgPath(), url + "/img"));
                }

                return ResponseModel.done(stringMap);
              } catch (IOException e) {
                log.error("Exception in upload resource", e);
                throw new RuntimeException(e);
              }
            })
        .orElse(ResponseModel.error(Code.NOT_PERMITTED));
  }
}

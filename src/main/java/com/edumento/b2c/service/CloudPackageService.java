package com.edumento.b2c.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edumento.b2c.domain.CloudPackage;
import com.edumento.b2c.model.CloudPackageCreateModel;
import com.edumento.b2c.model.CloudPackageModel;
import com.edumento.b2c.repos.CloudPackageRepository;
import com.edumento.core.constants.PackageType;
import com.edumento.core.exception.ExistException;
import com.edumento.core.exception.InvalidException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.model.PageResponseModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;
import com.edumento.user.repo.PermissionRepository;
import com.edumento.user.repo.UserRepository;

import jakarta.annotation.PostConstruct;

/** Created by ahmad on 4/19/17. */
@Service
@DependsOn("moduleService")
public class CloudPackageService {
  private final CloudPackageRepository cloudPackageRepository;
  private final UserRepository userRepository;
  private final PermissionRepository permissionRepository;

  @Autowired
  public CloudPackageService(
      CloudPackageRepository cloudPackageRepository,
      UserRepository userRepository,
      PermissionRepository permissionRepository) {
    this.cloudPackageRepository = cloudPackageRepository;
    this.userRepository = userRepository;
    this.permissionRepository = permissionRepository;
  }

  @PreAuthorize("hasAuthority('PACKAGE_CREATE')")
  public ResponseModel create(CloudPackageCreateModel cloudPackageCreateModel) {
    CloudPackage cloudPackage;
    if (cloudPackageCreateModel.getPackageType() != PackageType.CUSTOM) {
      cloudPackage =
          cloudPackageRepository.findByPackageTypeAndNameAndDeletedFalse(
              cloudPackageCreateModel.getPackageType(),
              cloudPackageCreateModel.getPackageType().name());
    } else {
      cloudPackage =
          cloudPackageRepository.findByPackageTypeAndNameAndDeletedFalse(
              cloudPackageCreateModel.getPackageType(), cloudPackageCreateModel.getName());
    }
    if (cloudPackage != null) {
      throw new ExistException();
    }

    cloudPackage = new CloudPackage();
    cloudPackage.setName(cloudPackageCreateModel.getName());
    if (cloudPackageCreateModel.getPackageType() != PackageType.CUSTOM) {
      cloudPackage.setName(cloudPackageCreateModel.getPackageType().name());
    }
    cloudPackage.setStorage(cloudPackageCreateModel.getStorage());
    cloudPackage.setPackageTimeLimit(cloudPackageCreateModel.getPackageTimeLimit());
    cloudPackage.setCommunitySizePerSpace(cloudPackageCreateModel.getCommunitySizePerSpace());
    cloudPackage.setEncryptedContent(cloudPackageCreateModel.getEncryptedContent());
    cloudPackage.setMaxCountOfRentedSpaces(cloudPackageCreateModel.getMaxCountOfRentedSpaces());
    cloudPackage.setPackageType(cloudPackageCreateModel.getPackageType());
    cloudPackage.getPermission().putAll(cloudPackageCreateModel.getPermission());
    cloudPackage.setPrice(cloudPackageCreateModel.getPrice());
    cloudPackageRepository.save(cloudPackage);

    return ResponseModel.done();
  }

  @PreAuthorize("hasAuthority('PACKAGE_UPDATE')")
  public ResponseModel update(Long id, CloudPackageCreateModel cloudPackageCreateModel) {
    CloudPackage cloudPackage =
        cloudPackageRepository.findById(id).orElseThrow(NotFoundException::new);

    cloudPackage.setStorage(cloudPackageCreateModel.getStorage());
    cloudPackage.setPackageTimeLimit(cloudPackageCreateModel.getPackageTimeLimit());
    cloudPackage.setCommunitySizePerSpace(cloudPackageCreateModel.getCommunitySizePerSpace());
    cloudPackage.setEncryptedContent(cloudPackageCreateModel.getEncryptedContent());
    cloudPackage.setMaxCountOfRentedSpaces(cloudPackageCreateModel.getMaxCountOfRentedSpaces());
    cloudPackage.setPrice(cloudPackageCreateModel.getPrice());
    cloudPackage.getPermission().clear();
    cloudPackage.getPermission().putAll(cloudPackageCreateModel.getPermission());
    cloudPackageRepository.save(cloudPackage);

    return ResponseModel.done();
  }

  @PreAuthorize("hasAuthority('PACKAGE_DELETE')")
  public ResponseModel delete(Long id) {
    CloudPackage cloudPackage =
        cloudPackageRepository.findById(id).orElseThrow(NotFoundException::new);

    if (cloudPackage.getPackageType() != PackageType.CUSTOM) {
      throw new InvalidException("error.cloudpackage.delete.type");
    }

    if (userRepository.countByCloudPackageAndDeletedFalse(cloudPackage) > 0) {
      throw new InvalidException("error.cloudpackage.delete.users");
    }

    cloudPackageRepository.delete(cloudPackage);

    return ResponseModel.done();
  }

  @PreAuthorize("hasAuthority('PACKAGE_READ')")
  public ResponseModel get(PageRequest pageRequest) {
    Page<CloudPackageModel> cloudPackageModelPage =
        cloudPackageRepository
            .findAll(pageRequest)
            .map(
                cloudPackage -> {
                  CloudPackageModel cloudPackageModel = new CloudPackageModel(cloudPackage);
                  cloudPackageModel.setNumberOfUsers(
                      userRepository.countByCloudPackageAndDeletedFalse(cloudPackage));
                  return cloudPackageModel;
                });
    return PageResponseModel.done(
        cloudPackageModelPage.getContent(),
        cloudPackageModelPage.getTotalPages(),
        cloudPackageModelPage.getNumber(),
        Long.valueOf(cloudPackageModelPage.getTotalElements()).intValue());
  }

  @PreAuthorize("hasAuthority('PACKAGE_READ')")
  public ResponseModel get(Long id) {
    CloudPackage cloudPackage =
        cloudPackageRepository.findById(id).orElseThrow(NotFoundException::new);
    CloudPackageModel cloudPackageModel = new CloudPackageModel(cloudPackage);
    cloudPackageModel.setNumberOfUsers(
        userRepository.countByCloudPackageAndDeletedFalse(cloudPackage));
    return ResponseModel.done(cloudPackageModel);
  }

  @PreAuthorize("hasAuthority('PACKAGE_ASSIGN')")
  public ResponseModel assign(Long id, Collection<Long> users) {
    CloudPackage cloudPackage =
        cloudPackageRepository.findById(id).orElseThrow(NotFoundException::new);
    if (users.isEmpty()) {
      throw new InvalidException("error.cloudpackage.assign.empty");
    }

    List<User> userList = new ArrayList<>();
    userRepository.findAllById(users).forEach(userList::add);
    userList.addAll(cloudPackage.getUsers());
    cloudPackageRepository.save(cloudPackage);

    return ResponseModel.done();
  }

  @PreAuthorize("hasAuthority('PACKAGE_UN_ASSIGN')")
  public ResponseModel unassign(Long id, Collection<Long> users) {
    CloudPackage cloudPackage = cloudPackageRepository.findById(id).orElseThrow(NotFoundException::new);

    if (users.isEmpty()) {
      throw new InvalidException("error.cloudpackage.assign.empty");
    }

    List<User> userList = new ArrayList<>();
    userRepository.findAllById(users).forEach(userList::add);
    cloudPackage.getUsers().removeAll(userList);
    cloudPackageRepository.save(cloudPackage);

    return ResponseModel.done();
  }

  @Transactional
  @PostConstruct
  public void createCloudPackages() {
    CloudPackage cloudPackage =
        cloudPackageRepository.findByPackageTypeAndNameAndDeletedFalse(
            PackageType.STANDARD, PackageType.STANDARD.name());
    if (cloudPackage == null) {
      cloudPackage = new CloudPackage();
      cloudPackage.setName(PackageType.STANDARD.name());
      cloudPackage.setPrice(0.0);
      cloudPackage.setStorage(10240L);
      cloudPackage.setPackageType(PackageType.STANDARD);
      cloudPackage.setMaxCountOfRentedSpaces(0);
      cloudPackage.setEncryptedContent(false);
      cloudPackage.setCommunitySizePerSpace(10);
      cloudPackage.setNumberOfTags(1000);
      cloudPackage.setNumberOfTagsGroup(100);
      cloudPackageRepository.save(cloudPackage);
      getPermsionForPackage(cloudPackage);
      cloudPackageRepository.save(cloudPackage);
    }
  }

  @Transactional(readOnly = true)
  protected void getPermsionForPackage(CloudPackage finalCloudPackage) {
    permissionRepository
        .findByTypeInAndDeletedFalse(Collections.singleton(UserType.USER))
        .forEach(
            permission -> {
              Byte aByte =
                  finalCloudPackage.getPermission().getOrDefault(permission.getKeyCode(), (byte) 0);
              if (aByte.equals((byte) 0)) {
                finalCloudPackage
                    .getPermission()
                    .put(permission.getKeyCode(), permission.getCode().byteValue());
              } else {
                finalCloudPackage
                    .getPermission()
                    .put(
                        permission.getKeyCode(), (byte) (permission.getCode().byteValue() + aByte));
              }
            });
  }
}

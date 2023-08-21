package com.edumento.b2b.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.FoundationPackage;
import com.edumento.b2b.model.foundationpackage.FoundationPackageCreateModel;
import com.edumento.b2b.model.foundationpackage.FoundationPackageModel;
import com.edumento.b2b.repo.FoundationPackageRepository;
import com.edumento.b2b.repo.FoundationRepository;
import com.edumento.core.exception.ExistException;
import com.edumento.core.exception.InvalidException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.model.PageResponseModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.user.repo.ModuleRepository;

/** Created by ahmad on 4/18/17. */
@Service
public class FoundationPackageService {
  private final FoundationPackageRepository foundationPackageRepository;
  private final ModuleRepository moduleRepository;
  private final FoundationRepository foundationRepository;

  @Autowired
  public FoundationPackageService(FoundationPackageRepository foundationPackageRepository,
      ModuleRepository moduleRepository, FoundationRepository foundationRepository) {
    this.foundationPackageRepository = foundationPackageRepository;
    this.moduleRepository = moduleRepository;
    this.foundationRepository = foundationRepository;
  }

  @Transactional
  @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
  public ResponseModel createFoundationPackage(
      FoundationPackageCreateModel foundationPackageCreateModel) {

    FoundationPackage foundationPackage = foundationPackageRepository
        .findByNameAndDeletedFalse(foundationPackageCreateModel.getName());
    if (foundationPackage != null) {
      throw new ExistException("foundationpackage");
    }
    foundationPackage = new FoundationPackage();
    foundationPackage.setName(foundationPackageCreateModel.getName());
    foundationPackage.setPackageTimeLimit(foundationPackageCreateModel.getPackageTimeLimit());
    foundationPackage.setStorage(foundationPackageCreateModel.getStorage());
    foundationPackage.setBroadcastMessages(foundationPackageCreateModel.getBroadcastMessages());
    foundationPackage.setIntegrationWithSIS(foundationPackageCreateModel.getIntegrationWithSIS());
    foundationPackage.setNumberOfUsers(foundationPackageCreateModel.getNumberOfUsers());
    foundationPackage
        .setNumberOfOrganizations(foundationPackageCreateModel.getNumberOfOrganizations());

    if (!foundationPackageCreateModel.getModules().isEmpty()) {
      var modules = moduleRepository.findAllById(foundationPackageCreateModel.getModules()).stream()
          .collect(Collectors.toList());
      foundationPackage.getModules().addAll(modules);
    }

    foundationPackageRepository.save(foundationPackage);

    return ResponseModel.done(foundationPackage.getId());
  }

  @Transactional
  @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
  public ResponseModel updateFoundationPackage(Long id,
      FoundationPackageCreateModel foundationPackageCreateModel) {

    FoundationPackage foundationPackage =
        foundationPackageRepository.findById(id).orElseThrow(NotFoundException::new);
    if (!foundationPackageCreateModel.getName().equals(foundationPackage.getName())) {
      FoundationPackage tempFoundationPackage = foundationPackageRepository
          .findByNameAndDeletedFalse(foundationPackageCreateModel.getName());
      if (tempFoundationPackage != null && !id.equals(tempFoundationPackage.getId())) {
        throw new ExistException("foundationpackage");
      }
    }

    foundationPackage.setName(foundationPackageCreateModel.getName());
    foundationPackage.setPackageTimeLimit(foundationPackageCreateModel.getPackageTimeLimit());
    foundationPackage.setStorage(foundationPackageCreateModel.getStorage());
    foundationPackage.setBroadcastMessages(foundationPackageCreateModel.getBroadcastMessages());
    foundationPackage.setIntegrationWithSIS(foundationPackageCreateModel.getIntegrationWithSIS());
    foundationPackage.setNumberOfUsers(foundationPackageCreateModel.getNumberOfUsers());
    foundationPackage
        .setNumberOfOrganizations(foundationPackageCreateModel.getNumberOfOrganizations());
    if (!foundationPackageCreateModel.getModules().isEmpty()) {
      if (!foundationPackage.getModules().isEmpty()) {
        foundationPackage.getModules().clear();
        foundationPackageRepository.save(foundationPackage);
      }

      var modules =
          moduleRepository.findAllById(foundationPackageCreateModel.getModules()).stream().toList();
      foundationPackage.getModules().addAll(modules);
    }

    foundationPackageRepository.save(foundationPackage);
    return ResponseModel.done();
  }

  @Transactional
  @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
  public ResponseModel deleteFoundationPackage(Long id) {

    FoundationPackage foundationPackage =
        foundationPackageRepository.findById(id).orElseThrow(NotFoundException::new);


    if (foundationPackage.getFoundation().stream().filter(foundation -> !foundation.isDeleted())
        .count() > 0) {
      throw new InvalidException("error.foundationpackage.foundation");
    }
    foundationPackageRepository.deleteById(id);
    return ResponseModel.done();
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
  public PageResponseModel getAll(PageRequest pageRequest) {
    Page<FoundationPackageModel> foundationPackagePage =
        foundationPackageRepository.findAll(pageRequest).map(FoundationPackageModel::new);
    return PageResponseModel.done(foundationPackagePage.getContent(),
        foundationPackagePage.getTotalPages(), foundationPackagePage.getNumber(),
        Long.valueOf(foundationPackagePage.getTotalElements()).intValue());
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
  public ResponseModel get(Long id) {
    FoundationPackage foundationPackage =
        foundationPackageRepository.findById(id).orElseThrow(NotFoundException::new);
    return ResponseModel.done(new FoundationPackageModel(foundationPackage));
  }

  @Transactional
  @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
  public ResponseModel assign(Long id, List<Long> foundationIds) {
    FoundationPackage foundationPackage =
        foundationPackageRepository.findById(id).orElseThrow(NotFoundException::new);

    if (foundationIds.isEmpty()) {
      throw new InvalidException("error.foundtionpackage.assign.empty");
    }
    List<Foundation> foundations =
        foundationRepository.findAllById(foundationIds).stream().toList();
    if (foundations.isEmpty()) {
      throw new NotFoundException("foundation");
    }
    foundationPackage.getFoundation().addAll(foundations);
    foundationPackageRepository.save(foundationPackage);
    return ResponseModel.done();
  }

  @Transactional
  @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
  public ResponseModel unassign(Long id, List<Long> foundationIds) {
    FoundationPackage foundationPackage = foundationPackageRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("foundationpackage"));
    if (foundationIds.isEmpty()) {
      throw new InvalidException("error.foundtionpackage.assign.empty");
    }
    List<Foundation> foundations = foundationRepository.findAllById(foundationIds);
    if (foundations.isEmpty()) {
      throw new NotFoundException("foundation");
    }
    foundationPackage.getFoundation().removeAll(foundations);
    foundationPackageRepository.save(foundationPackage);
    return ResponseModel.done();
  }
}

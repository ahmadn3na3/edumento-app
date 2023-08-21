package com.edumento.b2c.repos;

import com.edumento.b2c.domain.CloudPackage;
import com.edumento.core.constants.PackageType;
import com.edumento.core.repos.AbstractRepository;

/** Created by ahmad on 4/19/17. */
public interface CloudPackageRepository extends AbstractRepository<CloudPackage, Long> {
  CloudPackage findByPackageTypeAndNameAndDeletedFalse(PackageType packageType, String name);
}

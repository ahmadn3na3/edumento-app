package com.edumento.b2c.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edumento.b2c.domain.CloudPackage;
import com.edumento.core.constants.PackageType;

/** Created by ahmad on 4/19/17. */
public interface CloudPackageRepository extends JpaRepository<CloudPackage, Long> {
	CloudPackage findByPackageTypeAndNameAndDeletedFalse(PackageType packageType, String name);
}

package com.edumento.b2b.model.foundationpackage;

import java.time.ZonedDateTime;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.FoundationPackage;
import com.edumento.core.util.DateConverter;
import com.edumento.user.domain.Module;

/** Created by ahmad on 4/18/17. */
public class FoundationPackageModel extends FoundationPackageCreateModel {
	private Long id;
	private ZonedDateTime creationDate;
	private ZonedDateTime modificationDate;
	private Long foundationCount;

	public FoundationPackageModel() {
	}

	public FoundationPackageModel(FoundationPackage foundationPackage) {
		id = foundationPackage.getId();
		setName(foundationPackage.getName());
		setName(foundationPackage.getName());
		setPackageTimeLimit(foundationPackage.getPackageTimeLimit());
		setStorage(foundationPackage.getStorage());
		setBroadcastMessages(foundationPackage.getBroadcastMessages());
		setIntegrationWithSIS(foundationPackage.getIntegrationWithSIS());
		setNumberOfUsers(foundationPackage.getNumberOfUsers());
		setNumberOfOrganizations(foundationPackage.getNumberOfOrganizations());
		setCreationDate(DateConverter.convertDateToZonedDateTime(foundationPackage.getCreationDate()));
		setModificationDate(DateConverter.convertDateToZonedDateTime(foundationPackage.getLastModifiedDate()));

		if (!foundationPackage.getModules().isEmpty()) {
			foundationPackage.getModules().stream().forEach(new Consumer<Module>() {
			@Override
			public void accept(Module module) {
				getModules().add(module.getId());
			}
		});
		}

		foundationCount = foundationPackage.getFoundation().stream().filter(new Predicate<Foundation>() {
			@Override
			public boolean test(Foundation foundation) {
				return !foundation.isDeleted();
			}
		})
				.count();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ZonedDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(ZonedDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public ZonedDateTime getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(ZonedDateTime modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Long getFoundationCount() {
		return foundationCount;
	}

	public void setFoundationCount(Long foundationCount) {
		this.foundationCount = foundationCount;
	}
}

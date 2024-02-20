package com.edumento.b2b.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.edumento.b2b.domain.Organization;
import com.edumento.b2b.model.organization.OrganizationModel;
import com.edumento.b2b.model.organization.SimpleOrganizationModel;

@Mapper
public interface OrganizationMapper {

	OrganizationMapper INSTANCE = Mappers.getMapper(OrganizationMapper.class);

	OrganizationModel organizationToOrganizationModel(Organization organization);

	SimpleOrganizationModel mapOrganizationToSimpleOrganizationModel(Organization organization);
}

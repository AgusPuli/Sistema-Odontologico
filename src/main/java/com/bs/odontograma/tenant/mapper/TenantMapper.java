package com.bs.odontograma.tenant.mapper;

import com.bs.odontograma.tenant.dto.TenantResponse;
import com.bs.odontograma.tenant.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class TenantMapper {

    @Mapping(target = "planExpired", expression = "java(tenant.isPlanExpired())")
    @Mapping(source = "ivaCondition", target = "ivaCondition")
    @Mapping(source = "iibb", target = "iibb")
    @Mapping(source = "activityStartDate", target = "activityStartDate")
    public abstract TenantResponse toResponse(Tenant tenant);

}
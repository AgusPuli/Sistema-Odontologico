package com.bs.odontograma.periodontogram.mapper;

import com.bs.odontograma.periodontogram.dto.response.PeriodontalSiteResponse;
import com.bs.odontograma.periodontogram.dto.response.PeriodontalToothResponse;
import com.bs.odontograma.periodontogram.dto.response.PeriodontogramResponse;
import com.bs.odontograma.periodontogram.entity.Periodontogram;
import com.bs.odontograma.periodontogram.entity.PeriodontogramSite;
import com.bs.odontograma.periodontogram.entity.PeriodontogramTooth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PeriodontogramMapper {

    @Mapping(target = "patientId", expression = "java(perio.getPatient().getId())")
    @Mapping(target = "teeth", expression = "java(mapTeeth(perio.getTeeth()))")
    PeriodontogramResponse toResponse(Periodontogram perio);

    @Mapping(target = "sites", expression = "java(mapSites(tooth.getSites()))")
    PeriodontalToothResponse toToothResponse(PeriodontogramTooth tooth);

    @Mapping(target = "clinicalAttachmentLevel", expression = "java(computeCAL(site))")
    PeriodontalSiteResponse toSiteResponse(PeriodontogramSite site);

    default List<PeriodontalToothResponse> mapTeeth(List<PeriodontogramTooth> teeth) {
        return teeth == null ? List.of()
                : teeth.stream()
                .sorted((a, b) -> Integer.compare(
                        a.getFdiNumber() == null ? 0 : a.getFdiNumber(),
                        b.getFdiNumber() == null ? 0 : b.getFdiNumber()))
                .map(this::toToothResponse)
                .toList();
    }

    default List<PeriodontalSiteResponse> mapSites(List<PeriodontogramSite> sites) {
        return sites == null ? List.of() : sites.stream().map(this::toSiteResponse).toList();
    }

    /** CAL = probing depth + recession. Null if either component is missing. */
    default Integer computeCAL(PeriodontogramSite s) {
        if (s.getProbingDepth() == null || s.getRecession() == null) return null;
        return s.getProbingDepth() + s.getRecession();
    }
}

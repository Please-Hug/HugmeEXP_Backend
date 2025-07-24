package org.example.hugmeexp.domain.studyRoom.dto.mapper;

import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallRequest;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyHallResponse;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * StudyHall 관련 엔티티와 DTO를 변환하는 Mapper 입니다.
 */
@Mapper(componentModel = "spring")
public interface StudyHallMapper {
    StudyHallResponse toResponseDto(StudyHall studyHall);

    List<StudyHallResponse> toResponseDtos(List<StudyHall> studyHalls);

    // DTO의 필드가 null일 경우, 엔티티의 해당 필드를 무시하도록 설정
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(StudyHallRequest dto, @MappingTarget StudyHall entity);
}
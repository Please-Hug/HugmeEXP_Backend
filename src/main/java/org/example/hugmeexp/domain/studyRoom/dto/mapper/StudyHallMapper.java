package org.example.hugmeexp.domain.studyRoom.dto.mapper;

import org.example.hugmeexp.domain.studyRoom.dto.response.StudyHallResponse;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * StudyHall 관련 엔티티와 DTO를 변환하는 Mapper 입니다.
 */
@Mapper(componentModel = "spring")
public interface StudyHallMapper {
    StudyHallResponse toResponseDto(StudyHall studyHall);

    List<StudyHallResponse> toResponseDtos(List<StudyHall> studyHalls);
}
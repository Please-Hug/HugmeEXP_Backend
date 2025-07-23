package org.example.hugmeexp.domain.studyRoom.dto.mapper;

import org.example.hugmeexp.domain.studyRoom.dto.response.StudyRoomResponse;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoom;
import org.mapstruct.Mapper;

/**
 * StudyRoom 관련 엔티티와 DTO를 변환하는 Mapper 입니다.
 */
@Mapper(componentModel = "spring")
public interface StudyRoomMapper {
    StudyRoomResponse toResponseDto(StudyRoom studyRoom);
}

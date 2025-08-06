package org.example.hugmeexp.domain.studyRoom.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

/**
 * 특정 스터디 홀에 새로운 스터디 룸을 생성하기 위한 요청 DTO 입니다.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyRoomRequest {

    @NotBlank(message = "스터디 룸 이름은 필수입니다.")
    private String name;

    @NotNull(message = "최대 인원 수는 필수입니다.")
    @Positive(message = "최대 인원 수는 양수여야 합니다.")
    private Integer maxNum;

    @URL(message = "썸네일은 유효한 URL 형식이어야 합니다.")
    private String thumbnail;
}
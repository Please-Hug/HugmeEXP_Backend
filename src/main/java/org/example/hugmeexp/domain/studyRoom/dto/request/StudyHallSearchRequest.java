package org.example.hugmeexp.domain.studyRoom.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyHallSearchRequest {

    @Schema(description = "현재 위치 위도", example = "37.5665", required = true)
    @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다.")
    @DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다.")
    private Double latitude;

    @Schema(description = "현재 위치 경도", example = "126.9780", required = true)
    @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다.")
    @DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다.")
    private Double longitude;

    @Schema(description = "검색 반경 (km)", example = "5.0", defaultValue = "10.0")
    @DecimalMin(value = "0.1", message = "검색 반경은 0.1km 이상이어야 합니다.")
    @DecimalMax(value = "50.0", message = "검색 반경은 50km 이하여야 합니다.")
    private Double radius = 10.0; // 기본값 10km

    @Schema(description = "결과 개수 제한", example = "20", defaultValue = "50")
    @Min(value = 1, message = "최소 1개 이상 조회해야 합니다.")
    @Max(value = 100, message = "최대 100개까지 조회 가능합니다.")
    private Integer limit = 50; // 기본값 50개
}
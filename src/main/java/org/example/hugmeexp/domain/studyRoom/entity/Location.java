package org.example.hugmeexp.domain.studyRoom.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.studyRoom.util.DistanceCalculator;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Embeddable  // StudyHall에 임베드될 수 있도록
public class Location {

    /**
     * 위도 (-90.0 ~ 90.0)
     * 한국 위치: 대략 33.0 ~ 38.0
     */
    @NotNull(message = "위도는 필수입니다.")
    @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다.")
    @DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다.")
    private Double latitude;

    /**
     * 경도 (-180.0 ~ 180.0)
     * 한국 위치: 대략 124.0 ~ 132.0
     */
    @NotNull(message = "경도는 필수입니다.")
    @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다.")
    @DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다.")
    private Double longitude;

    /**
     * 상세 주소
     */
    private String address;

    /**
     * 간단한 주소 (구/동 정도)
     */
    private String simpleAddress;

    // 정적 팩터리 메서드들
    public static Location of(Double latitude, Double longitude) {
        return Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    public static Location of(Double latitude, Double longitude, String address) {
        return Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .address(address)
                .build();
    }

    public static Location of(Double latitude, Double longitude, String address, String simpleAddress) {
        return Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .address(address)
                .simpleAddress(simpleAddress)
                .build();
    }

    /**
     * 두 위치 간의 거리 계산
     * @param other 다른 위치
     * @return 거리 (km)
     */
    public Double calculateDistanceTo(Location other) {
        return DistanceCalculator.calculateDistance(
                this.latitude, this.longitude,
                other.latitude, other.longitude
        );
    }

    /**
     * 유효한 한국 좌표인지 확인
     * @return 한국 영역 내 좌표면 true
     */
    public boolean isValidKoreanLocation() {
        if (latitude == null || longitude == null) {
            return false;
        }

        // 한국 영역 대략적 범위
        return latitude >= 32.0 && latitude <= 39.0 &&
                longitude >= 123.0 && longitude <= 132.0;
    }

    /**
     * 위치 정보가 완전한지 확인
     * @return 위도, 경도가 모두 있으면 true
     */
    public boolean isComplete() {
        return latitude != null && longitude != null;
    }
}
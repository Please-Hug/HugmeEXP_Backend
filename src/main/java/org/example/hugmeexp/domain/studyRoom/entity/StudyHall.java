package org.example.hugmeexp.domain.studyRoom.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallRequest;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.hibernate.annotations.BatchSize;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Getter
@Entity
@Table(name = "study_hall",
        indexes = {
                @Index(name = "idx_study_hall_is_deleted", columnList = "isDeleted"),
                @Index(name = "idx_study_hall_location", columnList = "latitude, longitude, isDeleted")
        })
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyHall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyhall_id")
    private Long id;

    @NotBlank(message = "스터디홀 이름은 필수입니다.")
    @Size(max = 100, message = "스터디홀 이름은 100자 이하여야 합니다.")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    @Column(length = 500)
    private String description;

    @Valid
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "longitude")),
            @AttributeOverride(name = "address", column = @Column(name = "address")),
            @AttributeOverride(name = "simpleAddress", column = @Column(name = "simple_address"))
    })
    private Location location;

    @Column(name = "thumbnail_url")
    private String thumbnail;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "studyHall", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StudyRoom> studyRooms;

    @Builder.Default
    @JsonProperty("isDeleted")
    private boolean isDeleted = false;

    public void update(StudyHallRequest requestDto) {
        Optional.ofNullable(requestDto.getName()).ifPresent(name -> this.name = name);
        Optional.ofNullable(requestDto.getDescription()).ifPresent(description -> this.description = description);
        Optional.ofNullable(requestDto.getThumbnail()).ifPresent(thumbnail -> this.thumbnail = thumbnail);
        Optional.ofNullable(requestDto.getOpenTime()).ifPresent(openTime -> this.openTime = openTime);
        Optional.ofNullable(requestDto.getCloseTime()).ifPresent(closeTime -> this.closeTime = closeTime);

        // Location 정보 업데이트
        if (requestDto.getLatitude() != null || requestDto.getLongitude() != null ||
                requestDto.getAddress() != null || requestDto.getSimpleAddress() != null) {

            // 현재 값들 추출 (null-safe)
            Double latitude = requestDto.getLatitude() != null ? requestDto.getLatitude() :
                    (this.location != null ? this.location.getLatitude() : null);
            Double longitude = requestDto.getLongitude() != null ? requestDto.getLongitude() :
                    (this.location != null ? this.location.getLongitude() : null);
            String address = requestDto.getAddress() != null ? requestDto.getAddress() :
                    (this.location != null ? this.location.getAddress() : null);
            String simpleAddress = requestDto.getSimpleAddress() != null ? requestDto.getSimpleAddress() :
                    (this.location != null ? this.location.getSimpleAddress() : null);

            // Location 객체 업데이트
            this.location = Location.of(latitude, longitude, address, simpleAddress);
        }
    }

    public void delete() {
        this.isDeleted = true;
    }

    public int getTotalRoomsCount() {
        return studyRooms != null ? studyRooms.size() : 0;
    }

    public boolean isOpen() {
        if (openTime == null || closeTime == null) {
            return false;
        }

        LocalTime now = LocalTime.now();

        // 24시간 운영인 경우 (예: 00:00 - 00:00)
        if (openTime.equals(closeTime)) {
            return true;
        }

        // 일반적인 경우 (예: 09:00 - 18:00)
        if (openTime.isBefore(closeTime)) {
            return now.isAfter(openTime) && now.isBefore(closeTime);
        }
        // 다음날로 넘어가는 경우 (예: 22:00 - 02:00)
        else {
            return now.isAfter(openTime) || now.isBefore(closeTime);
        }
    }

    // Location 관련 Getter 메서드들
    public Double getLatitude() {
        return location != null ? location.getLatitude() : null;
    }

    public Double getLongitude() {
        return location != null ? location.getLongitude() : null;
    }

    public String getAddress() {
        return location != null ? location.getAddress() : null;
    }

    public String getSimpleAddress() {
        return location != null ? location.getSimpleAddress() : null;
    }
}
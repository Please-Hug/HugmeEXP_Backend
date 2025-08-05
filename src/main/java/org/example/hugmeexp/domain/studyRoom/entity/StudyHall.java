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
@Table(name = "study_hall", indexes = @Index(name = "idx_study_hall_is_deleted", columnList = "isDeleted"))
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

        // Location 업데이트
        if (requestDto.getLatitude() != null || requestDto.getLongitude() != null ||
                requestDto.getAddress() != null || requestDto.getSimpleAddress() != null) {

            if (this.location == null) {
                this.location = new Location();
            }

            this.location = Location.of(
                    requestDto.getLatitude() != null ? requestDto.getLatitude() : this.location.getLatitude(),
                    requestDto.getLongitude() != null ? requestDto.getLongitude() : this.location.getLongitude(),
                    requestDto.getAddress() != null ? requestDto.getAddress() : this.location.getAddress(),
                    requestDto.getSimpleAddress() != null ? requestDto.getSimpleAddress() : this.location.getSimpleAddress()
            );
        }
    }

    public void delete() {
        this.isDeleted = true;
    }

    // 비즈니스 메서드들
    public Double calculateDistanceFrom(Location otherLocation) {
        if (this.location == null) {
            return null;
        }
        return otherLocation.calculateDistanceTo(this.location);
    }

    public int getAvailableRoomsCount() {
        if (studyRooms == null) {
            return 0;
        }
        return studyRooms.size();
    }

    public int getTotalRoomsCount() {
        return studyRooms != null ? studyRooms.size() : 0;
    }

    public boolean isOpen() {
        if (openTime == null || closeTime == null) {
            return false;
        }
        LocalTime now = LocalTime.now();
        return now.isAfter(openTime) && now.isBefore(closeTime);
    }

    public void updateLocation(Location newLocation) {
        this.location = newLocation;
    }

    public void updateInfo(String name, String description, String thumbnail) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public void updateOperatingHours(LocalTime openTime, LocalTime closeTime) {
        this.openTime = openTime;
        this.closeTime = closeTime;
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
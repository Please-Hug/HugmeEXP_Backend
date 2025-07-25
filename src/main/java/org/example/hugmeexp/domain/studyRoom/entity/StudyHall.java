package org.example.hugmeexp.domain.studyRoom.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallRequest;
import org.example.hugmeexp.global.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_hall")
public class StudyHall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_hall_id")
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
    private LocalDateTime openTime;

    @Column(name = "close_time")
    private LocalDateTime closeTime;

    @OneToMany(mappedBy = "studyHall", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StudyRoom> studyRooms;

    private boolean isDeleted = false;

    public void update(StudyHallRequest requestDto) {
        Optional.ofNullable(requestDto.getName()).ifPresent(name -> this.name = name);
        Optional.ofNullable(requestDto.getDescription()).ifPresent(description -> this.description = description);
        Optional.ofNullable(requestDto.getSimpleAddress()).ifPresent(simpleAddress -> this.simpleAddress = simpleAddress);
        Optional.ofNullable(requestDto.getAddress()).ifPresent(address -> this.address = address);
        Optional.ofNullable(requestDto.getLatitude()).ifPresent(latitude -> this.latitude = latitude);
        Optional.ofNullable(requestDto.getLongitude()).ifPresent(longitude -> this.longitude = longitude);
        Optional.ofNullable(requestDto.getThumbnail()).ifPresent(thumbnail -> this.thumbnail = thumbnail);
        Optional.ofNullable(requestDto.getOpenTime()).ifPresent(openTime -> this.openTime = openTime);
        Optional.ofNullable(requestDto.getCloseTime()).ifPresent(closeTime -> this.closeTime = closeTime);
    }

    /**
     * 스터디 홀을 논리적으로 삭제 처리하는 메서드입니다.
     */
    public void delete() {
        this.isDeleted = true;
    }
}

    // 비즈니스 메서드들

    /**
     * 다른 위치로부터의 거리 계산
     */
    public Double calculateDistanceFrom(Location otherLocation) {
        if (this.location == null) {
            return null;
        }
        return otherLocation.calculateDistanceTo(this.location);
    }

    /**
     * 이용 가능한 스터디룸 개수
     */
    public int getAvailableRoomsCount() {
        if (studyRooms == null) {
            return 0;
        }
        // 실제로는 예약 상태를 확인해야 하지만, 임시로 전체 개수 반환
        return studyRooms.size();
    }

    /**
     * 전체 스터디룸 개수
     */
    public int getTotalRoomsCount() {
        return studyRooms != null ? studyRooms.size() : 0;
    }

    /**
     * 현재 운영 중인지 확인
     */
    public boolean isOpen() {
        if (openTime == null || closeTime == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(openTime) && now.isBefore(closeTime);
    }

    /**
     * 위치 정보 업데이트
     */
    public void updateLocation(Location newLocation) {
        this.location = newLocation;
    }

    /**
     * 스터디홀 정보 업데이트
     */
    public void updateInfo(String name, String description, String thumbnail) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        this.description = description;
        this.thumbnail = thumbnail;
    }

    /**
     * 운영시간 업데이트
     */
    public void updateOperatingHours(LocalDateTime openTime, LocalDateTime closeTime) {
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    // Getter 메서드들 (Location 관련)
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
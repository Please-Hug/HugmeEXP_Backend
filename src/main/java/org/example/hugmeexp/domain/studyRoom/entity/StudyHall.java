package org.example.hugmeexp.domain.studyRoom.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallRequest;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyHall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="studyhall_id")
    private Long id;

    private String name;

    private String description;

    private String simpleAddress;

    private String address;

    private Double latitude;

    private Double longitude;

    private String thumbnail;

    private LocalDateTime openTime;

    private LocalDateTime closeTime;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "studyHall", cascade = CascadeType.ALL, orphanRemoval = true)
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

package org.example.hugmeexp.domain.studyRoom.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallRequest;
import org.example.hugmeexp.global.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyHall extends BaseEntity {

    private boolean isDeleted = false;

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

    @OneToMany(mappedBy = "studyHall", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyRoom> studyRooms;

    /**
     * 스터디 홀 정보를 수정하는 메서드입니다.
     */
    public void update(StudyHallRequest requestDto) {
        this.name = requestDto.getName();
        this.description = requestDto.getDescription();
        this.simpleAddress = requestDto.getSimpleAddress();
        this.address = requestDto.getAddress();
        this.latitude = requestDto.getLatitude();
        this.longitude = requestDto.getLongitude();
        this.thumbnail = requestDto.getThumbnail();
        this.openTime = requestDto.getOpenTime();
        this.closeTime = requestDto.getCloseTime();
    }

    /**
     * 스터디 홀을 논리적으로 삭제 처리하는 메서드입니다.
     */
    public void delete() {
        this.isDeleted = true;
    }
}

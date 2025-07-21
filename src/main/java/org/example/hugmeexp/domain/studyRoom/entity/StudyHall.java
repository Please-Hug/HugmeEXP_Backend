package org.example.hugmeexp.domain.studyRoom.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.global.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany(mappedBy = "studyRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyRoom> studyRooms;
}

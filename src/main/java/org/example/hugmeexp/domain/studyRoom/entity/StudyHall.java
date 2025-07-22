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

//    @OneToMany(mappedBy = "studyHall")
//    ➡️ "This relationship is mapped by the studyHall field over in the StudyRoom entity."
//    ➡️ "이 관계는 StudyRoom 엔티티에 있는 studyHall 필드에 의해서 매핑됩니다."

    @OneToMany(mappedBy = "studyHall", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyRoom> studyRooms;
}

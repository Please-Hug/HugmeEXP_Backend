package org.example.hugmeexp.domain.studyRoom.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomReservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="studyroom_reservation_id")
    private Long id;

    private LocalDateTime reservationStart;

    private LocalDateTime reservationEnd;

    private Integer partyNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyroom_id")
    private StudyRoom studyRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

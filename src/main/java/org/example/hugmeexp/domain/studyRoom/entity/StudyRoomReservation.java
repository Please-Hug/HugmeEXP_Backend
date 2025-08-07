package org.example.hugmeexp.domain.studyRoom.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "study_room_reservation",
        indexes = {
                // 시간 기반 예약 검색 최적화
                @Index(name = "idx_reservation_time_search",
                        columnList = "studyroom_id, reservation_start, reservation_end",
                        unique = false)
        })
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomReservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyroom_reservation_id")
    private Long id;

    @Column(name = "reservation_start", nullable = false)
    private LocalDateTime reservationStart;

    @Column(name = "reservation_end", nullable = false)
    private LocalDateTime reservationEnd;

    @Column(name = "party_num", nullable = false)
    private Integer partyNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyroom_id", nullable = false)
    private StudyRoom studyRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
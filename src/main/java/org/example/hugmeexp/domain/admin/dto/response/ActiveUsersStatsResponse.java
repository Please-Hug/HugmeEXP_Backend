package org.example.hugmeexp.domain.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 활성 사용자 통계 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveUsersStatsResponse {

    /**
     * 전체 사용자 수
     */
    private int totalUsers;

    /**
     * 최근 30일 내 출석한 활성 사용자 수
     */
    private int activeUsers;

    /**
     * 활성 사용자 비율 (0.0 ~ 1.0)
     */
    private double activeUserRate;

    /**
     * 기준 날짜 (계산 기준일)
     */
    private String baseDate;
}
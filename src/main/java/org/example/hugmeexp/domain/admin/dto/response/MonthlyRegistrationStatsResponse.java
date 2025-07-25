package org.example.hugmeexp.domain.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 월별 가입자 통계 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRegistrationStatsResponse {

    /**
     * 년월 (yyyy-MM 형식)
     */
    private String month;

    /**
     * 해당 월 가입자 수
     */
    private int count;

    /**
     * 전월 대비 증감률 (선택사항)
     */
    private Double growthRate;
}
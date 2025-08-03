package org.example.hugmeexp.domain.recruitment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder(toBuilder = true)
public class RecruitmentSearchConditionDTO {

    private Integer salaryMin;
    private Integer salaryMax;
    private Integer experienceMin;
    private Integer experienceMax;
    private Integer education;
    private List<Long> techStacks;
    private String workLocation;
    private List<Long> tags;
    private BigDecimal topLeftLat; // 북쪽(큰 값)
    private BigDecimal topLeftLng; // 서쪽(작은 값)
    private BigDecimal bottomRightLat; // 남쪽(작은 값)
    private BigDecimal bottomRightLng; // 동쪽(큰 값)

    private Long techStackCount;
    private Long tagCount;

}

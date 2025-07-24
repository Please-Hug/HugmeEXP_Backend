package org.example.hugmeexp.domain.recruitment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitmentSearchConditionDTO {

    private Integer salaryMin;
    private Integer salaryMax;
    private Integer experience;
    private Integer education;
    private List<Long> techStacks;
    private String workLocation;
    private List<Long> tags;
    private BigDecimal topLeftLat;
    private BigDecimal topLeftLng;
    private BigDecimal bottomRightLat;
    private BigDecimal bottomRightLng;

}

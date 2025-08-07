package org.example.hugmeexp.domain.recruitment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder
public class RecruitmentResponseDTO {

    private Long id;
    private String recruitmentSourceId;
    private String title;
    private String companyName;
    private String companyImageUrl;
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;
    private Integer experienceMin;
    private Integer experienceMax;
    private String workLocation;
    private BigDecimal latitude; // 위도
    private BigDecimal longitude; // 경도

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;

    // JPA 에서 사용되는 생성자
    @QueryProjection
    public RecruitmentResponseDTO(Long id, String recruitmentSourceId, String title, String companyName, String companyImageUrl,
                                  LocalDateTime dueDate, Integer experienceMin, Integer experienceMax, String workLocation,
                                  BigDecimal latitude, BigDecimal longitude, LocalDateTime modifiedAt) {
        this.id = id;
        this.recruitmentSourceId = recruitmentSourceId;
        this.title = title;
        this.companyName = companyName;
        this.companyImageUrl = companyImageUrl;
        this.dueDate = dueDate;
        this.experienceMin = experienceMin;
        this.experienceMax = experienceMax;
        this.workLocation = workLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.modifiedAt = modifiedAt;
    }

    // 서비스 단에서 Recruitment 엔티티를 DTO로 변환하는 메서드
    public static RecruitmentResponseDTO from(Recruitment recruitment) {
        return RecruitmentResponseDTO.builder()
                .id(recruitment.getId())
                .recruitmentSourceId(recruitment.getRecruitmentSourceId())
                .title(recruitment.getTitle())
                .companyName(recruitment.getCompany().getCompanyName())
                .companyImageUrl(recruitment.getCompany().getCompanyImageUrl())
                .dueDate(recruitment.getDueDate())
                .experienceMin(recruitment.getExperienceMin())
                .experienceMax(recruitment.getExperienceMax())
                .workLocation(recruitment.getWorkLocation())
                .latitude(recruitment.getCompany().getLatitude())
                .longitude(recruitment.getCompany().getLongitude())
                .modifiedAt(recruitment.getModifiedAt())
                .build();
    }
}

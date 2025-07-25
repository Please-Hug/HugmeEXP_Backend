package org.example.hugmeexp.domain.recruitment.dto;

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
    private String title;
    private String companyName;
    private String companyImageUrl;
    private LocalDateTime dueDate;
    private Integer experience;
    private String workLocation;
    private BigDecimal latitude; // 위도
    private BigDecimal longitude; // 경도

    // JPA 에서 사용되는 생성자
    public RecruitmentResponseDTO(Long id, String title, String companyName, String companyImageUrl,
                                  LocalDateTime dueDate, Integer experience, String workLocation,
                                  BigDecimal latitude, BigDecimal longitude) {
        this.id = id;
        this.title = title;
        this.companyName = companyName;
        this.companyImageUrl = companyImageUrl;
        this.dueDate = dueDate;
        this.experience = experience;
        this.workLocation = workLocation;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // 서비스 단에서 Recruitment 엔티티를 DTO로 변환하는 메서드
    public static RecruitmentResponseDTO from(Recruitment recruitment) {
        return RecruitmentResponseDTO.builder()
                .id(recruitment.getId())
                .title(recruitment.getTitle())
                .companyName(recruitment.getCompany().getCompanyName())
                .companyImageUrl(recruitment.getCompany().getCompanyImageUrl())
                .dueDate(recruitment.getDueDate())
                .experience(recruitment.getExperience())
                .workLocation(recruitment.getWorkLocation())
                .latitude(recruitment.getCompany().getLatitude())
                .longitude(recruitment.getCompany().getLongitude())
                .build();
    }
}

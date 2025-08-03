package org.example.hugmeexp.domain.recruitment.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentRequestDTO;
import org.example.hugmeexp.domain.recruitment.enums.SourceType;
import org.example.hugmeexp.global.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "recruitment")
public class Recruitment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer education;
    @Column(nullable = false)
    private Integer experienceMin;
    @Column(nullable = false)
    private Integer experienceMax;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String qualification; // 자격 요건

    @Lob
    @Column(columnDefinition = "TEXT")
    private String advantage; // 우대 사항

    @Lob
    @Column(columnDefinition = "TEXT")
    private String welfare; // 복지 혜택

    private String workLocation; // 근무지

    @Column(precision = 15, scale = 8)
    private BigDecimal latitude; // 위도

    @Column(precision = 15, scale = 8)
    private BigDecimal longitude; // 경도

    @Column(name = "salary_min")
    private Integer salaryMin;

    @Column(name = "salary_max")
    private Integer salaryMax;

    private String link;

    @Enumerated(EnumType.STRING)
    private SourceType source; // WANTED: 원티드, JUMPIT: 점핏

    private LocalDateTime dueDate;

    @Column(unique = true)
    private Long recruitmentSourceId; // 외부 시스템에서의 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @Setter
    private Company company;

    // 중간 테이블 관계
    @OneToMany(mappedBy = "recruitment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TechStack> techStacks = new ArrayList<>();

    @OneToMany(mappedBy = "recruitment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @Column(nullable = false)
    private String sourceId;

    public void updateFromRequest(@Valid RecruitmentRequestDTO requestDTO) {
        title = requestDTO.getTitle();
        education = requestDTO.getEducation();
        experienceMin = requestDTO.getExperienceMin();
        experienceMax = requestDTO.getExperienceMax();
        qualification = requestDTO.getQualification();
        advantage = requestDTO.getAdvantage();
        welfare = requestDTO.getWelfare();
        workLocation = requestDTO.getWorkLocation();
        latitude = requestDTO.getLatitude();
        longitude = requestDTO.getLongitude();
        salaryMin = requestDTO.getSalaryMin();
        salaryMax = requestDTO.getSalaryMax();
        link = requestDTO.getLink();
        source = requestDTO.getSource();
        dueDate = requestDTO.getDueDate();
        sourceId = requestDTO.getSourceId();
    }
}

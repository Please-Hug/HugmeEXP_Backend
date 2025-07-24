package org.example.hugmeexp.domain.recruitment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.recruitment.enums.SourceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "recruitment")
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int education;
    @Column(nullable = false)
    private int experience;

    private String qualification; // 자격 요건
    private String advantage; // 우대 사항
    private String welfare; // 복지 혜택
    private String workLocation; // 근무지

    private BigDecimal latitude; // 위도
    private BigDecimal longitude; // 경도

    @Column(name = "salary_min")
    private int salaryMin;

    @Column(name = "salary_max")
    private int salaryMax;

    private String link;

    @Enumerated(EnumType.STRING)
    private SourceType source; // 0: 원티드, 1: 점핏

    private LocalDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // 중간 테이블 관계
    @OneToMany(mappedBy = "recruitment")
    private List<TechStack> techStacks;

    @OneToMany(mappedBy = "recruitment")
    private List<Tag> tags;




}

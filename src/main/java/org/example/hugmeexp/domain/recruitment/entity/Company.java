package org.example.hugmeexp.domain.recruitment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String companyAddress;

    @Column(precision = 15, scale = 8) // 위도와 경도를 저장하기 위해 BigDecimal 사용
    private BigDecimal latitude; // 위도
    @Column(precision = 15, scale = 8)
    private BigDecimal longitude; // 경도

    @Column(nullable = false)
    private LocalDate establishmentDate;

    private String companyImageUrl; // 회사 이미지 URL

    @Column(columnDefinition = "TEXT") // TEXT 타입으로도 저장 가능(길이 제한 없음)
    private String companyDescription;

    @Column(unique = true)
    private String companySourceId;

}

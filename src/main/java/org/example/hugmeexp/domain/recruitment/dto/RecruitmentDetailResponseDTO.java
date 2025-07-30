package org.example.hugmeexp.domain.recruitment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RecruitmentDetailResponseDTO {

    private Long id;
    private String title;
    private String companyName;
    private String companyImageUrl;
    private String companyAddress;
    private LocalDate establishmentDate;
    private String companyDescription;
    private LocalDateTime dueDate;
    private Integer experience;
    private Integer education;
    private Integer salaryMin;
    private Integer salaryMax;
    private List<TechStackDTO> techStacks;
    private String advantage;
    private String qualifications;
    private String welfare;
    private String link;
}

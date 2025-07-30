package org.example.hugmeexp.domain.recruitment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentDetailResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentSearchConditionDTO;
import org.example.hugmeexp.domain.recruitment.dto.TechStackDTO;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.example.hugmeexp.domain.recruitment.exception.RecruitmentNotFoundException;
import org.example.hugmeexp.domain.recruitment.repository.RecruitmentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;

    /**
     * 채용 공고 목록을 조회합니다.
     * 검색 조건에 따라 필터링된 채용 공고 목록을 반환합니다.
     *
     * @param cond 검색 조건 DTO
     * @return 채용 공고 목록
     */
    public List<RecruitmentResponseDTO> listRecruitments(RecruitmentSearchConditionDTO cond) {
        RecruitmentSearchConditionDTO enrichedCond = cond.toBuilder()
                .techStacks((cond.getTechStacks() == null || cond.getTechStacks().isEmpty()) ? null : cond.getTechStacks())
                .tags((cond.getTags() == null || cond.getTags().isEmpty()) ? null : cond.getTags())
                .techStackCount((cond.getTechStacks() == null || cond.getTechStacks().isEmpty()) ? null : (long) cond.getTechStacks().size())
                .tagCount((cond.getTags() == null || cond.getTags().isEmpty()) ? null : (long) cond.getTags().size())
                .build();

        return recruitmentRepository.findBySearchConditions(enrichedCond);
    }

    /**
     * 최신 채용 공고 목록을 조회합니다.
     * 수정일 기준으로 정렬된 최신 채용 공고를 반환합니다.
     *
     * @return 최신 채용 공고 목록
     */
    public List<RecruitmentResponseDTO> findLatestRecruitments(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return recruitmentRepository.findLatestRecruitments(pageable);
    }

    @Transactional(readOnly = true)
    public RecruitmentDetailResponseDTO getRecruitmentDetail(Long id){
        Recruitment recruitment = recruitmentRepository.findDetailById(id).orElseThrow(() -> new RecruitmentNotFoundException());

        return RecruitmentDetailResponseDTO.builder()
                .id(recruitment.getId())
                .title(recruitment.getTitle())
                .companyName(recruitment.getCompany().getCompanyName())
                .companyImageUrl(recruitment.getCompany().getCompanyImageUrl())
                .companyAddress(recruitment.getCompany().getCompanyAddress())
                .establishmentDate(recruitment.getCompany().getEstablishmentDate())
                .companyDescription(recruitment.getCompany().getCompanyDescription())
                .dueDate(recruitment.getDueDate())
                .experience(recruitment.getExperience())
                .education(recruitment.getEducation())
                .salaryMin(recruitment.getSalaryMin())
                .salaryMax(recruitment.getSalaryMax())
                .techStacks(recruitment.getTechStacks().stream()
                        .map(ts -> TechStackDTO.builder()
                            .labelKo(ts.getTechItem().getKoreanName())
                            .labelEn(ts.getTechItem().getEnglishName())
                            .iconUrl(ts.getTechItem().getIconUrl())
                            .build())
                        .toList())
                .advantage(recruitment.getAdvantage())
                .qualifications(recruitment.getQualification())
                .welfare(recruitment.getWelfare())
                .link(recruitment.getLink())
                .build();
    }
}

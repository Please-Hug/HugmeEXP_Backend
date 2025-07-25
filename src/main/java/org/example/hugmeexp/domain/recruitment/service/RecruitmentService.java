package org.example.hugmeexp.domain.recruitment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentSearchConditionDTO;
import org.example.hugmeexp.domain.recruitment.repository.RecruitmentRepository;
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
}

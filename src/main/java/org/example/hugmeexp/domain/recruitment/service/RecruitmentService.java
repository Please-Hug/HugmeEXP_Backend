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

    public List<RecruitmentResponseDTO> listRecruitments(RecruitmentSearchConditionDTO cond) {

        RecruitmentSearchConditionDTO enrichedCond = cond.toBuilder()
                .techStackCount(cond.getTechStacks() == null ? null : (long) cond.getTechStacks().size())
                .tagCount(cond.getTags() == null ? null : (long) cond.getTags().size())
                .build();

        return recruitmentRepository.findBySearchConditions(enrichedCond);
    }
}

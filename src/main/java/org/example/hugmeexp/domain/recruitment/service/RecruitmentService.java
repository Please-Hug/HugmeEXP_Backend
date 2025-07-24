package org.example.hugmeexp.domain.recruitment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentSearchConditionDTO;
import org.example.hugmeexp.domain.recruitment.repository.RecruitmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;

    public List<RecruitmentResponseDTO> listRecruitments(RecruitmentSearchConditionDTO cond) {

        List<Long> techStacks = cond.getTechStacks();
        List<Long> tags = cond.getTags();

        Long techStackCount = (techStacks == null) ? null : (long) techStacks.size();
        Long tagCount = (tags == null) ? null : (long) tags.size();

        return recruitmentRepository.findBySearchConditions(
                cond.getSalaryMin(),
                cond.getSalaryMax(),
                cond.getExperience(),
                cond.getEducation(),
                cond.getWorkLocation(),
                cond.getTopLeftLat(),
                cond.getTopLeftLng(),
                cond.getBottomRightLat(),
                cond.getBottomRightLng(),
                techStacks,
                tags,
                techStackCount,
                tagCount
        );
    }
}

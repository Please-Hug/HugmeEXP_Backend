package org.example.hugmeexp.domain.recruitment.repository;

import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentSearchConditionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface RecruitmentRepositoryCustom {

    Page<RecruitmentResponseDTO> findBySearchConditions(RecruitmentSearchConditionDTO cond, Pageable pageable);
}

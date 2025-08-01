package org.example.hugmeexp.domain.recruitment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentCompanySearchResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentDetailResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentSearchConditionDTO;
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

    /**
     * 채용 공고 상세 정보를 조회합니다.
     * ID에 해당하는 채용 공고의 상세 정보를 반환합니다.
     *
     * @param id 채용 공고 ID
     * @return 채용 공고 상세 정보 DTO
     */
    @Transactional(readOnly = true)
    public RecruitmentDetailResponseDTO getRecruitmentDetail(Long id){
        Recruitment recruitment = recruitmentRepository.findDetailById(id).orElseThrow(() -> new RecruitmentNotFoundException());

        return RecruitmentDetailResponseDTO.from(recruitment);
    }

    /**
     * 키워드로 채용 공고를 검색합니다.
     * 회사 이름 또는 공고 제목에 키워드가 포함된 채용 공고를 반환합니다.
     *
     * @param keyword 검색 키워드 (회사명 또는 공고 제목 일부)
     * @param limit 최대 조회 개수
     * @return 키워드에 해당하는 채용 공고 목록 (공고 ID, 제목, 회사명 등 포함)
     */
    public List<RecruitmentCompanySearchResponseDTO> findByKeyword(String keyword, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Recruitment> recruitments = recruitmentRepository.findByKeyword(keyword, pageable);

        return recruitments.stream()
                .map(RecruitmentCompanySearchResponseDTO::from)
                .toList();

    }

    /**
     * ID에 해당하는 채용 공고를 조회합니다.
     *
     * @param recruitmentId 채용 공고 ID
     * @return 채용 공고 객체
     */
    @Transactional(readOnly = true)
    public Recruitment getRecruitmentById(Long recruitmentId){
        return recruitmentRepository.findById(recruitmentId).orElseThrow(() -> new RecruitmentNotFoundException());
    }

}

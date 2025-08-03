package org.example.hugmeexp.domain.recruitment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.dto.*;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.example.hugmeexp.domain.recruitment.exception.RecruitmentNotFoundException;
import org.example.hugmeexp.domain.recruitment.repository.RecruitmentRepository;
import org.example.hugmeexp.domain.recruitment.repository.TagItemRepository;
import org.example.hugmeexp.domain.recruitment.repository.TechItemRepository;
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
    private final TechItemRepository techItemRepository;
    private final TagItemRepository tagItemRepository;

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

    /**
     * 채용 공고 필터 옵션을 조회합니다.
     * 교육 수준, 경력, 기술 스택, 근무 지역, 태그, 급여 범위 등의 필터 옵션을 반환합니다.
     *
     * @return RecruitmentFilterResponseDTO 필터 옵션 DTO
     */
    public RecruitmentFilterResponseDTO getFilterOptions(){
        List<EducationOptionDTO> educationOptions = List.of(
                new EducationOptionDTO("무관",0),
                new EducationOptionDTO("고졸",10),
                new  EducationOptionDTO("초대졸",20),
                new EducationOptionDTO("대졸",30),
                new EducationOptionDTO("석사",40),
                new EducationOptionDTO("박사",50)
        );

        List<Integer> experienceOptions = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<TechStackDTO> techStacks = techItemRepository.findAll().stream()
                .map(TechStackDTO::from)
                .toList();

        List<String> workLocations = List.of("판교", "강남", "구로");

        List<TagDTO> tags = tagItemRepository.findAll().stream()
                .map(TagDTO::from)
                .toList();

        SalaryRangeDTO salaryRange = SalaryRangeDTO.builder()
                .min(0)
                .max(10000)
                .build();

        return RecruitmentFilterResponseDTO.builder()
                .educationOptions(educationOptions)
                .experienceOptions(experienceOptions)
                .techStacks(techStacks)
                .workLocations(workLocations)
                .tags(tags)
                .salaryRange(salaryRange)
                .build();
    }

}

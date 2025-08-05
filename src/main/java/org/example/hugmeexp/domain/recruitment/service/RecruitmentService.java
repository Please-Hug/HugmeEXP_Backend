package org.example.hugmeexp.domain.recruitment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.dto.*;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.example.hugmeexp.domain.recruitment.exception.RecruitmentNotFoundException;
import org.example.hugmeexp.domain.recruitment.dto.TechItemRequestDTO;
import org.example.hugmeexp.domain.recruitment.dto.TagRequestDTO;
import org.example.hugmeexp.domain.recruitment.entity.TechItem;
import org.example.hugmeexp.domain.recruitment.entity.TechStack;
import org.example.hugmeexp.domain.recruitment.entity.Tag;
import org.example.hugmeexp.domain.recruitment.entity.TagItem;
import org.example.hugmeexp.domain.recruitment.entity.Company;
import org.example.hugmeexp.domain.recruitment.repository.RecruitmentRepository;
import org.example.hugmeexp.domain.recruitment.repository.TagItemRepository;
import org.example.hugmeexp.domain.recruitment.repository.TechItemRepository;
import org.example.hugmeexp.domain.recruitment.repository.TechStackRepository;
import org.example.hugmeexp.domain.recruitment.repository.TagRepository;
import org.example.hugmeexp.domain.recruitment.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final TechItemRepository techItemRepository;
    private final TechStackRepository techStackRepository;
    private final TagRepository tagRepository;
    private final TagItemRepository tagItemRepository;
    private final CompanyRepository companyRepository;
    static final List<EducationOptionDTO> EDUCATION_OPTIONS = List.of(
            new EducationOptionDTO("무관", 0),
            new EducationOptionDTO("고졸", 10),
            new EducationOptionDTO("초대졸", 20),
            new EducationOptionDTO("대졸", 30),
            new EducationOptionDTO("석사", 40),
            new EducationOptionDTO("박사", 50)
    );
    static final List<Integer> EXPERIENCE_OPTIONS = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    static final List<String> WORK_LOCATIONS  = List.of("판교", "강남", "구로");
    static final int DEFAULT_MIN_SALARY = 0;
    static final int DEFAULT_MAX_SALARY = 10000;

    /**
     * 채용 공고 목록을 조회합니다.
     * 검색 조건에 따라 필터링된 채용 공고를 페이지 단위로 반환합니다.
     *
     * @param cond 검색 조건 DTO
     * @param page 페이지 번호 (0부터 시작)
     * @return 필터링된 채용 공고 목록 (RecruitmentResponseDTO)
     */
    public Page<RecruitmentResponseDTO> listRecruitments(RecruitmentSearchConditionDTO cond, int page) {
        RecruitmentSearchConditionDTO enrichedCond = cond.toBuilder()
                .techStacks((cond.getTechStacks() == null || cond.getTechStacks().isEmpty()) ? null : cond.getTechStacks())
                .tags((cond.getTags() == null || cond.getTags().isEmpty()) ? null : cond.getTags())
                .techStackCount((cond.getTechStacks() == null || cond.getTechStacks().isEmpty()) ? null : (long) cond.getTechStacks().size())
                .tagCount((cond.getTags() == null || cond.getTags().isEmpty()) ? null : (long) cond.getTags().size())
                .build();

        Pageable pageable = PageRequest.of(page, 40);

        return recruitmentRepository.findBySearchConditions(enrichedCond, pageable);
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

        List<TechStackDTO> techStacks = techItemRepository.findAll().stream()
                .map(TechStackDTO::from)
                .toList();

        List<TagDTO> tags = tagItemRepository.findAll().stream()
                .map(TagDTO::from)
                .toList();

        SalaryRangeDTO salaryRange = SalaryRangeDTO.builder()
                .min(DEFAULT_MIN_SALARY)
                .max(DEFAULT_MAX_SALARY)
                .build();

        return RecruitmentFilterResponseDTO.builder()
                .educationOptions(EDUCATION_OPTIONS)
                .experienceOptions(EXPERIENCE_OPTIONS)
                .techStacks(techStacks)
                .workLocations(WORK_LOCATIONS)
                .tags(tags)
                .salaryRange(salaryRange)
                .build();
    }


    @Transactional
    public RecruitmentResponseDTO createOrUpdateRecruitment(RecruitmentRequestDTO requestDTO) {
        // 태그 및 기술 스택의 중복 제거 및 정렬
        Set<TagRequestDTO> uniqueTags = new TreeSet<>();
        if (requestDTO.getTags() != null) {
            uniqueTags.addAll(requestDTO.getTags());
        }
        requestDTO.setTags(new ArrayList<>(uniqueTags));
        Set<TechItemRequestDTO> uniqueTechItems = new TreeSet<>();
        if (requestDTO.getRequiredSkills() != null) {
            uniqueTechItems.addAll(requestDTO.getRequiredSkills());
        }
        requestDTO.setRequiredSkills(new ArrayList<>(uniqueTechItems));
        
        Optional<Recruitment> existingRecruitment = recruitmentRepository.findByRecruitmentSourceId(requestDTO.getRecruitmentSourceId());
        // 이미 존재한다면 업데이트
        Recruitment recruitment = existingRecruitment
                .map((existing) -> updateRecruitment(existing, requestDTO))
                .orElseGet(() -> createRecruitment(requestDTO, uniqueTags, uniqueTechItems));

        return RecruitmentResponseDTO.from(recruitment);
    }

    private Recruitment createRecruitment(RecruitmentRequestDTO requestDTO, Set<TagRequestDTO> uniqueTags, Set<TechItemRequestDTO> uniqueTechItems) {
        // Company를 먼저 저장 (transient 문제 해결)
        //Company savedCompany = companyRepository.save(requestDTO.getCompany().toEntity());
        Company company = requestDTO.getCompany().toEntity();
        // Company가 이미 존재하는지 확인
        Optional<Company> existingCompany = companyRepository.findByCompanySourceId(company.getCompanySourceId());
        Company savedCompany = existingCompany.orElseGet(() -> companyRepository.save(company));

        // Recruitment 엔티티 생성 시 저장된 Company 사용
        Recruitment recruitment = requestDTO.toEntity();
        recruitment.setCompany(savedCompany);

        recruitment = recruitmentRepository.save(recruitment);

        // 기술 스택 처리를 별도 메서드로 분리
        processTechStacks(recruitment, uniqueTechItems);

        // 태그 처리 추가
        processTags(recruitment, uniqueTags);

        return recruitment;
    }

    private Recruitment updateRecruitment(Recruitment recruitment, RecruitmentRequestDTO requestDTO) {
        // 기존 채용 공고 업데이트
        recruitment.updateFromRequest(requestDTO);

        // 기술 스택 차이를 비교하여 효율적으로 업데이트
        updateTechStacksEfficiently(recruitment, requestDTO.getRequiredSkills());

        // 태그 차이를 비교하여 효율적으로 업데이트
        updateTagsEfficiently(recruitment, requestDTO.getTags());

        return recruitment;
    }

    /**
     * 기존 기술 스택과 새로운 기술 스택을 비교하여 효율적으로 업데이트
     */
    private void updateTechStacksEfficiently(Recruitment recruitment, List<TechItemRequestDTO> newRequiredSkills) {
        if (newRequiredSkills == null) {
            newRequiredSkills = List.of();
        }

        // 새로운 기술명 집합
        Set<String> newTechNames = newRequiredSkills.stream()
                .map(TechItemRequestDTO::getEnglishName)
                .collect(Collectors.toSet());

        // 기존 기술명 집합
        Set<String> existingTechNames = recruitment.getTechStacks().stream()
                .map(techStack -> techStack.getTechItem().getEnglishName())
                .collect(Collectors.toSet());

        // 삭제할 기술 스택 (기존에는 있지만 새로운 요청에는 없는 것들)
        Set<String> techNamesToRemove = existingTechNames.stream()
                .filter(techName -> !newTechNames.contains(techName))
                .collect(Collectors.toSet());

        // 추가할 기술명 (새로운 요청에는 있지만 기존에는 없는 것들)
        Set<String> techNamesToAdd = newTechNames.stream()
                .filter(techName -> !existingTechNames.contains(techName))
                .collect(Collectors.toSet());

        // 삭제할 기술 스택 제거
        if (!techNamesToRemove.isEmpty()) {
            List<TechStack> techStacksToRemove = recruitment.getTechStacks().stream()
                    .filter(techStack -> techNamesToRemove.contains(techStack.getTechItem().getEnglishName()))
                    .toList();

            // 벌크 삭제 쿼리 사용 (더 효율적)
            List<Long> techStackIdsToRemove = techStacksToRemove.stream()
                    .map(TechStack::getId)
                    .toList();

            techStackRepository.deleteAllByIdInBatch(techStackIdsToRemove);
            recruitment.getTechStacks().removeAll(techStacksToRemove);
        }

        // 추가할 기술 스택 생성
        if (!techNamesToAdd.isEmpty()) {
            Set<TechItemRequestDTO> skillsToAdd = newRequiredSkills.stream()
                    .filter(skill -> techNamesToAdd.contains(skill.getEnglishName()))
                    .collect(Collectors.toSet());

            processTechStacks(recruitment, skillsToAdd);
        }
    }

    /**
     * 기술 스택을 처리하는 메서드
     * 1. 기존 TechItem 조회
     * 2. 없는 TechItem 생성
     * 3. TechStack 생성 및 저장
     */
    private void processTechStacks(Recruitment recruitment, Set<TechItemRequestDTO> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return;
        }

        // 필요한 기술명 추출
        Set<String> techNames = requiredSkills.stream()
                .map(TechItemRequestDTO::getEnglishName)
                .collect(Collectors.toSet());

        // 기존 TechItem 조회 및 새로운 TechItem 생성
        List<TechItem> allTechItems = getOrCreateTechItems(techNames, requiredSkills);

        // TechStack 생성 및 저장
        Set<TechStack> techStacks = allTechItems.stream()
                .map(techItem -> TechStack.builder()
                        .techItem(techItem)
                        .recruitment(recruitment)
                        .build())
                .collect(Collectors.toSet());

        techStackRepository.saveAll(techStacks);
        recruitment.getTechStacks().addAll(techStacks);
    }

    /**
     * TechItem을 조회하거나 생성하는 메서드
     */
    private List<TechItem> getOrCreateTechItems(Set<String> techNames, Set<TechItemRequestDTO> requiredSkills) {
        // 기존 TechItem 조회
        List<TechItem> existingTechItems = techItemRepository.findAllByEnglishNameIn(techNames);

        Set<String> existingTechNames = existingTechItems.stream()
                .map(TechItem::getEnglishName)
                .collect(Collectors.toSet());

        // 새로운 TechItem 생성
        List<TechItem> newTechItems = requiredSkills.stream()
                .filter(skill -> !existingTechNames.contains(skill.getEnglishName()))
                .map(TechItemRequestDTO::toEntity)
                .toList();

        // 새로운 TechItem 저장
        if (!newTechItems.isEmpty()) {
            List<TechItem> savedNewTechItems = techItemRepository.saveAll(newTechItems);
            existingTechItems.addAll(savedNewTechItems);
        }

        return existingTechItems;
    }

    /**
     * 기존 태그와 새로운 태그를 비교하여 효율적으로 업데이트
     */
    private void updateTagsEfficiently(Recruitment recruitment, List<TagRequestDTO> newTags) {
        if (newTags == null) {
            newTags = List.of();
        }

        // 새로운 태그명 집합
        Set<String> newTagNames = newTags.stream()
                .map(TagRequestDTO::getTagName)
                .collect(Collectors.toSet());

        // 기존 태그명 집합
        Set<String> existingTagNames = recruitment.getTags().stream()
                .map(tag -> tag.getTagItem().getTagName())
                .collect(Collectors.toSet());

        // 삭제할 태그 (기존에는 있지만 새로운 요청에는 없는 것들)
        Set<String> tagNamesToRemove = existingTagNames.stream()
                .filter(tagName -> !newTagNames.contains(tagName))
                .collect(Collectors.toSet());

        // 추가할 태그명 (새로운 요청에는 있지만 기존에는 없는 것들)
        Set<String> tagNamesToAdd = newTagNames.stream()
                .filter(tagName -> !existingTagNames.contains(tagName))
                .collect(Collectors.toSet());

        // 삭제할 태그 제거
        if (!tagNamesToRemove.isEmpty()) {
            List<Tag> tagsToRemove = recruitment.getTags().stream()
                    .filter(tag -> tagNamesToRemove.contains(tag.getTagItem().getTagName()))
                    .toList();

            // 벌크 삭제 쿼리 사용 (더 효율적)
            List<Long> tagIdsToRemove = tagsToRemove.stream()
                    .map(Tag::getId)
                    .toList();

            tagRepository.deleteAllByIdInBatch(tagIdsToRemove);
            tagsToRemove.forEach(recruitment.getTags()::remove);
            recruitment.getTags().removeAll(tagsToRemove);
        }

        // 추가할 태그 생성
        if (!tagNamesToAdd.isEmpty()) {
            Set<TagRequestDTO> tagsToAdd = newTags.stream()
                    .filter(tag -> tagNamesToAdd.contains(tag.getTagName()))
                    .collect(Collectors.toSet());

            processTags(recruitment, tagsToAdd);
        }
    }

    /**
     * 태그를 처리하는 메서드
     * 1. 기존 TagItem 조회
     * 2. 없는 TagItem 생성
     * 3. Tag 생성 및 저장
     */
    private void processTags(Recruitment recruitment, Set<TagRequestDTO> tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }

        // 필요한 태그명 추출
        Set<String> tagNames = tags.stream()
                .map(TagRequestDTO::getTagName)
                .collect(Collectors.toSet());

        // 기존 TagItem 조회 및 새로운 TagItem 생성
        List<TagItem> allTagItems = getOrCreateTagItems(tagNames, tags);

        // Tag 생성 및 저장
        Set<Tag> tagEntities = allTagItems.stream()
                .map(tagItem -> Tag.builder()
                        .tagItem(tagItem)
                        .recruitment(recruitment)
                        .build())
                .collect(Collectors.toSet());

        tagRepository.saveAll(tagEntities);
        recruitment.getTags().addAll(tagEntities);
    }

    /**
     * TagItem을 조회하거나 생성하는 메서드
     */
    private List<TagItem> getOrCreateTagItems(Set<String> tagNames, Set<TagRequestDTO> tags) {
        // 기존 TagItem 조회
        List<TagItem> existingTagItems = tagItemRepository.findAllByTagNameIn(tagNames);

        Set<String> existingTagNames = existingTagItems.stream()
                .map(TagItem::getTagName)
                .collect(Collectors.toSet());

        // 새로운 TagItem 생성
        List<TagItem> newTagItems = tags.stream()
                .filter(tag -> !existingTagNames.contains(tag.getTagName()))
                .map(TagRequestDTO::toEntity)
                .toList();

        // 새로운 TagItem 저장
        if (!newTagItems.isEmpty()) {
            List<TagItem> savedNewTagItems = tagItemRepository.saveAll(newTagItems);
            existingTagItems.addAll(savedNewTagItems);
        }

        return existingTagItems;
    }
}

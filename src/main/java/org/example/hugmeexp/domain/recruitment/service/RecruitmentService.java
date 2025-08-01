package org.example.hugmeexp.domain.recruitment.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentRequestDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentSearchConditionDTO;
import org.example.hugmeexp.domain.recruitment.dto.TechItemRequestDTO;
import org.example.hugmeexp.domain.recruitment.dto.TagRequestDTO;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.example.hugmeexp.domain.recruitment.entity.TechItem;
import org.example.hugmeexp.domain.recruitment.entity.TechStack;
import org.example.hugmeexp.domain.recruitment.entity.Tag;
import org.example.hugmeexp.domain.recruitment.entity.TagItem;
import org.example.hugmeexp.domain.recruitment.entity.Company;
import org.example.hugmeexp.domain.recruitment.repository.RecruitmentRepository;
import org.example.hugmeexp.domain.recruitment.repository.TechItemRepository;
import org.example.hugmeexp.domain.recruitment.repository.TechStackRepository;
import org.example.hugmeexp.domain.recruitment.repository.TagRepository;
import org.example.hugmeexp.domain.recruitment.repository.TagItemRepository;
import org.example.hugmeexp.domain.recruitment.repository.CompanyRepository;
import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
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

    @Transactional
    public RecruitmentResponseDTO createOrUpdateRecruitment(RecruitmentRequestDTO requestDTO) {
        Recruitment recruitment;
        // 이미 존재한다면 업데이트
        if (recruitmentRepository.existsRecruitmentBySourceId(requestDTO.getSourceId())) {
            recruitment = updateRecruitment(requestDTO);
        } else {
            // 새로 생성
            recruitment = createRecruitment(requestDTO);
        }


        return RecruitmentResponseDTO.from(recruitment);
    }

    private Recruitment createRecruitment(RecruitmentRequestDTO requestDTO) {
        // Company를 먼저 저장 (transient 문제 해결)
        Company savedCompany = companyRepository.save(requestDTO.getCompany().toEntity());

        // Recruitment 엔티티 생성 시 저장된 Company 사용
        Recruitment recruitment = requestDTO.toEntity();
        recruitment.setCompany(savedCompany);

        recruitment = recruitmentRepository.save(recruitment);

        // 기술 스택 처리를 별도 메서드로 분리
        processTechStacks(recruitment, requestDTO.getRequiredSkills());

        // 태그 처리 추가
        processTags(recruitment, requestDTO.getTags());

        return recruitment;
    }

    private Recruitment updateRecruitment(RecruitmentRequestDTO requestDTO) {
        Recruitment recruitment = recruitmentRepository.findBySourceId(requestDTO.getSourceId())
                .orElseThrow(() -> new BaseCustomException(HttpStatus.NOT_FOUND, "채용 공고를 찾을 수 없습니다.", 404));

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
            List<TechItemRequestDTO> skillsToAdd = newRequiredSkills.stream()
                    .filter(skill -> techNamesToAdd.contains(skill.getEnglishName()))
                    .toList();

            processTechStacks(recruitment, skillsToAdd);
        }
    }

    /**
     * 기술 스택을 처리하는 메서드
     * 1. 기존 TechItem 조회
     * 2. 없는 TechItem 생성
     * 3. TechStack 생성 및 저장
     */
    private void processTechStacks(Recruitment recruitment, List<TechItemRequestDTO> requiredSkills) {
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
    private List<TechItem> getOrCreateTechItems(Set<String> techNames, List<TechItemRequestDTO> requiredSkills) {
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
            recruitment.getTags().removeAll(tagsToRemove);
        }

        // 추가할 태그 생성
        if (!tagNamesToAdd.isEmpty()) {
            List<TagRequestDTO> tagsToAdd = newTags.stream()
                    .filter(tag -> tagNamesToAdd.contains(tag.getTagName()))
                    .toList();

            processTags(recruitment, tagsToAdd);
        }
    }

    /**
     * 태그를 처리하는 메서드
     * 1. 기존 TagItem 조회
     * 2. 없는 TagItem 생성
     * 3. Tag 생성 및 저장
     */
    private void processTags(Recruitment recruitment, List<TagRequestDTO> tags) {
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
    private List<TagItem> getOrCreateTagItems(Set<String> tagNames, List<TagRequestDTO> tags) {
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

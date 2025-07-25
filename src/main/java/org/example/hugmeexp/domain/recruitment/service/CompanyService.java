package org.example.hugmeexp.domain.recruitment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentCompanySearchResponseDTO;
import org.example.hugmeexp.domain.recruitment.entity.Company;
import org.example.hugmeexp.domain.recruitment.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;

    /**
     * 기업 이름을 키워드로 검색하여 기업 목록을 조회합니다.
     * 키워드가 null 또는 비어있으면 모든 기업을 조회합니다.
     *
     * @param keyword 검색 키워드
     * @return 기업 목록 DTO
     */
    public List<RecruitmentCompanySearchResponseDTO> searchCompaniesByKeyword(String keyword) {
        List<Company> companies = (keyword == null || keyword.isBlank())
                ? companyRepository.findAll()
                : companyRepository.findByCompanyNameContainingIgnoreCase(keyword);

        return companies.stream()
                .map(RecruitmentCompanySearchResponseDTO::from)
                .toList();
    }
}

package org.example.hugmeexp.domain.recruitment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentCompanySearchResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
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
     * 주어진 키워드로 기업 목록을 검색합니다.
     *
     * @param keyword 검색할 키워드
     * @return 검색된 기업 목록
     */
    public List<RecruitmentCompanySearchResponseDTO> searchCompaniesByKeyword(String keyword) {
        List<Company> companies = companyRepository.findByCompanyNameContainingIgnoreCase(keyword);

        return companies.stream()
                .map(RecruitmentCompanySearchResponseDTO::from)
                .toList();
    }
}

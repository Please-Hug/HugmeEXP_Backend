package org.example.hugmeexp.domain.recruitment.service;

import org.example.hugmeexp.domain.recruitment.dto.RecruitmentCompanySearchResponseDTO;
import org.example.hugmeexp.domain.recruitment.entity.Company;
import org.example.hugmeexp.domain.recruitment.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @Test
    @DisplayName("키워드로 회사 검색 - 결과가 있는 경우")
    void searchCompaniesByKeyword_WithMatchingResults_ShouldReturnCompanies() {
        // Given
        String keyword = "테크";
        List<Company> mockCompanies = createMockCompanies();
        when(companyRepository.findByCompanyNameContainingIgnoreCase(keyword)).thenReturn(mockCompanies);

        // When
        List<RecruitmentCompanySearchResponseDTO> result = companyService.searchCompaniesByKeyword(keyword);

        // Then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getCompanyId());
        assertEquals("ABC 테크", result.get(0).getCompanyName());
        assertEquals(2L, result.get(1).getCompanyId());
        assertEquals("XYZ 테크놀로지", result.get(1).getCompanyName());
        verify(companyRepository).findByCompanyNameContainingIgnoreCase(keyword);
    }

    @Test
    @DisplayName("키워드로 회사 검색 - 결과가 없는 경우")
    void searchCompaniesByKeyword_WithNoResults_ShouldReturnEmptyList() {
        // Given
        String keyword = "존재하지않는회사";
        when(companyRepository.findByCompanyNameContainingIgnoreCase(keyword)).thenReturn(Collections.emptyList());

        // When
        List<RecruitmentCompanySearchResponseDTO> result = companyService.searchCompaniesByKeyword(keyword);

        // Then
        assertEquals(0, result.size());
        verify(companyRepository).findByCompanyNameContainingIgnoreCase(keyword);
    }

    @Test
    @DisplayName("빈 키워드로 회사 검색 - 페이징 처리")
    void searchCompaniesByKeyword_WithEmptyKeyword_ShouldReturnAllCompanies() {
        // Given
        String keyword = "";
        List<Company> mockCompanies = createMockCompanies();
        when(companyRepository.findAll(PageRequest.of(0, 30))).thenReturn(new PageImpl<>(mockCompanies));

        // When
        List<RecruitmentCompanySearchResponseDTO> result = companyService.searchCompaniesByKeyword(keyword);

        // Then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getCompanyId());
        assertEquals("ABC 테크", result.get(0).getCompanyName());
        assertEquals(2L, result.get(1).getCompanyId());
        assertEquals("XYZ 테크놀로지", result.get(1).getCompanyName());
        verify(companyRepository).findAll(PageRequest.of(0, 30));
    }

    @Test
    @DisplayName("null 키워드로 회사 검색 - 페이징 처리")
    void searchCompaniesByKeyword_WithNullKeyword_ShouldReturnAllCompanies() {
        // Given
        String keyword = null;
        List<Company> mockCompanies = createMockCompanies();
        when(companyRepository.findAll(PageRequest.of(0, 30))).thenReturn(new PageImpl<>(mockCompanies));

        // When
        List<RecruitmentCompanySearchResponseDTO> result = companyService.searchCompaniesByKeyword(keyword);

        // Then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getCompanyId());
        assertEquals("ABC 테크", result.get(0).getCompanyName());
        assertEquals(2L, result.get(1).getCompanyId());
        assertEquals("XYZ 테크놀로지", result.get(1).getCompanyName());
        verify(companyRepository).findAll(PageRequest.of(0, 30));
    }

    @Test
    @DisplayName("대소문자 구분 없이 회사 검색")
    void searchCompaniesByKeyword_CaseInsensitive_ShouldReturnMatchingCompanies() {
        // Given
        String keyword = "tEcH"; // 대소문자 혼합
        List<Company> mockCompanies = createMockCompanies();
        when(companyRepository.findByCompanyNameContainingIgnoreCase(keyword)).thenReturn(mockCompanies);

        // When
        List<RecruitmentCompanySearchResponseDTO> result = companyService.searchCompaniesByKeyword(keyword);

        // Then
        assertEquals(2, result.size());
        verify(companyRepository).findByCompanyNameContainingIgnoreCase(keyword);
    }

    // 테스트용 회사 데이터 생성 헬퍼 메소드
    private List<Company> createMockCompanies() {
        List<Company> companies = new ArrayList<>();

        // 첫 번째 회사
        companies.add(Company.builder()
                .id(1L)
                .companyName("ABC 테크")
                .companyAddress("서울시 강남구")
                .latitude(new BigDecimal("37.5"))
                .longitude(new BigDecimal("127.0"))
                .establishmentDate(LocalDate.of(2010, 1, 1))
                .companyImageUrl("company_image_1.jpg")
                .companyDescription("ABC 테크는 혁신적인 기술 회사입니다.")
                .build());

        // 두 번째 회사
        companies.add(Company.builder()
                .id(2L)
                .companyName("XYZ 테크놀로지")
                .companyAddress("서울시 서초구")
                .latitude(new BigDecimal("37.4"))
                .longitude(new BigDecimal("127.1"))
                .establishmentDate(LocalDate.of(2015, 5, 10))
                .companyImageUrl("company_image_2.jpg")
                .companyDescription("XYZ 테크놀로지는 최첨단 기술을 개발하는 회사입니다.")
                .build());

        return companies;
    }
}

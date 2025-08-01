package org.example.hugmeexp.domain.recruitment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.hugmeexp.domain.recruitment.entity.Company;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequestDTO {
    private String companyName;
    private String companyAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDate establishmentDate;
    private String companyImageUrl;
    private String companyDescription;

    public Company toEntity() {
        return Company.builder()
                .companyName(companyName)
                .companyAddress(companyAddress)
                .latitude(latitude)
                .longitude(longitude)
                .establishmentDate(establishmentDate)
                .companyImageUrl(companyImageUrl)
                .companyDescription(companyDescription)
                .build();
    }
}

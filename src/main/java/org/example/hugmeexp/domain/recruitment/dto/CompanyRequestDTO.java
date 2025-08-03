package org.example.hugmeexp.domain.recruitment.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.hugmeexp.domain.recruitment.entity.Company;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRequestDTO {
    @NotBlank(message = "회사명은 필수입니다")
    @Size(max = 200, message = "회사명은 200자를 초과할 수 없습니다")
    private String companyName;

    @Size(max = 500, message = "회사 주소는 500자를 초과할 수 없습니다")
    private String companyAddress;

    @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다")
    @DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다")
    @Digits(integer = 2, fraction = 8, message = "위도는 소수점 8자리까지 가능합니다")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다")
    @DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다")
    @Digits(integer = 3, fraction = 8, message = "경도는 소수점 8자리까지 가능합니다")
    private BigDecimal longitude;

    @PastOrPresent(message = "설립일은 현재 날짜 이전이어야 합니다")
    private LocalDate establishmentDate;

    @Size(max = 1000, message = "회사 이미지 URL은 1000자를 초과할 수 없습니다")
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

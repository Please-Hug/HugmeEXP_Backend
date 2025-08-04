package org.example.hugmeexp.domain.recruitment.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.hugmeexp.domain.recruitment.entity.TechItem;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechItemRequestDTO {
    @NotBlank(message = "한글명은 필수입니다")
    @Size(max = 100, message = "영문명은 100자를 초과할 수 없습니다")
    private String englishName;

    @Size(max = 100, message = "한글명은 100자를 초과할 수 없습니다")
    private String koreanName;

    @Size(max = 1000, message = "아이콘 URL은 1000자를 초과할 수 없습니다")
    private String iconUrl;

    public TechItem toEntity() {
        return TechItem.builder()
                .englishName(englishName)
                .koreanName(koreanName)
                .iconUrl(iconUrl)
                .build();
    }
}

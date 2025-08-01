package org.example.hugmeexp.domain.recruitment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.hugmeexp.domain.recruitment.entity.TechItem;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechItemRequestDTO {
    private String englishName;
    private String koreanName;
    private String iconUrl;

    public TechItem toEntity() {
        return TechItem.builder()
                .englishName(englishName)
                .koreanName(koreanName)
                .iconUrl(iconUrl)
                .build();
    }
}

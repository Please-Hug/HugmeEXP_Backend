package org.example.hugmeexp.domain.recruitment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.recruitment.entity.TechItem;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TechStackDTO {

    private Long id;
    private String labelKo;
    private String labelEn;
    private String iconUrl;

    public static TechStackDTO from(TechItem techItem) {
        return TechStackDTO.builder()
                .id(techItem.getId())
                .labelKo(techItem.getKoreanName())
                .labelEn(techItem.getEnglishName())
                .iconUrl(techItem.getIconUrl())
                .build();
    }
}

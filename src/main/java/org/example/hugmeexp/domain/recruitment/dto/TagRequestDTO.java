package org.example.hugmeexp.domain.recruitment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.hugmeexp.domain.recruitment.entity.TagItem;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRequestDTO {
    private String tagName;

    public TagItem toEntity() {
        return TagItem.builder()
                .tagName(tagName)
                .build();
    }
}

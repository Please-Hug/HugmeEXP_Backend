package org.example.hugmeexp.domain.recruitment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.recruitment.entity.TagItem;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TagDTO {

    private Long id;
    private String tagName;

    public static TagDTO from(TagItem tagItem) {
        return TagDTO.builder()
                .id(tagItem.getId())
                .tagName(tagItem.getTagName())
                .build();
    }
}

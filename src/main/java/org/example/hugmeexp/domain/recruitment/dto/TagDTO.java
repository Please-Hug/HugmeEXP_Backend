package org.example.hugmeexp.domain.recruitment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.recruitment.entity.TagItem;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TagDTO {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TagDTO tagDTO = (TagDTO) o;
        return Objects.equals(tagName, tagDTO.tagName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tagName);
    }

    private Long id;
    private String tagName;

    public static TagDTO from(TagItem tagItem) {
        return TagDTO.builder()
                .id(tagItem.getId())
                .tagName(tagItem.getTagName())
                .build();
    }
}

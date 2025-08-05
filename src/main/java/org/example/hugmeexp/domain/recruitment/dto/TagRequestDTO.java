package org.example.hugmeexp.domain.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.hugmeexp.domain.recruitment.entity.TagItem;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRequestDTO implements Comparable<TagRequestDTO> {
    @NotBlank
    @Length(max = 100, message = "태그 이름은 100자를 초과할 수 없습니다")
    private String tagName;

    public TagItem toEntity() {
        return TagItem.builder()
                .tagName(tagName)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TagRequestDTO that = (TagRequestDTO) o;
        return Objects.equals(tagName, that.tagName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tagName);
    }

    @Override
    public int compareTo(TagRequestDTO o) {
        if (o == null) return 1; // null is considered greater
        if (this.tagName == null && o.tagName == null) return 0; // both are null
        if (this.tagName == null) return -1; // this is less than o
        if (o.tagName == null) return 1; // this is greater than o
        return this.tagName.compareTo(o.tagName); // compare tag names
    }
}

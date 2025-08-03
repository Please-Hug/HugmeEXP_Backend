package org.example.hugmeexp.domain.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.hugmeexp.domain.recruitment.entity.TagItem;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRequestDTO {
    @NotBlank
    @Length(max = 100, message = "태그 이름은 100자를 초과할 수 없습니다")
    private String tagName;

    public TagItem toEntity() {
        return TagItem.builder()
                .tagName(tagName)
                .build();
    }
}

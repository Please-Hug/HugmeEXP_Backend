package org.example.hugmeexp.domain.recruitment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TechStackDTO {

    private String labelKo;
    private String labelEn;
    private String iconUrl;
}

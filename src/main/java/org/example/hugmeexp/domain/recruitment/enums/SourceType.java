package org.example.hugmeexp.domain.recruitment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum SourceType {

    WANTED("원티드"),
    JUMPIT("점핏");

    private final String label;

}

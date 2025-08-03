package org.example.hugmeexp.domain.recruitment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RecruitmentFilterResponseDTO {

    private List<EducationOptionDTO> educationOptions;
    private List<Integer> experienceOptions;
    private List<TechStackDTO> techStacks;
    private List<String> workLocations;
    private List<TagDTO> tags;
    private SalaryRangeDTO salaryRange;
}

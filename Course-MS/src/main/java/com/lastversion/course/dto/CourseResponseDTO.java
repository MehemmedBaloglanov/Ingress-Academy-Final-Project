package com.lastversion.course.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String instructorName;
    private Integer duration;
}


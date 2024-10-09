package com.lastversion.course.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String instructorName;
    private int duration;
    private LocalDateTime startDate;
    private LocalDateTime endDate;


}


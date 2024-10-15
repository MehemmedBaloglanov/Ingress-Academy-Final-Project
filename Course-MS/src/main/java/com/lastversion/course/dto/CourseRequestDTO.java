package com.lastversion.course.dto;

import lombok.Data;

@Data
public class CourseRequestDTO {

    private String title;

    private String description;

    private String instructorName;

    private Integer duration;

}


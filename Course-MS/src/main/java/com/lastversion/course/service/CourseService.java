package com.lastversion.course.service;

import com.lastversion.course.dto.CourseCreateRequestDTO;
import com.lastversion.course.dto.CourseResponseDTO;
import com.lastversion.course.dto.CourseUpdateRequestDTO;

public interface CourseService {
    CourseResponseDTO createCourse(CourseCreateRequestDTO courseCreateRequestDTO);

    CourseResponseDTO getCourseById(Long id);

    CourseResponseDTO updateCourse(Long id, CourseUpdateRequestDTO courseUpdateRequestDTO);

    void deleteCourse(Long id);
}

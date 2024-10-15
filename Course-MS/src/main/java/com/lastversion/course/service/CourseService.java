package com.lastversion.course.service;

import com.lastversion.course.dto.CourseRequestDTO;
import com.lastversion.course.dto.CourseResponseDTO;

public interface CourseService {
    CourseResponseDTO createCourse(CourseRequestDTO courseCreateRequestDTO);

    CourseResponseDTO getCourseById(Long id);

    CourseResponseDTO updateCourse(Long id, CourseRequestDTO courseRequestDTO);

    void deleteCourse(Long id);
}

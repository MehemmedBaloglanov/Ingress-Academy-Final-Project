package com.lastversion.course.service;

import com.lastversion.course.dto.CourseCreateRequestDTO;
import com.lastversion.course.dto.CourseResponseDTO;
import com.lastversion.course.dto.CourseUpdateRequestDTO;
import com.lastversion.course.entity.CourseEntity;
import com.lastversion.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;


    public CourseResponseDTO createCourse(CourseCreateRequestDTO courseCreateRequestDTO) {
        CourseEntity courseEntity = mapToEntity(courseCreateRequestDTO);
        CourseEntity savedCourse = courseRepository.save(courseEntity);
        return mapToResponseDTO(savedCourse);
    }

    public CourseResponseDTO getCourseById(Long id) {
        CourseEntity courseEntity = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return mapToResponseDTO(courseEntity);
    }

    public CourseResponseDTO updateCourse(Long id, CourseUpdateRequestDTO courseUpdateRequestDTO) {
        CourseEntity existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        existingCourse.setTitle(courseUpdateRequestDTO.getTitle());
        existingCourse.setDescription(courseUpdateRequestDTO.getDescription());
        existingCourse.setInstructorName(courseUpdateRequestDTO.getInstructorName());
        existingCourse.setDuration(courseUpdateRequestDTO.getDuration());
        existingCourse.setStartDate(courseUpdateRequestDTO.getStartDate());
        existingCourse.setEndDate(courseUpdateRequestDTO.getEndDate());
        CourseEntity updatedCourse = courseRepository.save(existingCourse);
        return mapToResponseDTO(updatedCourse);
    }

    public void deleteCourse(Long id) {
        CourseEntity courseEntity = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        courseRepository.delete(courseEntity);
    }

    private CourseResponseDTO mapToResponseDTO(CourseEntity courseEntity) {
        CourseResponseDTO courseDTO = new CourseResponseDTO();
        courseDTO.setId(courseEntity.getId());
        courseDTO.setTitle(courseEntity.getTitle());
        courseDTO.setDescription(courseEntity.getDescription());
        courseDTO.setInstructorName(courseEntity.getInstructorName());
        courseDTO.setDuration(courseEntity.getDuration());
        courseDTO.setStartDate(courseEntity.getStartDate());
        courseDTO.setEndDate(courseEntity.getEndDate());
        return courseDTO;
    }

    private CourseEntity mapToEntity(CourseCreateRequestDTO courseCreateRequestDTO) {
        CourseEntity courseEntity = new CourseEntity();
        courseEntity.setTitle(courseCreateRequestDTO.getTitle());
        courseEntity.setDescription(courseCreateRequestDTO.getDescription());
        courseEntity.setInstructorName(courseCreateRequestDTO.getInstructorName());
        courseEntity.setDuration(courseCreateRequestDTO.getDuration());
        courseEntity.setStartDate(courseCreateRequestDTO.getStartDate());
        courseEntity.setEndDate(courseCreateRequestDTO.getEndDate());
        return courseEntity;
    }

    private CourseEntity mapToEntity(CourseUpdateRequestDTO courseUpdateRequestDTO) {
        CourseEntity courseEntity = new CourseEntity();
        courseEntity.setTitle(courseUpdateRequestDTO.getTitle());
        courseEntity.setDescription(courseUpdateRequestDTO.getDescription());
        courseEntity.setInstructorName(courseUpdateRequestDTO.getInstructorName());
        courseEntity.setDuration(courseUpdateRequestDTO.getDuration());
        courseEntity.setStartDate(courseUpdateRequestDTO.getStartDate());
        courseEntity.setEndDate(courseUpdateRequestDTO.getEndDate());
        return courseEntity;
    }
}

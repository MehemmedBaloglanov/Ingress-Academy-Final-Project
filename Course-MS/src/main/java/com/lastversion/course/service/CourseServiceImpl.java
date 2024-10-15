package com.lastversion.course.service;

import com.lastversion.course.dto.CourseRequestDTO;
import com.lastversion.course.dto.CourseResponseDTO;
import com.lastversion.course.entity.CourseEntity;
import com.lastversion.course.exception.CourseNotFoundException;
import com.lastversion.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;


    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO courseCreateRequestDTO) {
        CourseEntity courseEntity = CourseEntity.builder()
                .title(courseCreateRequestDTO.getTitle())
                .description(courseCreateRequestDTO.getDescription())
                .instructorName(courseCreateRequestDTO.getInstructorName())
                .duration(courseCreateRequestDTO.getDuration())
                .build();

        CourseEntity save = courseRepository.save(courseEntity);
        return CourseResponseDTO.builder()
                .id(save.getId())
                .title(save.getTitle())
                .description(save.getDescription())
                .instructorName(save.getInstructorName())
                .duration(save.getDuration())
                .build();
    }

    @Override
    public CourseResponseDTO getCourseById(Long id) {
        Optional<CourseEntity> courseEntity = courseRepository.findById(id);

        if (courseEntity.isEmpty()) {
            throw new CourseNotFoundException("Course not found with id: " + id);
        }

        return CourseResponseDTO.builder()
                .id(courseEntity.get().getId())
                .title(courseEntity.get().getTitle())
                .description(courseEntity.get().getDescription())
                .instructorName(courseEntity.get().getInstructorName())
                .duration(courseEntity.get().getDuration())
                .build();
    }

    @Override
    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO courseRequestDTO) {
        Optional<CourseEntity> courseEntity = courseRepository.findById(id);
        if (courseEntity.isEmpty()) {
            throw new CourseNotFoundException("Course not found with id: " + id);
        }
        courseEntity.get().setTitle(courseRequestDTO.getTitle());
        courseEntity.get().setDescription(courseRequestDTO.getDescription());
        courseEntity.get().setInstructorName(courseRequestDTO.getInstructorName());
        courseEntity.get().setDuration(courseRequestDTO.getDuration());

        CourseEntity save = courseRepository.save(courseEntity.get());

        return CourseResponseDTO.builder()
                .id(save.getId())
                .title(save.getTitle())
                .description(save.getDescription())
                .instructorName(save.getInstructorName())
                .duration(save.getDuration())
                .build();
    }

    @Override
    public void deleteCourse(Long id) {
        Optional<CourseEntity> courseEntity = courseRepository.findById(id);

        if (courseEntity.isEmpty()) {
            throw new CourseNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }
}

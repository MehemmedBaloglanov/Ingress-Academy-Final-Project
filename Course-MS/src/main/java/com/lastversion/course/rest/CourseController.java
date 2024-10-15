package com.lastversion.course.rest;

import com.lastversion.common.entity.UserEntity;
import com.lastversion.course.dto.CourseRequestDTO;
import com.lastversion.course.dto.CourseResponseDTO;
import com.lastversion.course.entity.StudentEntity;
import com.lastversion.course.repository.StudentRepository;
import com.lastversion.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final StudentRepository studentRepository;

    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@RequestBody CourseRequestDTO courseCreateRequestDTO) {
        CourseResponseDTO createdCourse = courseService.createCourse(courseCreateRequestDTO);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        CourseResponseDTO courseDTO = courseService.getCourseById(id);
        return new ResponseEntity<>(courseDTO, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseRequestDTO courseRequestDTO) {
        CourseResponseDTO updatedCourse = courseService.updateCourse(id, courseRequestDTO);
        return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/createstudent")
    public void createStudent(@RequestBody UserEntity userEntity) {
        StudentEntity studentEntity = StudentEntity.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getFirstName())
                .build();
        studentRepository.save(studentEntity);
    }
}


package com.lastversion.course.repository;

import com.lastversion.course.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  StudentRepository extends JpaRepository<StudentEntity,Long> {
}

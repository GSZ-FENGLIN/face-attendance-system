package com.attendance.mapper;

import com.attendance.entity.CourseStudent;
import java.util.List;

public interface CourseStudentMapper {
    List<CourseStudent> findByCourseId(Long courseId);
    List<CourseStudent> findByStudentId(Long studentId);
    int insert(CourseStudent cs);
    int deleteByCourseId(Long courseId);
    int deleteById(Long id);
    int countByCourseId(Long courseId);
}

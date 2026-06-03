package com.attendance.service;

import com.attendance.dto.PageResult;
import com.attendance.entity.Course;

public interface CourseService {
    PageResult<Course> listCourses(String keyword, Long teacherId, String className, int page, int pageSize);
    Course getCourse(Long id);
    int addCourse(Course course);
    int updateCourse(Course course);
    int deleteCourse(Long id);
}

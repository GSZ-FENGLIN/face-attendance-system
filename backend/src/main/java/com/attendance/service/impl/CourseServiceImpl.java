package com.attendance.service.impl;

import com.attendance.dto.PageResult;
import com.attendance.entity.Course;
import com.attendance.mapper.CourseMapper;
import com.attendance.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public PageResult<Course> listCourses(String keyword, Long teacherId, String className, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Course> list = courseMapper.findList(keyword, teacherId, className, offset, pageSize);
        long total = courseMapper.countList(keyword, teacherId, className);
        return new PageResult<>(total, list, page, pageSize);
    }

    @Override
    public Course getCourse(Long id) {
        return courseMapper.findById(id);
    }

    @Override
    public int addCourse(Course course) {
        return courseMapper.insert(course);
    }

    @Override
    public int updateCourse(Course course) {
        return courseMapper.update(course);
    }

    @Override
    public int deleteCourse(Long id) {
        return courseMapper.deleteById(id);
    }
}

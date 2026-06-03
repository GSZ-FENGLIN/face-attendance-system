package com.attendance.mapper;

import com.attendance.entity.Course;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CourseMapper {
    Course findById(Long id);
    List<Course> findByTeacherId(Long teacherId);
    List<Course> findList(@Param("keyword") String keyword, @Param("teacherId") Long teacherId,
                          @Param("className") String className,
                          @Param("offset") int offset, @Param("limit") int limit);
    long countList(@Param("keyword") String keyword, @Param("teacherId") Long teacherId,
                   @Param("className") String className);
    int insert(Course course);
    int update(Course course);
    int deleteById(Long id);
}

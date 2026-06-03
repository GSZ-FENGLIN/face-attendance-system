package com.attendance.mapper;

import com.attendance.entity.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserMapper {
    User findById(Long id);
    User findByUsername(String username);
    User findByStudentNo(String studentNo);
    List<User> findList(@Param("role") String role, @Param("keyword") String keyword,
                        @Param("className") String className, @Param("status") Integer status,
                        @Param("offset") int offset, @Param("limit") int limit);
    long countList(@Param("role") String role, @Param("keyword") String keyword,
                   @Param("className") String className, @Param("status") Integer status);
    List<User> findByCourseId(@Param("courseId") Long courseId);
    int insert(User user);
    int update(User user);
    int updateFaceStatus(@Param("id") Long id, @Param("status") Integer status);
    int deleteById(Long id);
}

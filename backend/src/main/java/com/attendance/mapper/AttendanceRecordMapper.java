package com.attendance.mapper;

import com.attendance.entity.AttendanceRecord;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface AttendanceRecordMapper {
    AttendanceRecord findById(Long id);
    List<AttendanceRecord> findList(@Param("courseId") Long courseId,
                                     @Param("studentId") Long studentId,
                                     @Param("studentName") String studentName,
                                     @Param("studentNo") String studentNo,
                                     @Param("className") String className,
                                     @Param("status") Integer status,
                                     @Param("weekNum") Integer weekNum,
                                     @Param("startDate") String startDate,
                                     @Param("endDate") String endDate,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);
    long countList(@Param("courseId") Long courseId,
                   @Param("studentId") Long studentId,
                   @Param("studentName") String studentName,
                   @Param("studentNo") String studentNo,
                   @Param("className") String className,
                   @Param("status") Integer status,
                   @Param("weekNum") Integer weekNum,
                   @Param("startDate") String startDate,
                   @Param("endDate") String endDate);

    // 查询某次考勤详情
    List<AttendanceRecord> findByCourseAndWeek(@Param("courseId") Long courseId, @Param("weekNum") Integer weekNum);

    // 学生个人考勤记录
    List<AttendanceRecord> findByStudentId(@Param("studentId") Long studentId,
                                            @Param("offset") int offset,
                                            @Param("limit") int limit);
    long countByStudentId(Long studentId);

    int insert(AttendanceRecord record);
    int insertBatch(List<AttendanceRecord> records);
    int update(AttendanceRecord record);

    // 统计出勤率
    List<Map<String, Object>> statisticsByCourse(@Param("courseId") Long courseId);
    Map<String, Object> statisticsByStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
}

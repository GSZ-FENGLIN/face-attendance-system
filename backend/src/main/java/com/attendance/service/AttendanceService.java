package com.attendance.service;

import com.attendance.dto.AttendanceQueryDTO;
import com.attendance.dto.PageResult;
import com.attendance.entity.AttendanceRecord;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    PageResult<AttendanceRecord> listRecords(AttendanceQueryDTO query);
    List<AttendanceRecord> getCourseAttendance(Long courseId, Integer weekNum);
    List<Map<String, Object>> getStatistics(Long courseId);
    Map<String, Object> getStudentStatistics(Long courseId, Long studentId);
    void exportExcel(Long courseId, Integer weekNum, HttpServletResponse response);
}

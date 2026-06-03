package com.attendance.controller;

import com.attendance.dto.AttendanceQueryDTO;
import com.attendance.dto.PageResult;
import com.attendance.dto.Result;
import com.attendance.entity.AttendanceRecord;
import com.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/list")
    public Result list(AttendanceQueryDTO query) {
        PageResult<AttendanceRecord> result = attendanceService.listRecords(query);
        return Result.success(result);
    }

    @GetMapping("/course/{courseId}")
    public Result getCourseAttendance(@PathVariable Long courseId,
                                       @RequestParam(required = false) Integer weekNum) {
        List<AttendanceRecord> records = attendanceService.getCourseAttendance(courseId, weekNum);
        return Result.success(records);
    }

    @GetMapping("/statistics/{courseId}")
    public Result statistics(@PathVariable Long courseId) {
        List<Map<String, Object>> stats = attendanceService.getStatistics(courseId);
        return Result.success(stats);
    }

    @GetMapping("/statistics/student")
    public Result studentStatistics(@RequestParam Long courseId, @RequestParam Long studentId) {
        Map<String, Object> stats = attendanceService.getStudentStatistics(courseId, studentId);
        return Result.success(stats);
    }

    @GetMapping("/export")
    public void export(@RequestParam Long courseId,
                       @RequestParam(required = false) Integer weekNum,
                       HttpServletResponse response) {
        attendanceService.exportExcel(courseId, weekNum, response);
    }
}

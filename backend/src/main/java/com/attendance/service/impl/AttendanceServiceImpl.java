package com.attendance.service.impl;

import com.attendance.dto.AttendanceQueryDTO;
import com.attendance.dto.PageResult;
import com.attendance.entity.AttendanceRecord;
import com.attendance.mapper.AttendanceRecordMapper;
import com.attendance.service.AttendanceService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRecordMapper attendanceRecordMapper;

    @Override
    public PageResult<AttendanceRecord> listRecords(AttendanceQueryDTO query) {
        int offset = (query.getPage() - 1) * query.getPageSize();
        List<AttendanceRecord> list = attendanceRecordMapper.findList(
                query.getCourseId(), query.getStudentId(), query.getStudentName(), query.getStudentNo(),
                query.getClassName(), query.getStatus(), query.getWeekNum(),
                query.getStartDate(), query.getEndDate(), offset, query.getPageSize());
        long total = attendanceRecordMapper.countList(
                query.getCourseId(), query.getStudentId(), query.getStudentName(), query.getStudentNo(),
                query.getClassName(), query.getStatus(), query.getWeekNum(),
                query.getStartDate(), query.getEndDate());
        return new PageResult<>(total, list, query.getPage(), query.getPageSize());
    }

    @Override
    public List<AttendanceRecord> getCourseAttendance(Long courseId, Integer weekNum) {
        return attendanceRecordMapper.findByCourseAndWeek(courseId, weekNum);
    }

    @Override
    public List<Map<String, Object>> getStatistics(Long courseId) {
        return attendanceRecordMapper.statisticsByCourse(courseId);
    }

    @Override
    public Map<String, Object> getStudentStatistics(Long courseId, Long studentId) {
        return attendanceRecordMapper.statisticsByStudent(courseId, studentId);
    }

    @Override
    public void exportExcel(Long courseId, Integer weekNum, HttpServletResponse response) {
        List<AttendanceRecord> records = attendanceRecordMapper.findByCourseAndWeek(courseId, weekNum);
        if (records.isEmpty()) {
            throw new RuntimeException("暂无考勤数据");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("考勤记录");

            // 表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 表头
            String[] headers = {"学号", "姓名", "班级", "课程", "考勤状态", "签到时间", "第几周"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 数据行
            for (int i = 0; i < records.size(); i++) {
                AttendanceRecord r = records.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(r.getStudentNo() != null ? r.getStudentNo() : "");
                row.createCell(1).setCellValue(r.getStudentName() != null ? r.getStudentName() : "");
                row.createCell(2).setCellValue(r.getClassName() != null ? r.getClassName() : "");
                row.createCell(3).setCellValue(r.getCourseName() != null ? r.getCourseName() : "");
                String statusStr = r.getStatus() == 1 ? "已签到" : (r.getStatus() == 2 ? "迟到" : "缺勤");
                row.createCell(4).setCellValue(statusStr);
                row.createCell(5).setCellValue(r.getSignTime() != null ? r.getSignTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
                row.createCell(6).setCellValue(r.getWeekNum() != null ? r.getWeekNum().toString() : "");
            }

            // 自动列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 输出
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = URLEncoder.encode("考勤记录_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.flush();
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }
}

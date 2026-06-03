package com.attendance.entity;

import java.time.LocalDateTime;

public class AttendanceRecord {
    private Long id;
    private Long courseId;         // 课程ID
    private String courseName;     // 课程名称
    private Long studentId;        // 学生ID
    private String studentName;    // 学生姓名
    private String studentNo;      // 学号
    private String className;      // 班级
    private Integer status;        // 0-缺勤 1-已签到 2-迟到
    private LocalDateTime signTime; // 签到时间
    private Integer weekNum;       // 第几周
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getSignTime() { return signTime; }
    public void setSignTime(LocalDateTime signTime) { this.signTime = signTime; }
    public Integer getWeekNum() { return weekNum; }
    public void setWeekNum(Integer weekNum) { this.weekNum = weekNum; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

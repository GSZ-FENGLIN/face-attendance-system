package com.attendance.entity;

import java.time.LocalDateTime;

public class Course {
    private Long id;
    private String courseName;    // 课程名称
    private String courseCode;    // 课程编号
    private Long teacherId;       // 授课教师ID
    private String teacherName;   // 授课教师姓名
    private String className;     // 上课班级
    private String classroom;     // 上课教室
    private String weekDay;       // 星期几（1-7）
    private String startTime;     // 开始时间 HH:mm
    private String endTime;       // 结束时间 HH:mm
    private Integer weekStart;    // 起始周
    private Integer weekEnd;      // 结束周
    private Integer status;       // 0-未开课 1-已开课
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getClassroom() { return classroom; }
    public void setClassroom(String classroom) { this.classroom = classroom; }
    public String getWeekDay() { return weekDay; }
    public void setWeekDay(String weekDay) { this.weekDay = weekDay; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public Integer getWeekStart() { return weekStart; }
    public void setWeekStart(Integer weekStart) { this.weekStart = weekStart; }
    public Integer getWeekEnd() { return weekEnd; }
    public void setWeekEnd(Integer weekEnd) { this.weekEnd = weekEnd; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}

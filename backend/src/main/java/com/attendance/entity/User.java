package com.attendance.entity;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;       // 登录账号
    private String password;       // 登录密码
    private String realName;       // 真实姓名
    private String role;           // admin / teacher / student
    private String studentNo;      // 学号（学生）
    private String phone;
    private String email;
    private String className;      // 班级（学生）
    private Integer faceRegistered; // 0-未注册人脸 1-已注册
    private Integer status;        // 0-禁用 1-启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public Integer getFaceRegistered() { return faceRegistered; }
    public void setFaceRegistered(Integer faceRegistered) { this.faceRegistered = faceRegistered; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}

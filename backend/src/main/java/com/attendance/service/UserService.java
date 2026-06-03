package com.attendance.service;

import com.attendance.dto.LoginDTO;
import com.attendance.dto.PageResult;
import com.attendance.entity.User;

import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginDTO loginDTO);
    User getCurrentUser(Long userId);
    PageResult<User> listUsers(String role, String keyword, String className, Integer status, int page, int pageSize);
    int addUser(User user);
    int updateUser(User user);
    int deleteUser(Long id);
    void resetPassword(Long id);
}

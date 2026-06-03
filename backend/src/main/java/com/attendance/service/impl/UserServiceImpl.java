package com.attendance.service.impl;

import com.attendance.config.JwtUtil;
import com.attendance.dto.LoginDTO;
import com.attendance.dto.PageResult;
import com.attendance.entity.User;
import com.attendance.mapper.UserMapper;
import com.attendance.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        User user = userMapper.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        String encryptedPwd = DigestUtils.md5Hex(loginDTO.getPassword());
        if (!user.getPassword().equals(encryptedPwd)) {
            throw new RuntimeException("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Override
    public User getCurrentUser(Long userId) {
        return userMapper.findById(userId);
    }

    @Override
    public PageResult<User> listUsers(String role, String keyword, String className, Integer status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<User> list = userMapper.findList(role, keyword, className, status, offset, pageSize);
        long total = userMapper.countList(role, keyword, className, status);
        return new PageResult<>(total, list, page, pageSize);
    }

    @Override
    public int addUser(User user) {
        User existing = userMapper.findByUsername(user.getUsername());
        if (existing != null) {
            throw new RuntimeException("账号已存在");
        }
        if (user.getStudentNo() != null) {
            existing = userMapper.findByStudentNo(user.getStudentNo());
            if (existing != null) {
                throw new RuntimeException("学号已存在");
            }
        }
        user.setPassword(DigestUtils.md5Hex("123456")); // 默认密码
        return userMapper.insert(user);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.update(user);
    }

    @Override
    public int deleteUser(Long id) {
        return userMapper.deleteById(id);
    }

    @Override
    public void resetPassword(Long id) {
        User user = new User();
        user.setId(id);
        user.setPassword(DigestUtils.md5Hex("123456"));
        userMapper.update(user);
    }
}

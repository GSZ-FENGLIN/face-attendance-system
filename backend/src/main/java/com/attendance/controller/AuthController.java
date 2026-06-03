package com.attendance.controller;

import com.attendance.dto.LoginDTO;
import com.attendance.dto.Result;
import com.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        try {
            Map<String, Object> data = userService.login(loginDTO);
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result getInfo(@RequestAttribute Long userId) {
        return Result.success(userService.getCurrentUser(userId));
    }
}

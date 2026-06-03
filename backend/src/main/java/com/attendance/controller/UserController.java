package com.attendance.controller;

import com.attendance.dto.PageResult;
import com.attendance.dto.Result;
import com.attendance.entity.User;
import com.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String role,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String className,
                       @RequestParam(required = false) Integer status,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<User> result = userService.listUsers(role, keyword, className, status, page, pageSize);
        return Result.success(result);
    }

    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        try {
            userService.addUser(user);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result update(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PostMapping("/reset-password/{id}")
    public Result resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }
}

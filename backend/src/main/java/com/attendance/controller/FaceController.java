package com.attendance.controller;

import com.attendance.dto.Result;
import com.attendance.service.FaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/face")
public class FaceController {

    @Autowired
    private FaceService faceService;

    @PostMapping("/register")
    public Result register(@RequestParam Long userId, @RequestParam("image") MultipartFile image) {
        return faceService.registerFace(userId, image);
    }

    @PostMapping("/attendance/start")
    public Result startAttendance(@RequestParam Long courseId, @RequestAttribute Long userId) {
        return faceService.startAttendance(courseId, userId);
    }

    @PostMapping("/attendance/stop")
    public Result stopAttendance(@RequestParam Long courseId) {
        faceService.stopAttendance(courseId);
        return Result.success("考勤已结束");
    }
}

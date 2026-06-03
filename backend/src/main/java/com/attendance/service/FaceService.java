package com.attendance.service;

import com.attendance.dto.Result;
import org.springframework.web.multipart.MultipartFile;

public interface FaceService {
    Result registerFace(Long userId, MultipartFile image);
    Result startAttendance(Long courseId, Long teacherId);
    void stopAttendance(Long courseId);
}

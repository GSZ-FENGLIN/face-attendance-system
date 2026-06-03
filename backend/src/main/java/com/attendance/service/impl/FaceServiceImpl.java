package com.attendance.service.impl;

import com.attendance.dto.Result;
import com.attendance.entity.FaceInfo;
import com.attendance.entity.User;
import com.attendance.mapper.FaceInfoMapper;
import com.attendance.mapper.UserMapper;
import com.attendance.service.FaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FaceServiceImpl implements FaceService {

    @Value("${face.service.url}")
    private String faceServiceUrl;

    @Autowired
    private FaceInfoMapper faceInfoMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Result registerFace(Long userId, MultipartFile image) {
        try {
            // 1. 调用Python人脸服务进行人脸检测与特征提取
            String url = faceServiceUrl + "/api/face/extract";
            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new org.springframework.core.io.ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

            if (response.getBody() == null || !"success".equals(response.getBody().get("status"))) {
                return Result.error("人脸检测失败，请确保照片中包含清晰的人脸");
            }

            String encoding = (String) response.getBody().get("encoding");
            String imageUrl = (String) response.getBody().get("image_url");

            // 2. 删除旧人脸数据
            faceInfoMapper.deleteByUserId(userId);

            // 3. 保存新人脸特征
            FaceInfo faceInfo = new FaceInfo();
            faceInfo.setUserId(userId);
            faceInfo.setFaceEncoding(encoding);
            faceInfo.setImageUrl(imageUrl);
            faceInfoMapper.insert(faceInfo);

            // 4. 更新用户人脸注册状态
            userMapper.updateFaceStatus(userId, 1);

            return Result.success("人脸注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("人脸注册失败: " + e.getMessage());
        }
    }

    @Override
    public Result startAttendance(Long courseId, Long teacherId) {
        try {
            // 调用Python人脸服务开启考勤
            String url = faceServiceUrl + "/api/attendance/start";
            Map<String, Object> params = new HashMap<>();
            params.put("course_id", courseId);

            // 获取该课程所有学生的人脸特征
            List<User> students = userMapper.findByCourseId(courseId);
            List<FaceInfo> faceInfos = faceInfoMapper.findAllEncoded();

            Map<Long, String> studentMap = new HashMap<>();
            Map<Long, String> faceMap = new HashMap<>();
            for (FaceInfo fi : faceInfos) {
                for (User stu : students) {
                    if (fi.getUserId().equals(stu.getId())) {
                        studentMap.put(stu.getId(), stu.getRealName() + "|" + stu.getStudentNo());
                        faceMap.put(stu.getId(), fi.getFaceEncoding());
                        break;
                    }
                }
            }

            params.put("students", studentMap);
            params.put("face_encodings", faceMap);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

            if (response.getBody() != null && "success".equals(response.getBody().get("status"))) {
                return Result.success("考勤已开始");
            }
            return Result.error("启动考勤失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("启动考勤失败: " + e.getMessage());
        }
    }

    @Override
    public void stopAttendance(Long courseId) {
        try {
            String url = faceServiceUrl + "/api/attendance/stop";
            Map<String, Object> params = new HashMap<>();
            params.put("course_id", courseId);
            restTemplate.postForEntity(url, params, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.attendance.mapper;

import com.attendance.entity.FaceInfo;
import java.util.List;

public interface FaceInfoMapper {
    List<FaceInfo> findByUserId(Long userId);
    List<FaceInfo> findAllEncoded();
    int insert(FaceInfo faceInfo);
    int deleteByUserId(Long userId);
}

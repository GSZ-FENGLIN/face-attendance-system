"""
基于人脸识别的智能课堂考勤系统 - Python人脸识别服务
负责：人脸特征提取、实时人脸识别考勤
技术栈：Flask + face_recognition + OpenCV
与SpringBoot后端通过RESTful API通信
"""

import os
import json
import base64
import threading
import time
from io import BytesIO

import cv2
import numpy as np
import face_recognition
from flask import Flask, request, jsonify
from flask_cors import CORS
from PIL import Image

app = Flask(__name__)
CORS(app)

# 配置
UPLOAD_FOLDER = 'uploads'
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
os.makedirs('captures', exist_ok=True)

# 全局变量：存储当前正在进行的考勤任务
# { course_id: { 'known_encodings': [], 'known_names': [], 'thread': Thread, 'running': bool } }
active_attendances = {}

# 考勤结果回调地址 (SpringBoot后端)
BACKEND_URL = "http://localhost:8080"


def load_face_encodings(user_id, image_file):
    """
    加载人脸图片并提取128维特征向量
    返回: (status, encoding_json, image_url)
    """
    try:
        # 读取图片
        image_bytes = image_file.read()
        image = face_recognition.load_image_file(BytesIO(image_bytes))

        # SSD算法检测人脸位置
        face_locations = face_recognition.face_locations(image)

        if len(face_locations) == 0:
            return {"status": "error", "message": "未检测到人脸"}

        # FaceNet提取128维特征向量
        face_encodings = face_recognition.face_encodings(image, face_locations)

        if len(face_encodings) == 0:
            return {"status": "error", "message": "人脸特征提取失败"}

        # 取第一张人脸的特征向量
        encoding = face_encodings[0].tolist()

        # 保存上传的图片
        filename = f"face_{user_id}_{int(time.time())}.jpg"
        filepath = os.path.join(UPLOAD_FOLDER, filename)
        pil_image = Image.fromarray(image)
        pil_image.save(filepath)

        return {
            "status": "success",
            "encoding": json.dumps(encoding),
            "image_url": f"/uploads/{filename}"
        }
    except Exception as e:
        return {"status": "error", "message": str(e)}


def recognize_faces(frame, known_encodings, known_names, threshold=0.6):
    """
    在单帧图像中进行人脸识别
    使用SSD检测人脸 -> FaceNet提取特征 -> 欧氏距离比对

    参数:
        frame: BGR图像
        known_encodings: 已知人脸特征列表
        known_names: 对应的人名/学号列表
        threshold: 匹配阈值(欧氏距离)
    返回:
        [(name, (top, right, bottom, left), confidence)]
    """
    # 转换BGR到RGB (face_recognition使用RGB)
    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

    # SSD算法检测所有人脸位置
    face_locations = face_recognition.face_locations(rgb_frame)

    if not face_locations:
        return []

    # FaceNet提取128维特征向量
    face_encodings = face_recognition.face_encodings(rgb_frame, face_locations)

    results = []
    for face_encoding, face_location in zip(face_encodings, face_locations):
        if not known_encodings:
            results.append(("Unknown", face_location, 0.0))
            continue

        # 计算与所有已知人脸的欧氏距离
        distances = face_recognition.face_distance(known_encodings, face_encoding)
        best_match_index = np.argmin(distances)
        min_distance = distances[best_match_index]

        if min_distance < threshold:
            name = known_names[best_match_index]
            # 置信度 = 1 - 归一化距离
            confidence = float(1.0 - min_distance / threshold)
            results.append((name, face_location, confidence))
        else:
            results.append(("Unknown", face_location, 0.0))

    return results


def attendance_worker(course_id, known_encodings, known_names, camera_id=0):
    """
    考勤线程：持续从摄像头采集画面进行人脸识别
    对应文档中的 4.2.2 实时人脸识别考勤模块
    """
    video_capture = cv2.VideoCapture(camera_id)

    # 已签到学生集合(去重)
    recognized_students = set()

    attendance_data = active_attendances.get(course_id)
    if attendance_data is None:
        return

    last_report_time = time.time()
    report_interval = 2  # 每2秒向服务端报告一次识别结果

    while attendance_data.get('running', False):
        ret, frame = video_capture.read()
        if not ret:
            time.sleep(0.1)
            continue

        # 调整帧大小以提升性能
        small_frame = cv2.resize(frame, (0, 0), fx=0.5, fy=0.5)

        # 人脸识别
        results = recognize_faces(small_frame, known_encodings, known_names)

        current_time = time.strftime("%H:%M:%S")

        for name, location, confidence in results:
            if name != "Unknown":
                # 提取学号和姓名
                parts = name.split('|')
                student_id = parts[0] if len(parts) > 1 else name

                if student_id not in recognized_students:
                    recognized_students.add(student_id)
                    print(f"[考勤] 识别到学生: {name}, 置信度: {confidence:.2f}, 时间: {current_time}")

            # 在原图上绘制检测框和标签 (坐标需要x2因为缩小过)
            top, right, bottom, left = [int(x * 2) for x in location]
            color = (0, 255, 0) if name != "Unknown" else (0, 0, 255)
            cv2.rectangle(frame, (left, top), (right, bottom), color, 2)

            display_name = name.split('|')[-1] if '|' in name else name
            cv2.rectangle(frame, (left, bottom - 35), (right, bottom), color, cv2.FILLED)
            cv2.putText(frame, f"{display_name} {confidence:.2f}", (left + 6, bottom - 6),
                        cv2.FONT_HERSHEY_DUPLEX, 0.6, (255, 255, 255), 1)

        # 显示识别画面
        cv2.putText(frame, f"Course: {course_id} | Students: {len(recognized_students)}",
                    (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 255, 0), 2)
        cv2.imshow(f'Attendance - Course {course_id}', frame)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    video_capture.release()
    cv2.destroyAllWindows()
    print(f"[考勤] 课程 {course_id} 考勤结束，共识别 {len(recognized_students)} 名学生")


# ========== API接口 ==========

@app.route('/api/face/extract', methods=['POST'])
def extract_face():
    """
    人脸特征提取接口
    由SpringBoot后端调用，用于学生注册人脸时提取特征
    """
    if 'image' not in request.files:
        return jsonify({"status": "error", "message": "未上传图片"})

    image_file = request.files['image']
    user_id = request.form.get('user_id', '0')

    result = load_face_encodings(user_id, image_file)
    return jsonify(result)


@app.route('/api/attendance/start', methods=['POST'])
def start_attendance():
    """
    开始考勤接口
    由SpringBoot后端调用，开启实时人脸识别考勤
    """
    data = request.json
    course_id = data.get('course_id')
    face_encodings = data.get('face_encodings', {})
    students = data.get('students', {})

    if not course_id:
        return jsonify({"status": "error", "message": "缺少课程ID"})

    # 如果已有考勤在运行，先停止
    if course_id in active_attendances:
        active_attendances[course_id]['running'] = False
        time.sleep(1)

    # 准备已知人脸数据
    known_encodings = []
    known_names = []
    uid_to_name = {}

    for uid, encoding_str in face_encodings.items():
        try:
            encoding_list = json.loads(encoding_str)
            known_encodings.append(np.array(encoding_list))
            student_info = students.get(uid, f"{uid}|Unknown")
            known_names.append(student_info)
            uid_to_name[uid] = student_info
        except Exception as e:
            print(f"加载人脸特征失败 user_id={uid}: {e}")

    if not known_encodings:
        return jsonify({"status": "error", "message": "无人脸数据"})

    # 启动考勤线程
    attendance_data = {
        'known_encodings': known_encodings,
        'known_names': known_names,
        'running': True
    }

    thread = threading.Thread(
        target=attendance_worker,
        args=(course_id, known_encodings, known_names),
        daemon=True
    )
    thread.start()
    attendance_data['thread'] = thread
    active_attendances[course_id] = attendance_data

    return jsonify({
        "status": "success",
        "message": f"考勤已开始，课程 {course_id}，共 {len(known_encodings)} 名学生",
        "student_count": len(known_encodings)
    })


@app.route('/api/attendance/stop', methods=['POST'])
def stop_attendance():
    """
    停止考勤接口
    """
    data = request.json
    course_id = data.get('course_id')

    if course_id in active_attendances:
        active_attendances[course_id]['running'] = False
        del active_attendances[course_id]

    return jsonify({"status": "success", "message": "考勤已停止"})


@app.route('/api/health', methods=['GET'])
def health():
    """健康检查"""
    return jsonify({
        "status": "ok",
        "active_attendances": len(active_attendances)
    })


@app.route('/uploads/<filename>')
def uploaded_file(filename):
    """提供上传文件访问"""
    return flask.send_from_directory(UPLOAD_FOLDER, filename)


if __name__ == '__main__':
    import flask
    print("=" * 50)
    print("人脸识别服务启动中...")
    print(f"  后端地址: {BACKEND_URL}")
    print(f"  监听端口: 5000")
    print("=" * 50)
    app.run(host='0.0.0.0', port=5000, debug=False)

# 基于人脸识别的智能课堂考勤管理系统

## 项目概述

本项目是一套完整的基于人脸识别的智能课堂考勤管理系统，采用前后端分离架构，集成了 SSD 人脸检测算法与 FaceNet 深度学习人脸识别算法，实现了从人脸信息采集、实时课堂人脸身份比对到考勤数据自动统计分析的全流程自动化管理。

### 核心功能

- **用户管理**：管理员管理教师/学生账号，多角色权限划分
- **课程管理**：教师管理课程信息、上课时间、班级
- **人脸注册**：学生通过摄像头拍照完成人脸特征注册
- **实时考勤**：基于摄像头实时画面自动识别人脸并记录考勤
- **考勤管理**：查看、筛选考勤记录，支持多维度查询
- **数据统计**：可视化考勤统计图表
- **报表导出**：考勤数据导出为 Excel

### 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | SpringBoot 2.7 + MyBatis |
| 前端框架 | Vue.js 3 + Element Plus + Vite |
| 数据库 | MySQL 8.0 |
| 人脸识别 | Python + face_recognition + OpenCV |
| 认证授权 | JWT |
| 图表 | ECharts |

## 项目结构

```
face-attendance-system/
├── backend/                    # SpringBoot 后端
│   ├── pom.xml                 # Maven 依赖配置
│   └── src/main/
│       ├── java/com/attendance/
│       │   ├── AttendanceApplication.java   # 启动类
│       │   ├── config/                      # 配置（CORS、JWT、异常处理）
│       │   ├── controller/                  # RESTful API 控制器
│       │   ├── entity/                      # 实体类
│       │   ├── mapper/                      # MyBatis Mapper 接口
│       │   ├── service/                     # 业务逻辑
│       │   └── dto/                         # 数据传输对象
│       └── resources/
│           ├── application.yml              # 应用配置
│           ├── schema.sql                   # 数据库初始化脚本
│           └── mapper/                      # MyBatis XML 映射
├── frontend/                   # Vue.js 前端
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── api/                # API 请求封装
│       ├── router/             # 路由配置
│       ├── views/              # 页面组件
│       └── styles/             # 全局样式
├── face-service/               # Python 人脸识别服务
│   ├── requirements.txt        # Python 依赖
│   └── app.py                  # Flask 服务（人脸检测+识别）
└── README.md
```

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0
- Python 3.8+
- 摄像头（用于人脸采集和实时识别）

### 1. 数据库初始化

```sql
-- 执行 SQL 脚本创建数据库和表
mysql -u root -p < backend/src/main/resources/schema.sql
```

### 2. 启动后端

```bash
# 修改 application.yml 中的数据库连接信息
cd backend
mvn clean package -DskipTests
java -jar target/face-attendance-backend-1.0.0.jar
# 或使用 Maven 插件直接运行
mvn spring-boot:run
```

后端默认启动在 `http://localhost:8080`

### 3. 启动人脸识别服务

```bash
cd face-service
pip install -r requirements.txt
python app.py
```

人脸服务默认启动在 `http://localhost:5000`

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认启动在 `http://localhost:3000`

## 默认账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | 123456 |
| 教师 | teacher01 | 123456 |
| 学生 | student01 | 123456 |
| 学生 | student02 | 123456 |
| 学生 | student03 | 123456 |

## 系统流程

1. **管理员**登录 → 用户管理 → 添加教师/学生账号
2. **教师**登录 → 课程管理 → 添加课程信息
3. **学生**登录 → 人脸注册 → 通过摄像头拍照完成人脸特征采集
4. **教师**登录 → 考勤管理 → 选择课程 → 点击"开始考勤"
5. 系统自动调用摄像头进行实时人脸识别考勤
6. 考勤结束后可查看记录、统计图表并导出 Excel

## 人脸识别算法说明

- **SSD (Single Shot MultiBox Detector)**: 用于快速检测图像中的人脸位置
- **FaceNet**: 将人脸图像映射到 128 维欧氏空间特征向量
- **欧氏距离比对**: 计算特征向量之间的距离，阈值 0.6 判定是否匹配

使用 Python 的 `face_recognition` 库（基于 dlib + 深度学习）封装上述算法，提供 RESTful API 供后端调用。

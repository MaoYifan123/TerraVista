# TerraVista

TerraVista是一个基于Spring Boot和Vue.js的现代化Web应用程序，以实现POI可视化方便用户制作旅游计划。

## 项目架构

项目采用前后端分离的架构设计：

### 后端技术栈
- Spring Boot 3.4.4
- Spring Security
- Spring Data JPA
- MySQL 8.0
- MyBatis-Plus 3.5.9
- JWT认证
- Resilience4j限流
- Swagger/OpenAPI 3.0
- Lombok
- EasyExcel
- Hutool

### 前端技术栈
- Vue 3
- Vue Router 4
- Pinia/Vuex 4
- Element Plus
- Axios
- AMap (高德地图)
- Sass

## 项目结构
```
TerraVista/
├── src/                                # 后端源代码
│   ├── main/
│   │   ├── java/com/example/terravista/
│   │   │   ├── config/                 # 配置类
│   │   │   ├── controller/            # 控制器层
│   │   │   ├── dto/                   # 数据传输对象
│   │   │   ├── entity/                # 实体类
│   │   │   ├── repository/            # 数据访问层
│   │   │   ├── security/              # 安全相关配置
│   │   │   ├── service/               # 业务逻辑层
│   │   │   ├── util/                  # 工具类
│   │   │   └── TerraVistaApplication.java  # 应用程序入口
│   │   └── resources/
│   │       ├── static/                # 静态资源
│   │       ├── templates/             # 模板文件
│   │       └── application.properties # 应用配置文件
│   └── test/                          # 测试代码
├── Client/                            # 前端Vue项目
│   ├── src/
│   │   ├── api/                      # API 请求相关
│   │   ├── assets/                   # 静态资源
│   │   ├── components/               # 公共组件
│   │   ├── router/                   # 路由配置
│   │   ├── stores/                   # Pinia状态管理
│   │   ├── utils/                    # 工具函数
│   │   ├── views/                    # 页面组件
│   │   ├── App.vue                   # 根组件
│   │   └── main.js                   # 入口文件
│   ├── public/                       # 公共资源
│   ├── package.json                  # 项目依赖配置
│   └── vue.config.js                 # Vue配置文件
├── Admin/                            # 管理端
├── Plot/                            # 用户界面交互可视化相关
└── SQL/                             # 数据库脚本
```

## 环境要求
- JDK 21
- Node.js
- MySQL 8.0
- Maven

## 快速开始

### 后端启动
1. 配置数据库
   - 创建MySQL数据库
   - 执行SQL目录下的数据库脚本

2. 配置application.properties
   - 设置数据库连接信息
   - 配置JWT密钥
   - 配置其他必要的参数

3. 启动Spring Boot应用
```bash
mvn spring-boot:run
```

### 前端启动
1. 进入Client目录
```bash
cd Client
```

2. 安装依赖
```bash
npm install
```

3. 启动开发服务器
```bash
npm run serve
```

4. 构建生产版本
```bash
npm run build
```

## 主要功能
- 用户认证与授权
- 数据可视化
- 地图服务集成
- 数据导入导出
- API限流保护

## 开发指南
- 后端API文档访问：http://localhost:8080/swagger-ui.html
- 遵循RESTful API设计规范
- 使用JWT进行身份验证
- 实现了请求限流保护

## 注意事项
- 确保MySQL服务已启动
- 检查数据库连接配置
- 前端开发时注意跨域配置
- 生产环境部署时注意修改相关配置

## 贡献指南
1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request


# 游戏攻略知识库系统 - 项目总览

## 🎮 项目简介

这是一个完整的游戏攻略知识库管理系统，包含用户认证、权限控制、内容管理等功能。

## 📦 项目结构

```
game-guide/
├── database/                      # 数据库脚本
│   ├── init.sql                  # 基础表结构
│   └── user.sql                  # 用户表
├── game-guide-backend/           # 后端项目
│   ├── src/
│   │   ├── main/java/com/gameguide/
│   │   │   ├── controller/       # 控制器
│   │   │   ├── service/          # 服务层
│   │   │   ├── mapper/           # 数据访问层
│   │   │   ├── entity/           # 实体类
│   │   │   ├── dto/              # 数据传输对象
│   │   │   ├── vo/               # 视图对象
│   │   │   ├── security/         # 安全配置
│   │   │   ├── common/           # 公共类
│   │   │   └── exception/        # 异常处理
│   │   └── resources/
│   │       ├── mapper/           # MyBatis XML
│   │       └── application.yml   # 配置文件
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── pom.xml
├── frontend/                      # 前端项目
│   ├── src/
│   │   ├── api/                  # API 接口
│   │   ├── store/                # 状态管理
│   │   ├── router/               # 路由配置
│   │   ├── views/                # 页面组件
│   │   ├── layout/               # 布局组件
│   │   ├── utils/                # 工具类
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
├── init-database.bat             # Windows 数据库初始化脚本
├── init-database.sh              # Linux/Mac 数据库初始化脚本
├── 运行指南.md                   # 详细运行指南
└── README.md                     # 项目说明
```

## 🚀 快速开始

### 1️⃣ 数据库初始化

**Windows:**
```bash
init-database.bat
```

**Linux/Mac:**
```bash
chmod +x init-database.sh
./init-database.sh
```

### 2️⃣ 启动后端

```bash
cd game-guide-backend
mvn spring-boot:run
```

访问: http://localhost:8080/api

### 3️⃣ 启动前端

```bash
cd frontend
npm install
npm run dev
```

访问: http://localhost:5173

### 4️⃣ 登录系统

- **管理员**: `admin` / `admin123`
- **普通用户**: `user` / `admin123`

## 🔧 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- MyBatis
- PostgreSQL 15
- BCrypt 密码加密

### 前端
- Vue 3
- Vite
- Vue Router
- Pinia
- Element Plus
- Axios

## ✨ 功能特性

### 认证授权
- ✅ 用户注册登录
- ✅ JWT Token 认证
- ✅ 基于角色的权限控制（ADMIN/USER）
- ✅ Token 自动刷新
- ✅ 路由守卫

### 业务功能
- ✅ 游戏管理（CRUD）
- ✅ 攻略管理（CRUD + 搜索）
- ✅ 分类管理（CRUD）
- ✅ 标签管理（CRUD）
- ✅ 分页查询
- ✅ 数据统计

### 权限控制
- **ADMIN**: 可以增删改查所有数据
- **USER**: 只能查看数据，不能修改

## 📖 API 文档

详见 [运行指南.md](./运行指南.md)

## 🎯 核心实现

### 后端安全配置

1. **JWT 认证**: 使用 JJWT 库生成和验证 Token
2. **密码加密**: BCrypt 加密存储
3. **无状态会话**: Stateless Session
4. **CORS 配置**: 支持跨域请求
5. **权限拦截**: 基于角色的方法级权限控制

### 前端核心功能

1. **Axios 封装**: 自动携带 Token，统一错误处理
2. **路由守卫**: 未登录自动跳转登录页
3. **状态管理**: Pinia 管理用户状态
4. **权限控制**: 根据角色显示/隐藏操作按钮

## 📝 开发说明

### 后端开发

```bash
# 编译
mvn clean compile

# 运行测试
mvn test

# 打包
mvn clean package

# 运行
java -jar target/game-guide-backend-1.0.0.jar
```

### 前端开发

```bash
# 安装依赖
npm install

# 开发模式
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

## 🐳 Docker 部署

```bash
cd game-guide-backend
docker-compose up -d
```

## 📊 数据库设计

### 核心表
- `user` - 用户表
- `game` - 游戏表
- `guide` - 攻略表
- `category` - 分类表
- `tag` - 标签表
- `guide_tag` - 攻略标签关联表

详细设计见 `database/init.sql` 和 `database/user.sql`

## 🔐 安全特性

1. **密码加密**: BCrypt 不可逆加密
2. **JWT Token**: 有效期 24 小时
3. **HTTPS 支持**: 生产环境建议启用
4. **SQL 注入防护**: MyBatis 参数化查询
5. **XSS 防护**: 前端输入验证

## 📱 浏览器支持

- Chrome (推荐)
- Firefox
- Safari
- Edge

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

## 📞 技术支持

遇到问题请查看 [运行指南.md](./运行指南.md) 或提交 Issue。

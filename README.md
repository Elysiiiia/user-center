# Demo1 项目说明

## 项目简介
Test2
Demo1 是一个基于 Spring Boot 构建的后端项目，集成了 MyBatis Plus、Redis、Redisson 等常用组件，实现了用户与团队的基本管理功能。适合作为学习和实际开发的脚手架项目。

## 主要功能

- 用户注册、登录、信息管理
- 团队的创建、查询、加入、退出、解散
- 用户与团队的关联管理
- 基于 Redis 的缓存与分布式锁
- 全局异常处理与统一响应封装

## 技术栈

- Spring Boot
- MyBatis Plus
- Redis & Redisson
- Lombok
- Maven
- Docker

## 快速启动

### 1. 克隆项目

```bash
git clone <your-repo-url>
cd demo1
```

### 2. 配置数据库

- 创建数据库并执行 `sql/create_table.sql` 初始化表结构。
- 修改 `src/main/resources/application.yml` 数据库连接配置。

### 3. 启动 Redis

本地需启动 Redis 服务，或修改配置连接远程 Redis。

### 4. 启动项目

```bash
# 使用 Maven Wrapper 启动
./mvnw spring-boot:run
```

或

```bash
# 直接用 Docker 启动
docker build -t demo1 .
docker run -p 8080:8080 demo1
```

### 5. 访问接口

项目默认端口为 8080，可通过 Postman 或浏览器访问接口。

## 目录结构

```text
demo1/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── common/         # 通用响应、异常、分页等
│   │   │   ├── config/         # 配置类（MyBatis Plus、Redis、Redisson）
│   │   │   ├── contant/        # 常量
│   │   │   ├── controller/     # 控制器（接口层）
│   │   │   ├── exception/      # 全局异常处理
│   │   │   ├── job/            # 定时任务
│   │   │   ├── mapper/         # MyBatis Mapper 接口
│   │   │   ├── model/          # 实体、DTO、VO、枚举、请求对象
│   │   │   ├── once/           # 一次性脚本
│   │   │   ├── service/        # 业务接口与实现
│   │   │   └── utils/          # 工具类
│   │   └── resources/
│   │       ├── application.yml # 配置文件
│   │       └── ...             # 其他资源
│   └── test/                   # 测试代码
├── sql/                        # 数据库建表脚本
├── Dockerfile                  # Docker 构建文件
├── pom.xml                     # Maven 配置
└── README.md                   # 项目说明
```

## 接口格式

### 通用响应格式

所有接口统一返回如下结构：

```json
{
  "code": 0,
  "data": {},
  "message": "ok",
  "description": ""
}
```

- `code`：状态码（0为成功，其他为错误）
- `data`：返回数据
- `message`：简要信息
- `description`：详细描述（可选）

---

### 用户相关接口

#### 1. 用户注册

- **URL**：`POST /user/register`
- **请求体**：

```json
{
  "userAccount": "testuser",
  "userPassword": "12345678",
  "checkPassword": "12345678"
}
```

- **响应**：

```json
{
  "code": 0,
  "data": 123,
  "message": "ok"
}
```

#### 2. 用户登录

- **URL**：`POST /user/login`
- **请求体**：

```json
{
  "userAccount": "testuser",
  "userPassword": "12345678"
}
```

- **响应**：

```json
{
  "code": 0,
  "data": {
    "id": 123,
    "userAccount": "testuser",
    "userName": "测试用户",
    "userRole": "user"
  },
  "message": "ok"
}
```

#### 3. 获取当前登录用户

- **URL**：`GET /user/current`
- **响应**：

```json
{
  "code": 0,
  "data": {
    "id": 123,
    "userAccount": "testuser",
    "userName": "测试用户"
  },
  "message": "ok"
}
```

#### 4. 用户注销

- **URL**：`POST /user/logout`
- **响应**：

```json
{
  "code": 0,
  "data": 1,
  "message": "ok"
}
```

---

### 团队相关接口

#### 1. 创建团队

- **URL**：`POST /team/add`
- **请求体**：

```json
{
  "name": "开发组",
  "description": "负责开发",
  "maxNum": 10,
  "status": 0,
  "password": "team123"
}
```

- **响应**：

```json
{
  "code": 0,
  "data": 101,
  "message": "ok"
}
```

#### 2. 查询团队

- **URL**：`GET /team/list`
- **请求参数**（可选，分页与筛选）：

| 参数名      | 类型   | 说明         |
| ----------- | ------ | ------------ |
| pageNum     | int    | 页码         |
| pageSize    | int    | 每页数量     |
| name        | string | 团队名称     |
| status      | int    | 团队状态     |

- **响应**：

```json
{
  "code": 0,
  "data": [
    {
      "id": 101,
      "name": "开发组",
      "description": "负责开发",
      "maxNum": 10,
      "status": 0,
      "createUser": {
        "id": 123,
        "userName": "测试用户"
      }
    }
  ],
  "message": "ok"
}
```

#### 3. 加入团队

- **URL**：`POST /team/join`
- **请求体**：

```json
{
  "teamId": 101,
  "password": "team123"
}
```

- **响应**：

```json
{
  "code": 0,
  "data": 1,
  "message": "ok"
}
```

#### 4. 退出团队

- **URL**：`POST /team/quit`
- **请求体**：

```json
{
  "teamId": 101
}
```

- **响应**：

```json
{
  "code": 0,
  "data": 1,
  "message": "ok"
}
```

#### 5. 解散团队

- **URL**：`POST /team/delete`
- **请求体**：

```json
{
  "id": 101
}
```

- **响应**：

```json
{
  "code": 0,
  "data": 1,
  "message": "ok"
}
```

---

### 其他说明

- 所有 POST 请求均需 `Content-Type: application/json`
- 登录后部分接口需携带 Cookie 或 Token（如有实现）

如需详细字段说明或补充其他接口，请查阅源码或联系作者。 
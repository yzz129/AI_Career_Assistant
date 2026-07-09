# InternAI Career Assistant / AI 实习就业助手系统

一个用于实训答辩演示的前后端分离项目：后端使用 Spring Boot 3 + JDK 17 + MyBatis-Plus + MySQL + JWT，前端使用 Vue 3 + Vite + Element Plus + Pinia + Vue Router。系统默认开启 AI mock 模式，可直接演示 SSE 流式输出。

## 项目结构

```text
.
├── src/main/java/com/internai/career   # Spring Boot 后端
├── src/main/resources/application.yml  # 数据库、JWT、AI 配置
├── sql/init.sql                        # 建库建表与演示数据
├── frontend                            # Vue 3 前端
└── README.md
```

## 技术栈

- 后端：Spring Boot 3.3.5、JDK 17、Maven、MyBatis-Plus、MySQL 8、JWT、Spring Validation、SSE
- 前端：Vue 3、Vite、Element Plus、Axios、Pinia、Vue Router
- AI：OpenAI 兼容接口封装，支持 DeepSeek / 通义千问 / 智谱等兼容 Chat Completions 的模型；默认 `ai.mock=true`

## 数据库导入

1. 确认本机 MySQL 8 正在运行。
2. 按需修改 [application.yml](/d:/Idea/AI_Career_Assistant/src/main/resources/application.yml:8) 中数据库用户名和密码。
3. 导入初始化 SQL：

```bash
mysql -uroot -proot < sql/init.sql
```

默认数据库名：`internai_career`。

## 后端启动

```bash
mvn spring-boot:run
```

后端地址：`http://localhost:8080`

也可以先打包：

```bash
mvn -DskipTests package
java -jar target/career-assistant-1.0.0.jar
```

## 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端地址：`http://localhost:5173`

生产构建：

```bash
cd frontend
npm run build
```

## 演示账号

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| `admin` | `123456` | `ADMIN` 管理员 |
| `teacher` | `123456` | `TEACHER` 指导教师 |
| `student` | `123456` | `STUDENT` 学生 |

说明：`sql/init.sql` 中为便于导入演示，初始密码是 `123456`。第一次登录成功后，后端会自动把该账号密码升级为 BCrypt 密文；后续登录使用 BCrypt 校验。

## AI 配置

默认 mock 模式，不需要 API Key：

```yaml
ai:
  api-key:
  base-url: https://api.openai.com/v1
  model: gpt-4o-mini
  mock: true
```

如需接入 DeepSeek、通义千问、智谱等 OpenAI 兼容接口：

```yaml
ai:
  api-key: 你的 API Key
  base-url: 兼容接口地址，例如 https://api.deepseek.com/v1
  model: deepseek-chat
  mock: false
```

敏感配置都在 [application.yml](/d:/Idea/AI_Career_Assistant/src/main/resources/application.yml:1) 中，不在代码中硬编码。

## 核心接口

### 登录与鉴权

- `POST /api/auth/login`：登录，返回 `token`、`userId`、`username`、`realName`、`roleCode`
- `GET /api/auth/me`：获取当前登录用户
- `GET /api/dashboard/overview`：首页看板聚合数据，从数据库读取用户档案、岗位、申请、简历和知识库内容

除 `/api/auth/login` 外，所有 `/api/**` 接口都需要请求头：

```text
Authorization: Bearer <token>
```

### 管理 CRUD

- `GET /api/users/page`、`POST /api/users`、`PUT /api/users/{id}`、`DELETE /api/users/{id}`
- `GET /api/students/page`、`POST /api/students`、`PUT /api/students/{id}`、`DELETE /api/students/{id}`
- `GET /api/teachers/page`、`POST /api/teachers`、`PUT /api/teachers/{id}`、`DELETE /api/teachers/{id}`
- `GET /api/companies/page`、`POST /api/companies`、`PUT /api/companies/{id}`、`DELETE /api/companies/{id}`
- `GET /api/jobs/page`、`POST /api/jobs`、`PUT /api/jobs/{id}`、`DELETE /api/jobs/{id}`
- `GET /api/knowledge/page`、`POST /api/knowledge`、`PUT /api/knowledge/{id}`、`DELETE /api/knowledge/{id}`

分页统一参数：`pageNum`、`pageSize`。

### 实习申请、日志、简历

- `POST /api/apply/submit`：学生提交实习申请
- `POST /api/apply/review`：教师审核申请，状态为 `PENDING`、`APPROVED`、`REJECTED`
- `GET /api/apply/my`：学生查看自己的申请
- `GET /api/apply/todo`：教师查看负责学生的申请
- `POST /api/log/save`：学生保存/提交实习日志
- `POST /api/log/comment`：教师点评日志
- `GET /api/log/my`：学生查看自己的日志
- `GET /api/log/todo`：教师查看负责学生的日志
- `POST /api/resume/save`：学生保存简历
- `GET /api/resume/my`：学生查看自己的简历

### AI SSE 流式接口

- `GET /api/ai/resume/optimize?resumeId=`
- `GET /api/ai/job/recommend?studentId=`
- `GET /api/ai/interview?jobId=&answer=`
- `GET /api/ai/log/summary?studentId=`
- `GET /api/ai/kb/chat?question=`

返回类型：`text/event-stream`。

## 答辩演示建议

1. 使用 `admin / 123456` 登录，维护企业和岗位。
2. 使用 `student / 123456` 登录，查看岗位中心并提交实习申请。
3. 使用 `teacher / 123456` 登录，审核学生申请并点评日志。
4. 使用 `student / 123456` 登录，编辑简历并打开 AI 助手。
5. 演示 AI 简历优化、岗位推荐、模拟面试和知识库问答的流式输出。

## 已验证

- `mvn -q -DskipTests package` 通过。
- `cd frontend && npm run build` 通过。

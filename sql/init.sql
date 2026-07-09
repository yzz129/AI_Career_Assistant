CREATE DATABASE IF NOT EXISTS internai_career DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE internai_career;

DROP TABLE IF EXISTS ai_chat_message;
DROP TABLE IF EXISTS ai_chat_session;
DROP TABLE IF EXISTS internship_log;
DROP TABLE IF EXISTS internship_apply;
DROP TABLE IF EXISTS resume;
DROP TABLE IF EXISTS knowledge_doc;
DROP TABLE IF EXISTS job_position;
DROP TABLE IF EXISTS company;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS teacher;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_role;

CREATE TABLE sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code VARCHAR(32) NOT NULL UNIQUE,
  role_name VARCHAR(64) NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(120) NOT NULL,
  real_name VARCHAR(64) NOT NULL,
  role_code VARCHAR(32) NOT NULL,
  phone VARCHAR(32),
  email VARCHAR(120),
  status VARCHAR(20) DEFAULT 'ENABLED',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE teacher (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  teacher_no VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(64) NOT NULL,
  department VARCHAR(120),
  title VARCHAR(64),
  phone VARCHAR(32),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE student (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  student_no VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(64) NOT NULL,
  major VARCHAR(120),
  grade VARCHAR(32),
  teacher_id BIGINT,
  skills VARCHAR(500),
  intention_city VARCHAR(120),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE company (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  company_name VARCHAR(160) NOT NULL,
  industry VARCHAR(120),
  city VARCHAR(80),
  contact_name VARCHAR(64),
  contact_phone VARCHAR(32),
  description TEXT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE job_position (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  company_id BIGINT,
  company_name VARCHAR(160),
  title VARCHAR(160) NOT NULL,
  city VARCHAR(80),
  salary_range VARCHAR(80),
  skill_keyword VARCHAR(300),
  description TEXT,
  status VARCHAR(20) DEFAULT 'OPEN',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE internship_apply (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  teacher_id BIGINT,
  job_id BIGINT NOT NULL,
  job_title VARCHAR(160),
  company_name VARCHAR(160),
  reason TEXT,
  status VARCHAR(20) DEFAULT 'PENDING',
  review_comment TEXT,
  apply_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  review_time DATETIME
);

CREATE TABLE internship_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  teacher_id BIGINT,
  log_date DATE NOT NULL,
  content TEXT NOT NULL,
  status VARCHAR(20) DEFAULT 'DRAFT',
  teacher_comment TEXT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE resume (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  title VARCHAR(160) NOT NULL,
  content TEXT,
  file_url VARCHAR(500),
  ai_suggestion TEXT,
  version INT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE ai_chat_session (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  type VARCHAR(40) NOT NULL,
  title VARCHAR(160),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ai_chat_message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  session_id BIGINT NOT NULL,
  role VARCHAR(20) NOT NULL,
  content LONGTEXT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE knowledge_doc (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(180) NOT NULL,
  category VARCHAR(80),
  content TEXT NOT NULL,
  keywords VARCHAR(300),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO sys_role(role_code, role_name) VALUES
('ADMIN', '管理员'),
('TEACHER', '指导教师'),
('STUDENT', '学生');

INSERT INTO sys_user(id, username, password, real_name, role_code, phone, email, status) VALUES
(1, 'admin', '123456', '系统管理员', 'ADMIN', '13800000000', 'admin@internai.local', 'ENABLED'),
(2, 'teacher', '123456', '王老师', 'TEACHER', '13800000001', 'teacher@internai.local', 'ENABLED'),
(3, 'student', '123456', '李同学', 'STUDENT', '13800000002', 'student@internai.local', 'ENABLED');

INSERT INTO teacher(id, user_id, teacher_no, name, department, title, phone) VALUES
(1, 2, 'T2026001', '王老师', '软件工程学院', '就业指导教师', '13800000001');

INSERT INTO student(id, user_id, student_no, name, major, grade, teacher_id, skills, intention_city) VALUES
(1, 3, 'S2026001', '李同学', '软件工程', '2023级', 1, 'Java, Spring Boot, Vue, MySQL, AI 应用开发', '杭州 / 上海');

INSERT INTO company(id, company_name, industry, city, contact_name, contact_phone, description) VALUES
(1, '星芽科技有限公司', '人工智能 / 企业服务', '杭州', '陈 HR', '0571-88880001', '专注 AI 办公与校园数字化产品。'),
(2, '云桥软件工作室', '软件开发', '上海', '赵经理', '021-66660002', '为中小企业提供 Web 与数据应用开发服务。');

INSERT INTO job_position(company_id, company_name, title, city, salary_range, skill_keyword, description, status) VALUES
(1, '星芽科技有限公司', 'Java 后端实习生', '杭州', '180-220/天', 'Java, Spring Boot, MyBatis-Plus, MySQL', '参与就业服务平台后端接口开发、接口联调与基础 AI 能力接入。', 'OPEN'),
(1, '星芽科技有限公司', 'AI 应用开发实习生', '杭州', '200-260/天', 'Prompt, SSE, Vue, Java', '参与 AI 问答、简历优化、知识库检索等应用功能开发。', 'OPEN'),
(2, '云桥软件工作室', 'Vue 前端实习生', '上海', '160-220/天', 'Vue 3, Vite, Element Plus, Axios', '负责业务页面开发、组件封装和交互优化。', 'OPEN');

INSERT INTO resume(student_id, title, content, version) VALUES
(1, '李同学的后端实习简历', '教育背景：软件工程专业。项目经历：InternAI Career Assistant，负责 Spring Boot 接口、JWT 鉴权、Vue 页面联调。技能：Java、Spring Boot、MySQL、Vue。求职方向：Java 后端 / AI 应用开发。', 1);

INSERT INTO internship_log(student_id, teacher_id, log_date, content, status) VALUES
(1, 1, CURRENT_DATE, '今天完成了岗位列表接口联调，学习了 MyBatis-Plus 分页查询和 Axios 拦截器。遇到的问题是 Token 过期后页面跳转处理不够顺滑。', 'SUBMITTED');

INSERT INTO knowledge_doc(title, category, content, keywords) VALUES
('校内实习申请流程', '流程制度', '学生选择岗位后提交实习申请，指导教师审核通过后可进入实习日志阶段。申请材料应真实完整。', '实习申请,审核,指导教师'),
('简历优化建议', '就业指导', '简历项目经历建议采用任务、行动、结果结构描述。不要编造不存在的经历，可突出课程设计、实训项目和个人贡献。', '简历,项目经历,STAR'),
('面试准备清单', '就业指导', '面试前应准备自我介绍、项目复盘、岗位技能匹配说明，并提前了解公司业务。', '面试,自我介绍,项目复盘');

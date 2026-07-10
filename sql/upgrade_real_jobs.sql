USE internai_career;

-- Existing installations: safe to run repeatedly.
DROP PROCEDURE IF EXISTS add_job_source_columns;
DELIMITER //
CREATE PROCEDURE add_job_source_columns()
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'job_position' AND COLUMN_NAME = 'source_name'
  ) THEN
    ALTER TABLE job_position ADD COLUMN source_name VARCHAR(80) AFTER description;
  END IF;
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'job_position' AND COLUMN_NAME = 'source_url'
  ) THEN
    ALTER TABLE job_position ADD COLUMN source_url VARCHAR(500) AFTER source_name;
  END IF;
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'job_position' AND COLUMN_NAME = 'source_checked_at'
  ) THEN
    ALTER TABLE job_position ADD COLUMN source_checked_at DATE AFTER source_url;
  END IF;
END//
DELIMITER ;
CALL add_job_source_columns();
DROP PROCEDURE add_job_source_columns;

INSERT INTO company(id, company_name, industry, city, contact_name, contact_phone, description) VALUES
(1, '云智迪', '计算机软件', '杭州', NULL, NULL, 'BOSS 直聘公开岗位页面中的招聘企业。'),
(2, '杭州十五分钟文化传媒', '文化传媒 / AI 内容', '杭州', NULL, NULL, 'BOSS 直聘公开岗位页面中的招聘企业。'),
(3, '幻量科技', '软件开发 / 人工智能', '上海', NULL, NULL, 'BOSS 直聘公开岗位页面中的招聘企业。')
ON DUPLICATE KEY UPDATE
  company_name = VALUES(company_name),
  industry = VALUES(industry),
  city = VALUES(city),
  contact_name = VALUES(contact_name),
  contact_phone = VALUES(contact_phone),
  description = VALUES(description);

INSERT INTO job_position(id, company_id, company_name, title, city, salary_range, skill_keyword, description, source_name, source_url, source_checked_at, status) VALUES
(1, 1, '云智迪', 'Java 后端开发实习生', '杭州', '200-300元/天', 'Java, 后端开发, 系统优化', '参与 Java 后端模块开发、代码质量保障及现有系统的稳定性优化。岗位事实依据公开招聘页概括整理。', 'BOSS直聘', 'https://www.zhipin.com/job_detail/a358b65018bf840503J53dy5GVVV.html', '2026-07-10', 'OPEN'),
(2, 2, '杭州十五分钟文化传媒', 'AI 应用开发工程师', '杭州西湖区', '300-350元/天', 'Python, AI 应用, 自动化', '参与 AI 视频内容生产自动化与相关应用开发。岗位事实依据公开招聘页概括整理。', 'BOSS直聘', 'https://m.zhipin.com/job_detail/1f2962417c43c8510nZ73NW1FVpV.html', '2026-07-10', 'OPEN'),
(3, 3, '幻量科技', '前端实习生', '上海浦东新区', '200-300元/天', 'Vue 3, 前端开发, 接口联调', '参与线上前端项目维护和功能迭代，使用 Vue 3 完成页面开发及接口联调。岗位事实依据公开招聘页概括整理。', 'BOSS直聘', 'https://m.zhipin.com/job_detail/6c7bc3f76e5d505d1HF409i1FVJZ.html', '2026-07-10', 'OPEN')
ON DUPLICATE KEY UPDATE
  company_id = VALUES(company_id),
  company_name = VALUES(company_name),
  title = VALUES(title),
  city = VALUES(city),
  salary_range = VALUES(salary_range),
  skill_keyword = VALUES(skill_keyword),
  description = VALUES(description),
  source_name = VALUES(source_name),
  source_url = VALUES(source_url),
  source_checked_at = VALUES(source_checked_at),
  status = VALUES(status);

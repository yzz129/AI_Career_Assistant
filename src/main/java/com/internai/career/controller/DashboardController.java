package com.internai.career.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.internai.career.common.Result;
import com.internai.career.entity.InternshipApply;
import com.internai.career.entity.JobPosition;
import com.internai.career.entity.KnowledgeDoc;
import com.internai.career.entity.Resume;
import com.internai.career.entity.Student;
import com.internai.career.entity.Teacher;
import com.internai.career.security.LoginUser;
import com.internai.career.security.LoginUserContext;
import com.internai.career.service.InternshipApplyService;
import com.internai.career.service.JobPositionService;
import com.internai.career.service.KnowledgeDocService;
import com.internai.career.service.ResumeService;
import com.internai.career.service.StudentService;
import com.internai.career.service.TeacherService;
import com.internai.career.vo.DashboardVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final JobPositionService jobPositionService;
    private final InternshipApplyService applyService;
    private final ResumeService resumeService;
    private final KnowledgeDocService knowledgeDocService;

    public DashboardController(StudentService studentService,
                               TeacherService teacherService,
                               JobPositionService jobPositionService,
                               InternshipApplyService applyService,
                               ResumeService resumeService,
                               KnowledgeDocService knowledgeDocService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.jobPositionService = jobPositionService;
        this.applyService = applyService;
        this.resumeService = resumeService;
        this.knowledgeDocService = knowledgeDocService;
    }

    @GetMapping("/overview")
    public Result<DashboardVO> overview() {
        LoginUser loginUser = LoginUserContext.get();
        Student student = findCurrentStudent(loginUser);
        Teacher teacher = findCurrentTeacher(loginUser);
        DashboardVO vo = new DashboardVO();

        List<JobPosition> openJobs = jobPositionService.list(new LambdaQueryWrapper<JobPosition>()
                .eq(JobPosition::getStatus, "OPEN")
                .orderByDesc(JobPosition::getCreateTime)
                .last("limit 12"));
        List<InternshipApply> applies = scopedApplies(loginUser, student, teacher);
        fillProfile(vo, loginUser, student, teacher, applies);
        Resume resume = findResume(student);
        List<KnowledgeDoc> docs = knowledgeDocService.list(new LambdaQueryWrapper<KnowledgeDoc>()
                .orderByDesc(KnowledgeDoc::getUpdateTime)
                .last("limit 6"));

        fillStats(vo, openJobs, applies, student);
        fillJobs(vo, openJobs, student);
        fillProcess(vo, applies);
        fillResume(vo, resume);
        fillKnowledge(vo, docs);
        fillAssistant(vo, openJobs, docs);
        return Result.ok(vo);
    }

    private Student findCurrentStudent(LoginUser loginUser) {
        if (!"STUDENT".equals(loginUser.roleCode())) {
            return null;
        }
        return studentService.getOne(new LambdaQueryWrapper<Student>().eq(Student::getUserId, loginUser.userId()), false);
    }

    private Teacher findCurrentTeacher(LoginUser loginUser) {
        if (!"TEACHER".equals(loginUser.roleCode())) {
            return null;
        }
        return teacherService.getOne(new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, loginUser.userId()), false);
    }

    private void fillProfile(DashboardVO vo,
                             LoginUser loginUser,
                             Student student,
                             Teacher teacher,
                             List<InternshipApply> applies) {
        String realName = loginUser.realName();
        String major = switch (loginUser.roleCode()) {
            case "STUDENT" -> student == null ? "学生用户" : safe(student.getMajor(), "学生用户");
            case "TEACHER" -> teacher == null ? "指导教师" : safe(teacher.getDepartment(), "指导教师");
            default -> "系统管理员";
        };
        DashboardVO.Profile profile = vo.getProfile();
        profile.setRealName(realName);
        profile.setMajor(major);
        profile.setGreeting("早上好，" + realName);
        profile.setSubtitle("AI 实习就业助手为你提供智能化的职业探索与求职支持");
        profile.setSeason("2026 秋招季");
        long notificationCount = "STUDENT".equals(loginUser.roleCode())
                ? applies.stream().filter(apply -> !"PENDING".equals(apply.getStatus())).count()
                : applies.stream().filter(apply -> "PENDING".equals(apply.getStatus())).count();
        profile.setNotificationCount(Math.toIntExact(notificationCount));
    }

    private List<InternshipApply> scopedApplies(LoginUser loginUser, Student student, Teacher teacher) {
        LambdaQueryWrapper<InternshipApply> wrapper = new LambdaQueryWrapper<InternshipApply>().orderByDesc(InternshipApply::getApplyTime);
        if ("STUDENT".equals(loginUser.roleCode()) && student != null) {
            wrapper.eq(InternshipApply::getStudentId, student.getId());
        }
        if ("TEACHER".equals(loginUser.roleCode()) && teacher != null) {
            wrapper.eq(InternshipApply::getTeacherId, teacher.getId());
        }
        return applyService.list(wrapper);
    }

    private Resume findResume(Student student) {
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<Resume>().orderByDesc(Resume::getUpdateTime).last("limit 1");
        if (student != null) {
            wrapper.eq(Resume::getStudentId, student.getId());
        }
        return resumeService.getOne(wrapper, false);
    }

    private void fillStats(DashboardVO vo,
                           List<JobPosition> openJobs,
                           List<InternshipApply> applies,
                           Student student) {
        long pendingCount = applies.stream().filter(apply -> "PENDING".equals(apply.getStatus())).count();
        long approvedCount = applies.stream().filter(apply -> "APPROVED".equals(apply.getStatus())).count();
        int bestMatch = openJobs.stream().mapToInt(job -> matchScore(job, student)).max().orElse(0);
        vo.getStats().add(stat("jobs", String.valueOf(openJobs.size()), "推荐岗位", "数据库开放岗位", "teal"));
        vo.getStats().add(stat("apply", String.valueOf(applies.size()), "我的申请", "待处理 <b class=\"red\">" + pendingCount + "</b>", "coral"));
        vo.getStats().add(stat("interview", String.valueOf(approvedCount), "已通过申请", approvedCount > 0 ? "已通过 " + approvedCount + " 份" : "暂无已通过申请", "yellow"));
        vo.getStats().add(stat("match", bestMatch + "%", "最高岗位匹配度", student == null ? "需关联学生档案" : "基于技能与意向城市", "blue"));
    }

    private DashboardVO.StatItem stat(String key, String value, String label, String sub, String color) {
        DashboardVO.StatItem item = new DashboardVO.StatItem();
        item.setKey(key);
        item.setValue(value);
        item.setLabel(label);
        item.setSub(sub);
        item.setColor(color);
        return item;
    }

    private void fillJobs(DashboardVO vo, List<JobPosition> openJobs, Student student) {
        for (int i = 0; i < Math.min(3, openJobs.size()); i++) {
            JobPosition job = openJobs.get(i);
            DashboardVO.JobItem item = new DashboardVO.JobItem();
            item.setId(job.getId());
            item.setCompany(safe(job.getCompanyName(), "未填写企业"));
            item.setTitle(safe(job.getTitle(), "未填写岗位"));
            item.setCity(safe(job.getCity(), "待定"));
            item.setSalaryRange(safe(job.getSalaryRange(), "薪资面议"));
            item.setTags(splitTags(job.getSkillKeyword()));
            item.setMatch(matchScore(job, student) + "%");
            item.setLogo(companyLogo(job.getCompanyName()));
            item.setLogoClass("logo-" + ((i % 3) + 1));
            item.setSourceName(safe(job.getSourceName(), "未标注来源"));
            item.setSourceUrl(job.getSourceUrl());
            item.setSourceCheckedAt(job.getSourceCheckedAt() == null ? "" : job.getSourceCheckedAt().toString());
            vo.getJobs().add(item);
        }
    }

    private List<String> splitTags(String value) {
        if (value == null || value.isBlank()) {
            return List.of("实习");
        }
        return Arrays.stream(value.split("[,，、\\s]+"))
                .filter(tag -> !tag.isBlank())
                .limit(3)
                .collect(Collectors.toList());
    }

    private int matchScore(JobPosition job, Student student) {
        if (student == null || student.getSkills() == null || job.getSkillKeyword() == null) {
            return 0;
        }
        List<String> jobTags = splitTags(job.getSkillKeyword());
        String skills = student.getSkills().toLowerCase(Locale.ROOT);
        long matched = jobTags.stream()
                .map(tag -> tag.toLowerCase(Locale.ROOT))
                .filter(skills::contains)
                .count();
        int skillScore = jobTags.isEmpty() ? 0 : (int) Math.round(matched * 80.0 / jobTags.size());
        String intentionCity = safe(student.getIntentionCity(), "").toLowerCase(Locale.ROOT);
        String jobCity = safe(job.getCity(), "").toLowerCase(Locale.ROOT);
        String cityPrefix = jobCity.length() >= 2 ? jobCity.substring(0, 2) : jobCity;
        int cityScore = !cityPrefix.isBlank() && intentionCity.contains(cityPrefix) ? 20 : 0;
        return Math.min(100, skillScore + cityScore);
    }

    private String companyLogo(String companyName) {
        if (companyName == null || companyName.isBlank()) {
            return "AI";
        }
        String first = companyName.trim().substring(0, 1);
        return first.matches("[a-zA-Z]") ? first.toUpperCase(Locale.ROOT) : first;
    }

    private void fillProcess(DashboardVO vo, List<InternshipApply> applies) {
        InternshipApply latest = applies.stream().findFirst().orElse(null);
        vo.getProcess().getSteps().add(step("投递简历", latest == null ? "未开始" : formatDate(latest.getApplyTime()), "submit", "teal"));
        vo.getProcess().getSteps().add(step("简历筛选", latest == null ? "待开始" : statusText(latest.getStatus()), "screen", "yellow"));
        vo.getProcess().getSteps().add(step("笔试/测评", latest != null && "APPROVED".equals(latest.getStatus()) ? "待完成" : "待开启", "test", "blue"));
        vo.getProcess().getSteps().add(step("面试", "待安排", "interview", "purple"));
        vo.getProcess().getSteps().add(step("Offer", "待获得", "offer", "gray"));

        DashboardVO.CurrentApply current = vo.getProcess().getCurrent();
        if (latest == null) {
            current.setCompanyName("暂无申请");
            current.setJobTitle("请先在岗位中心提交实习申请");
            current.setBadge("待开始");
            current.setProgressText("当前进度：未投递");
            current.setProgressPercent(0);
            current.setProgressNo("0/5");
            return;
        }
        current.setCompanyName(safe(latest.getCompanyName(), "未填写企业"));
        current.setJobTitle(safe(latest.getJobTitle(), "未填写岗位"));
        current.setBadge(statusText(latest.getStatus()));
        current.setProgressText("当前进度：" + currentStage(latest.getStatus()));
        int progress = "APPROVED".equals(latest.getStatus()) ? 60 : "REJECTED".equals(latest.getStatus()) ? 40 : 35;
        current.setProgressPercent(progress);
        current.setProgressNo(progress >= 60 ? "3/5" : "2/5");
    }

    private DashboardVO.ProcessStep step(String label, String time, String iconKey, String color) {
        DashboardVO.ProcessStep step = new DashboardVO.ProcessStep();
        step.setLabel(label);
        step.setTime(time);
        step.setIconKey(iconKey);
        step.setColor(color);
        return step;
    }

    private void fillResume(DashboardVO vo, Resume resume) {
        DashboardVO.ResumeInfo info = vo.getResume();
        int score = resumeScore(resume);
        info.setScore(score);
        info.setSummary(resume == null ? "数据库暂无简历，请先维护简历内容" : "基于数据库简历内容生成优化建议");
        String content = resume == null ? "" : safe(resume.getContent(), "");
        addSuggestion(info, content.contains("项目") ? "继续强化项目成果量化" : "补充项目经历与个人贡献", "高影响", "danger");
        addSuggestion(info, content.toLowerCase(Locale.ROOT).contains("spring") || content.contains("Vue") ? "提炼技术栈关键词" : "补充岗位相关技术栈", "中影响", "warning");
        addSuggestion(info, content.length() > 80 ? "优化自我介绍结构" : "扩展经历描述细节", "低影响", "success");
    }

    private void addSuggestion(DashboardVO.ResumeInfo info, String text, String level, String type) {
        DashboardVO.Suggestion suggestion = new DashboardVO.Suggestion();
        suggestion.setText(text);
        suggestion.setLevel(level);
        suggestion.setType(type);
        info.getSuggestions().add(suggestion);
    }

    private int resumeScore(Resume resume) {
        if (resume == null || resume.getContent() == null) {
            return 70;
        }
        int lengthScore = Math.min(15, resume.getContent().length() / 20);
        int keywordScore = resume.getContent().contains("项目") ? 6 : 0;
        int skillScore = resume.getContent().toLowerCase(Locale.ROOT).contains("spring") ? 5 : 0;
        return Math.min(96, 70 + lengthScore + keywordScore + skillScore);
    }

    private void fillKnowledge(DashboardVO vo, List<KnowledgeDoc> docs) {
        docs.stream()
                .map(KnowledgeDoc::getTitle)
                .filter(Objects::nonNull)
                .limit(3)
                .forEach(vo.getKnowledge().getQuestions()::add);
    }

    private void fillAssistant(DashboardVO vo, List<JobPosition> openJobs, List<KnowledgeDoc> docs) {
        addMessage(vo, "assistant", "你好呀！我已读取数据库中的岗位、简历与知识库资料，可以帮你做求职分析。", "09:30", false);
        String userQuestion = openJobs.isEmpty() ? "当前数据库里有哪些求职资料？" : "我想看看数据库里的实习岗位推荐。";
        addMessage(vo, "user", userQuestion, "09:31 ✓✓", false);
        String answer = openJobs.isEmpty()
                ? "当前暂无开放岗位，请管理员先维护岗位数据。"
                : "数据库中已找到 " + openJobs.size() + " 个开放岗位，我优先展示匹配度最高的前 3 个。";
        addMessage(vo, "assistant", answer, "09:31", true);
        vo.getAssistant().setRecommendButtonText("查看推荐岗位 (" + openJobs.size() + ")");
        docs.stream().map(KnowledgeDoc::getTitle).filter(Objects::nonNull).limit(3).forEach(vo.getAssistant().getQuickQuestions()::add);
    }

    private void addMessage(DashboardVO vo, String role, String content, String time, boolean showRecommendButton) {
        DashboardVO.ChatLine line = new DashboardVO.ChatLine();
        line.setRole(role);
        line.setContent(content);
        line.setTime(time);
        line.setShowRecommendButton(showRecommendButton);
        vo.getAssistant().getMessages().add(line);
    }

    private String statusText(String status) {
        if ("APPROVED".equals(status)) {
            return "已通过";
        }
        if ("REJECTED".equals(status)) {
            return "已驳回";
        }
        return "进行中";
    }

    private String currentStage(String status) {
        if ("APPROVED".equals(status)) {
            return "笔试/测评";
        }
        if ("REJECTED".equals(status)) {
            return "重新修改申请";
        }
        return "简历筛选";
    }

    private String formatDate(LocalDateTime time) {
        if (time == null) {
            return "已提交";
        }
        return time.format(DateTimeFormatter.ofPattern("MM-dd"));
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}

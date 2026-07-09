package com.internai.career.ai;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.internai.career.entity.*;
import com.internai.career.security.LoginUserContext;
import com.internai.career.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiService {
    private final boolean mock;
    private final OpenAiCompatibleClient client;
    private final ResumeService resumeService;
    private final StudentService studentService;
    private final JobPositionService jobPositionService;
    private final InternshipLogService logService;
    private final KnowledgeDocService knowledgeDocService;
    private final AiChatSessionService sessionService;
    private final AiChatMessageService messageService;

    public AiService(@Value("${ai.mock:true}") boolean mock,
                     OpenAiCompatibleClient client,
                     ResumeService resumeService,
                     StudentService studentService,
                     JobPositionService jobPositionService,
                     InternshipLogService logService,
                     KnowledgeDocService knowledgeDocService,
                     AiChatSessionService sessionService,
                     AiChatMessageService messageService) {
        this.mock = mock;
        this.client = client;
        this.resumeService = resumeService;
        this.studentService = studentService;
        this.jobPositionService = jobPositionService;
        this.logService = logService;
        this.knowledgeDocService = knowledgeDocService;
        this.sessionService = sessionService;
        this.messageService = messageService;
    }

    public Flux<String> resumeOptimize(Long resumeId) {
        Resume resume = resumeService.getById(resumeId);
        String prompt = PromptTemplates.resumeOptimize(resume == null ? "暂无简历内容" : resume.getContent());
        return chat("RESUME_OPTIMIZE", "简历优化", prompt);
    }

    public Flux<String> jobRecommend(Long studentId) {
        Student student = studentService.getById(studentId);
        List<JobPosition> jobs = jobPositionService.list(new LambdaQueryWrapper<JobPosition>().eq(JobPosition::getStatus, "OPEN").last("limit 8"));
        String jobText = jobs.stream().map(j -> j.getTitle() + " / " + j.getCompanyName() + " / " + j.getCity() + " / " + j.getSkillKeyword()).collect(Collectors.joining("\n"));
        String profile = student == null ? "暂无学生档案" : student.getName() + "，专业：" + student.getMajor() + "，技能：" + student.getSkills() + "，意向城市：" + student.getIntentionCity();
        return chat("JOB_RECOMMEND", "岗位推荐", PromptTemplates.jobRecommend(profile, jobText));
    }

    public Flux<String> interview(Long jobId, String answer) {
        JobPosition job = jobId == null ? null : jobPositionService.getById(jobId);
        String prompt = "岗位：" + (job == null ? "通用实习岗位" : job.getTitle()) + "\n" + PromptTemplates.interview(answer);
        return chat("INTERVIEW", "模拟面试", prompt);
    }

    public Flux<String> logSummary(Long studentId) {
        List<InternshipLog> logs = logService.list(new LambdaQueryWrapper<InternshipLog>().eq(InternshipLog::getStudentId, studentId).last("limit 10"));
        String text = logs.stream().map(InternshipLog::getContent).collect(Collectors.joining("\n"));
        return chat("LOG_SUMMARY", "实习日志总结", PromptTemplates.logSummary(text));
    }

    public Flux<String> kbChat(String question) {
        List<KnowledgeDoc> docs = knowledgeDocService.list(new LambdaQueryWrapper<KnowledgeDoc>()
                .like(KnowledgeDoc::getTitle, question)
                .or().like(KnowledgeDoc::getKeywords, question)
                .or().like(KnowledgeDoc::getContent, question)
                .last("limit 5"));
        String text = docs.stream().map(d -> "《" + d.getTitle() + "》" + d.getContent()).collect(Collectors.joining("\n"));
        return chat("KB_CHAT", "知识库问答", PromptTemplates.knowledgeChat(question, text));
    }

    private Flux<String> chat(String type, String title, String prompt) {
        AiChatSession session = new AiChatSession();
        session.setUserId(LoginUserContext.get().userId());
        session.setType(type);
        session.setTitle(title);
        session.setCreateTime(LocalDateTime.now());
        sessionService.save(session);
        saveMessage(session.getId(), "user", prompt);

        Flux<String> stream = mock ? mockStream(type) : client.stream(prompt);
        StringBuilder answer = new StringBuilder();
        return stream.doOnNext(answer::append)
                .doOnComplete(() -> saveMessage(session.getId(), "assistant", answer.toString()));
    }

    private void saveMessage(Long sessionId, String role, String content) {
        AiChatMessage message = new AiChatMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        messageService.save(message);
    }

    private Flux<String> mockStream(String type) {
        String text = switch (type) {
            case "RESUME_OPTIMIZE" -> "我先看整体结构：建议把项目经历改成“任务-行动-结果”的三段式；技能部分按 Java、Vue、数据库、AI 工具分类；每段经历补充可量化成果，但不要添加未发生的内容。";
            case "JOB_RECOMMEND" -> "推荐优先投递：Java 后端实习、AI 应用开发实习、前端工程实习。你的准备重点是补一个可展示项目链接，复习 MyBatis-Plus、JWT、SSE 和 Vue 状态管理。";
            case "INTERVIEW" -> "你的回答有清晰方向。下一版可以先给结论，再讲一个具体项目例子，最后补充遇到问题时如何排查。注意少用泛泛而谈的形容词。";
            case "LOG_SUMMARY" -> "本周实习成长主要体现在需求拆解、接口联调和问题记录。下周建议把每天的问题沉淀成 checklist，并主动向指导老师同步阻塞点。";
            default -> "根据知识库资料，建议先确认实习申请截止时间、岗位技能要求和校内审批流程；如资料未覆盖具体公司政策，请咨询就业指导老师。";
        };
        return Flux.fromArray(text.split("")).delayElements(Duration.ofMillis(22));
    }
}

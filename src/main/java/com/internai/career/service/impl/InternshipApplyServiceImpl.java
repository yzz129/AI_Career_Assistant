package com.internai.career.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.internai.career.common.BizException;
import com.internai.career.dto.ApplyReviewRequest;
import com.internai.career.entity.InternshipApply;
import com.internai.career.entity.JobPosition;
import com.internai.career.entity.Student;
import com.internai.career.mapper.InternshipApplyMapper;
import com.internai.career.security.RoleGuard;
import com.internai.career.service.InternshipApplyService;
import com.internai.career.service.JobPositionService;
import com.internai.career.service.StudentService;
import com.internai.career.service.TeacherService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class InternshipApplyServiceImpl extends ServiceImpl<InternshipApplyMapper, InternshipApply> implements InternshipApplyService {
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final JobPositionService jobPositionService;
    private final RoleGuard roleGuard;

    public InternshipApplyServiceImpl(StudentService studentService, TeacherService teacherService, JobPositionService jobPositionService, RoleGuard roleGuard) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.jobPositionService = jobPositionService;
        this.roleGuard = roleGuard;
    }

    @Override
    public InternshipApply submit(Long jobId, String reason) {
        roleGuard.require("STUDENT");
        Student student = studentService.currentStudent();
        JobPosition job = jobPositionService.getById(jobId);
        if (job == null) {
            throw new BizException("岗位不存在");
        }
        InternshipApply apply = new InternshipApply();
        apply.setStudentId(student.getId());
        apply.setTeacherId(student.getTeacherId());
        apply.setJobId(job.getId());
        apply.setJobTitle(job.getTitle());
        apply.setCompanyName(job.getCompanyName());
        apply.setReason(reason);
        apply.setStatus("PENDING");
        apply.setApplyTime(LocalDateTime.now());
        save(apply);
        return apply;
    }

    @Override
    public InternshipApply review(ApplyReviewRequest request) {
        roleGuard.require("TEACHER", "ADMIN");
        if (!Set.of("PENDING", "APPROVED", "REJECTED").contains(request.getStatus())) {
            throw new BizException("审核状态必须是 PENDING、APPROVED 或 REJECTED");
        }
        InternshipApply apply = getById(request.getApplyId());
        if (apply == null) {
            throw new BizException("申请记录不存在");
        }
        if ("TEACHER".equals(com.internai.career.security.LoginUserContext.get().roleCode())) {
            Long currentTeacherId = teacherService.currentTeacher().getId();
            if (!currentTeacherId.equals(apply.getTeacherId())) {
                throw new BizException("只能审核自己负责学生的申请");
            }
        }
        apply.setStatus(request.getStatus());
        apply.setReviewComment(request.getReviewComment());
        apply.setReviewTime(LocalDateTime.now());
        updateById(apply);
        return apply;
    }
}

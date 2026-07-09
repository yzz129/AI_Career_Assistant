package com.internai.career.ai;

public class PromptTemplates {
    private PromptTemplates() {
    }

    public static String resumeOptimize(String resumeContent) {
        return "你是高校就业指导老师，请只基于学生已提供的简历内容提出优化建议，不编造经历。简历内容：\n" + resumeContent;
    }

    public static String jobRecommend(String studentProfile, String jobs) {
        return "请根据学生专业、技能和意向城市，从已有岗位中推荐匹配岗位，说明匹配理由和准备建议。\n学生信息："
                + studentProfile + "\n岗位列表：\n" + jobs;
    }

    public static String interview(String answer) {
        return "你是模拟面试官，请对学生回答进行鼓励式点评，指出亮点、风险和下一版回答结构。学生回答：\n" + answer;
    }

    public static String logSummary(String logs) {
        return "请总结学生实习日志中的成长、问题和下周改进计划，不夸大事实。日志：\n" + logs;
    }

    public static String knowledgeChat(String question, String docs) {
        return "请只依据知识库资料回答学生问题；资料不足时说明需要咨询老师或就业办。\n资料：\n" + docs + "\n问题：" + question;
    }
}

package com.internai.career.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DashboardVO {
    private Profile profile = new Profile();
    private List<StatItem> stats = new ArrayList<>();
    private List<JobItem> jobs = new ArrayList<>();
    private ProcessInfo process = new ProcessInfo();
    private ResumeInfo resume = new ResumeInfo();
    private KnowledgeInfo knowledge = new KnowledgeInfo();
    private AssistantInfo assistant = new AssistantInfo();

    @Data
    public static class Profile {
        private String greeting;
        private String subtitle;
        private String realName;
        private String major;
        private String season;
        private Integer notificationCount;
        private String avatarText;
    }

    @Data
    public static class StatItem {
        private String key;
        private String value;
        private String label;
        private String sub;
        private String color;
    }

    @Data
    public static class JobItem {
        private Long id;
        private String company;
        private String title;
        private String city;
        private List<String> tags = new ArrayList<>();
        private String match;
        private String logo;
        private String logoClass;
    }

    @Data
    public static class ProcessInfo {
        private List<ProcessStep> steps = new ArrayList<>();
        private CurrentApply current = new CurrentApply();
    }

    @Data
    public static class ProcessStep {
        private String label;
        private String time;
        private String iconKey;
        private String color;
    }

    @Data
    public static class CurrentApply {
        private String companyName;
        private String jobTitle;
        private String badge;
        private String progressText;
        private Integer progressPercent;
        private String progressNo;
    }

    @Data
    public static class ResumeInfo {
        private Integer score;
        private String summary;
        private List<Suggestion> suggestions = new ArrayList<>();
    }

    @Data
    public static class Suggestion {
        private String text;
        private String level;
        private String type;
    }

    @Data
    public static class KnowledgeInfo {
        private List<String> questions = new ArrayList<>();
    }

    @Data
    public static class AssistantInfo {
        private List<ChatLine> messages = new ArrayList<>();
        private List<String> quickQuestions = new ArrayList<>();
        private String recommendButtonText;
    }

    @Data
    public static class ChatLine {
        private String role;
        private String content;
        private String time;
        private Boolean showRecommendButton;
    }
}

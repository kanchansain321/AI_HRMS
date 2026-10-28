package com.aihrms.aihrmsbackend.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Per-task Gemini model routing.
 * <pre>
 * gemini.models.primary  — quality (chat / summaries)
 * gemini.models.fast     — low-latency JSON work
 * gemini.models.skill / questions / rank / chat / summary — optional overrides
 * gemini.ranking.use-ai  — false = local skill-overlap ranking (fast)
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "gemini")
public class GeminiModelProperties {

    private String apiKey = "";
    private final Models models = new Models();
    private final Ranking ranking = new Ranking();
    private final Skill skill = new Skill();
    private final Questions questions = new Questions();

    public String getApiKey() {
        if (apiKey != null && !apiKey.isBlank()) {
            return apiKey.trim();
        }
        String env = System.getenv("GOOGLE_API_KEY");
        return env != null ? env.trim() : "";
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Models getModels() {
        return models;
    }

    public Ranking getRanking() {
        return ranking;
    }

    public Skill getSkill() {
        return skill;
    }

    public Questions getQuestions() {
        return questions;
    }

    public String primaryModel() {
        return models.primary;
    }

    public String fastModel() {
        return firstNonBlank(models.fast, models.primary);
    }

    public String skillModel() {
        return firstNonBlank(models.skill, fastModel());
    }

    public String questionsModel() {
        return firstNonBlank(models.questions, fastModel());
    }

    public String rankModel() {
        return firstNonBlank(models.rank, fastModel());
    }

    public String chatModel() {
        return firstNonBlank(models.chat, models.primary);
    }

    public String summaryModel() {
        return firstNonBlank(models.summary, models.primary);
    }

    public boolean isRankingUseAi() {
        return ranking.useAi;
    }

    public int getSkillMaxChars() {
        return skill.maxChars;
    }

    public int getQuestionsMax() {
        return questions.max;
    }

    private static String firstNonBlank(String value, String fallback) {
        return value != null && !value.isBlank() ? value.trim() : fallback;
    }

    public static class Models {
        private String primary = "gemini-3.8-flash";
        private String fast = "gemini-3.5-flash-lite";
        private String skill;
        private String questions;
        private String rank;
        private String chat;
        private String summary;

        public String getPrimary() {
            return primary;
        }

        public void setPrimary(String primary) {
            this.primary = primary;
        }

        public String getFast() {
            return fast;
        }

        public void setFast(String fast) {
            this.fast = fast;
        }

        public String getSkill() {
            return skill;
        }

        public void setSkill(String skill) {
            this.skill = skill;
        }

        public String getQuestions() {
            return questions;
        }

        public void setQuestions(String questions) {
            this.questions = questions;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getChat() {
            return chat;
        }

        public void setChat(String chat) {
            this.chat = chat;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
    }

    public static class Ranking {
        /** false = local overlap scoring (recommended for speed). */
        private boolean useAi = false;

        public boolean isUseAi() {
            return useAi;
        }

        public void setUseAi(boolean useAi) {
            this.useAi = useAi;
        }
    }

    public static class Skill {
        private int maxChars = 12000;

        public int getMaxChars() {
            return maxChars;
        }

        public void setMaxChars(int maxChars) {
            this.maxChars = maxChars;
        }
    }

    public static class Questions {
        private int max = 8;

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }
}
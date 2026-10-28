package com.aihrms.aihrmsbackend.ai.service;

/** Which Gemini model/config profile to use for a call. */
public enum GeminiTask {
    /** Balanced / quality tasks (chat, summaries). */
    DEFAULT,
    /** Generic low-latency JSON extraction. */
    FAST,
    /** Resume skill screening. */
    SKILL,
    /** Interview question generation. */
    QUESTIONS,
    /** Per-candidate ranking (when AI ranking is enabled). */
    RANK,
    /** HR chatbot. */
    CHAT,
    /** Performance summary prose. */
    SUMMARY
}
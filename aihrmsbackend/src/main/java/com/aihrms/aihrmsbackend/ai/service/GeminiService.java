package com.aihrms.aihrmsbackend.ai.service;

import com.aihrms.aihrmsbackend.ai.config.GeminiModelProperties;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.genai.types.ThinkingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private final GeminiModelProperties props;
    private volatile Client sharedClient;

    public GeminiService(GeminiModelProperties props) {
        this.props = props;
    }

    private Client client() {
        String apiKey = props.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "gemini.api.key is empty. Set GOOGLE_API_KEY (or gemini.api.key) before calling Gemini."
            );
        }
        Client existing = sharedClient;
        if (existing != null) {
            return existing;
        }
        synchronized (this) {
            if (sharedClient == null) {
                sharedClient = Client.builder().apiKey(apiKey).build();
            }
            return sharedClient;
        }
    }

    public String ask(GeminiTask task, String text) {
        String modelId = modelFor(task);
        GenerateContentConfig config = configFor(task);
        try {
            return generateText(modelId, text, config);
        } catch (RuntimeException first) {
            if (isJsonTask(task)) {
                try {
                    log.warn("Gemini {} config failed ({}), retrying without thinkingConfig",
                            task, first.getMessage());
                    return generateText(modelId, text, jsonConfigNoThinking(maxTokensFor(task)));
                } catch (RuntimeException jsonRetryFail) {
                    first = jsonRetryFail;
                }
            }
            if (isTransientModelFailure(first)) {
                String fallback = alternateModel(modelId);
                if (fallback != null) {
                    log.warn("Gemini {} model {} unavailable ({}), retrying with {}",
                            task, modelId, first.getMessage(), fallback);
                    return generateText(fallback, text, config);
                }
            }
            throw first;
        }
    }

    /** Prefer the other of primary/fast when the requested model is overloaded or missing. */
    private String alternateModel(String modelId) {
        String primary = props.primaryModel();
        String fast = props.fastModel();
        if (modelId != null && modelId.equals(primary) && fast != null && !fast.equals(primary)) {
            return fast;
        }
        if (modelId != null && modelId.equals(fast) && primary != null && !primary.equals(fast)) {
            return primary;
        }
        if (fast != null && !fast.equals(modelId)) {
            return fast;
        }
        if (primary != null && !primary.equals(modelId)) {
            return primary;
        }
        return null;
    }

    public String askToGemini(String text) {
        return ask(GeminiTask.DEFAULT, text);
    }

    public String askToGeminiFast(String text) {
        return ask(GeminiTask.FAST, text);
    }

    public String askToGemini(String text, MultipartFile file, String type) {
        try {
            if (file == null || file.isEmpty()) {
                return ask(GeminiTask.DEFAULT, text);
            }
            String mime = resolveMime(file.getContentType(), file.getOriginalFilename(), type);
            return generateMultimodal(props.primaryModel(), text, file.getBytes(), mime, proseConfig(2048));
        } catch (Exception e) {
            throw new IllegalStateException("Gemini multimodal call failed: " + e.getMessage(), e);
        }
    }

    public String askWithFilePath(String text, String filePath, String mimeType) {
        try {
            if (filePath == null || filePath.isBlank()) {
                return ask(GeminiTask.SKILL, text);
            }
            byte[] bytes = Files.readAllBytes(Path.of(filePath));
            return generateMultimodal(
                    props.skillModel(),
                    text,
                    bytes,
                    mimeType != null ? mimeType : "application/octet-stream",
                    jsonConfig(512));
        } catch (Exception e) {
            throw new IllegalStateException("Gemini file prompt failed: " + e.getMessage(), e);
        }
    }

    public void askToGeminiStream(String text, MultipartFile file, String type, Consumer<String> lineConsumer) {
        try {
            String result = askToGemini(text, file, type);
            for (String line : result.split("\\r?\\n")) {
                lineConsumer.accept(line);
            }
        } catch (Exception e) {
            lineConsumer.accept("Error: " + e.getMessage());
        }
    }

    public boolean isConfigured() {
        String apiKey = props.getApiKey();
        return apiKey != null && !apiKey.isBlank();
    }

    public GeminiModelProperties props() {
        return props;
    }

    private String modelFor(GeminiTask task) {
        return switch (task) {
            case SKILL -> props.skillModel();
            case QUESTIONS -> props.questionsModel();
            case RANK -> props.rankModel();
            case CHAT -> props.chatModel();
            case SUMMARY -> props.summaryModel();
            case FAST -> props.fastModel();
            case DEFAULT -> props.primaryModel();
        };
    }

    private GenerateContentConfig configFor(GeminiTask task) {
        return switch (task) {
            case SKILL, FAST, RANK -> jsonConfig(maxTokensFor(task));
            case QUESTIONS -> jsonConfig(maxTokensFor(task));
            case CHAT, SUMMARY, DEFAULT -> proseConfig(maxTokensFor(task));
        };
    }

    private boolean isJsonTask(GeminiTask task) {
        return task == GeminiTask.SKILL
                || task == GeminiTask.FAST
                || task == GeminiTask.RANK
                || task == GeminiTask.QUESTIONS;
    }

    private static boolean isTransientModelFailure(Throwable t) {
        String msg = t != null && t.getMessage() != null ? t.getMessage().toLowerCase() : "";
        return msg.contains("503")
                || msg.contains("high demand")
                || msg.contains("unavailable")
                || msg.contains("resource_exhausted")
                || msg.contains("404")
                || msg.contains("not found");
    }

    private int maxTokensFor(GeminiTask task) {
        return switch (task) {
            case SKILL, FAST, RANK -> 512;
            case QUESTIONS -> 1024;
            case CHAT, SUMMARY, DEFAULT -> 2048;
        };
    }

    private String generateText(String modelId, String text, GenerateContentConfig config) {
        long start = System.currentTimeMillis();
        GenerateContentResponse response = client().models.generateContent(modelId, text, config);
        String result = response.text();
        log.info("Gemini model={} textCall ms={} outChars={}",
                modelId, System.currentTimeMillis() - start, result != null ? result.length() : 0);
        return result != null ? result : "";
    }

    private String generateMultimodal(
            String modelId, String text, byte[] bytes, String mimeType, GenerateContentConfig config) {
        long start = System.currentTimeMillis();
        Part textPart = Part.fromText(text != null ? text : "");
        Part filePart = Part.fromBytes(bytes, mimeType);
        Content content = Content.fromParts(textPart, filePart);
        GenerateContentResponse response = client().models.generateContent(modelId, content, config);
        String result = response.text();
        log.info("Gemini model={} multimodalCall ms={} outChars={}",
                modelId, System.currentTimeMillis() - start, result != null ? result.length() : 0);
        return result != null ? result : "";
    }

    private GenerateContentConfig proseConfig(int maxOutputTokens) {
        return GenerateContentConfig.builder()
                .temperature(0.2f)
                .maxOutputTokens(maxOutputTokens)
                .build();
    }

    private GenerateContentConfig jsonConfig(int maxOutputTokens) {
        return GenerateContentConfig.builder()
                .temperature(0.1f)
                .maxOutputTokens(maxOutputTokens)
                .responseMimeType("application/json")
                .thinkingConfig(ThinkingConfig.builder().thinkingBudget(0).includeThoughts(false).build())
                .build();
    }

    private GenerateContentConfig jsonConfigNoThinking(int maxOutputTokens) {
        return GenerateContentConfig.builder()
                .temperature(0.1f)
                .maxOutputTokens(maxOutputTokens)
                .responseMimeType("application/json")
                .build();
    }

    private String resolveMime(String contentType, String filename, String inputType) {
        if (contentType != null && !contentType.isBlank() && !"application/octet-stream".equals(contentType)) {
            return contentType;
        }
        if (filename != null) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".pdf")) {
                return "application/pdf";
            }
            if (lower.endsWith(".png")) {
                return "image/png";
            }
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                return "image/jpeg";
            }
        }
        if (inputType != null) {
            return switch (inputType.toLowerCase()) {
                case "image" -> "image/png";
                case "pdf", "document" -> "application/pdf";
                case "audio" -> "audio/mpeg";
                case "video" -> "video/mp4";
                default -> "text/plain";
            };
        }
        return "application/octet-stream";
    }
}
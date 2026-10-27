package com.aihrms.aihrmsbackend.ai.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.function.Consumer;

/**
 * Gemini cloud agent wrapper — patterned after geminiDummy's GeminiService
 * using {@code com.google.genai.Client}.
 */
@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private Client client() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "gemini.api.key is empty. Set GOOGLE_API_KEY (or gemini.api.key) before calling Gemini."
            );
        }
        return Client.builder().apiKey(apiKey).build();
    }

    /** Non-streaming text prompt (used by skill extraction / question generation). */
    public String askToGemini(String text) {
        Client client = client();
        GenerateContentResponse response = client.models.generateContent(model, text, null);
        String result = response.text();
        log.debug("Gemini response length={}", result != null ? result.length() : 0);
        return result != null ? result : "";
    }

    /** Text + optional media, matching geminiDummy signature. */
    public String askToGemini(String text, MultipartFile file, String type) {
        if (file != null && !file.isEmpty()) {
            // ponytail: SDK multimodal path not wired yet — append note; text prompt still runs.
            log.info("Media input type={} name={} size={} (text prompt only for now)",
                    type, file.getOriginalFilename(), file.getSize());
        }
        return askToGemini(text);
    }

    /** SSE-style line streaming, same idea as geminiDummy. */
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
        return apiKey != null && !apiKey.isBlank();
    }

    @SuppressWarnings("unused")
    private String encodeFileToBase64(MultipartFile file) throws Exception {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }
}

package org.marta.vocabtracker.translation.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final WebClient.Builder webClientBuilder;

    @Value("${translator.api-url}")
    private String apiUrl;

    @Value("${translator.source-lang}")
    private String sourceLang;

    @Value("${translator.target-lang}")
    private String targetLang;

    public List<String> translate(String text) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(apiUrl).build();

            Map<String, Object> requestBody = Map.of(
                    "q", text,
                    "source", sourceLang,
                    "target", targetLang
            );

            Map<String, Object> response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("translatedText")) {
                String translation = (String) response.get("translatedText");
                return List.of(translation);
            }
            return List.of(text);


        } catch (Exception e) {
            System.err.println("Translation API error: " + e.getMessage());
            return List.of(text);
        }
    }
}
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

            Map response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("q", text)
                            .queryParam("langpair", sourceLang + "|" + targetLang)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            System.out.println("API RESPONSE: " + response);

            if (response != null && response.containsKey("responseData")) {
                Map responseData = (Map) response.get("responseData");
                String translation = (String) responseData.get("translatedText");


                return List.of(translation);
            }
            return List.of(text);


        } catch (Exception e) {
            System.err.println("Translation API error: " + e.getMessage());
            return List.of(text);
        }
    }
}
package org.marta.vocabtracker.word.service;

import lombok.RequiredArgsConstructor;
import org.marta.vocabtracker.word.dto.WordDTO;
import org.marta.vocabtracker.word.model.Status;
import org.marta.vocabtracker.word.model.WordEntity;
import org.marta.vocabtracker.word.repository.WordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final Random random = new Random();

    @Transactional
    public WordEntity addWord(String inputWord, List<String> translationsFromAPI) {
        if (inputWord == null || inputWord.isBlank()) {
            throw new IllegalArgumentException("Word cannot be empty");
        }
        if (translationsFromAPI == null || translationsFromAPI.isEmpty()) {
            throw new IllegalArgumentException("No translations found for: " + inputWord);
        }

        String normalized = inputWord.trim().toLowerCase(Locale.ROOT);

        Optional<WordEntity> existing = wordRepository.findByOriginal(normalized);
        if (existing.isPresent()) {
            WordEntity word = existing.get();
            word.setStatus(Status.NEW);
            return wordRepository.save(word);
        }

        WordEntity word = WordEntity.builder()
                .original(normalized)
                .translations(translationsFromAPI.stream()
                        .map(String::trim)
                        .filter(t -> !t.isBlank())
                        .distinct()
                        .collect(Collectors.toList()))
                .status(Status.NEW)
                .createdAt(LocalDateTime.now())
                .repetition(10)
                .build();

        return wordRepository.save(word);
    }

    public int getStatusPriority(Status status) {
        return switch (status) {
            case NEW -> 1;
            case REPEAT -> 2;
            case KNOWN -> 3;
            case MASTERED -> 4;
        };
    }

    public List<WordDTO> getAllWords() {
        return wordRepository.findAll().stream()
                .map(word -> new WordDTO(
                        word.getOriginal(),
                        word.getTranslations(),
                        word.getStatus()))
                .toList();
    }

    public List<WordDTO> getDailyRepetition(int count) {
        List<WordDTO> allWords = new ArrayList<>(getAllWords());

        allWords.sort(Comparator.comparingInt(wordDto ->
                getStatusPriority(wordDto.getStatus())));

        int reviewPoolSize = Math.min(allWords.size(), count * 3);
        List<WordDTO> priorityWords = allWords.stream()
                .limit(reviewPoolSize)
                .toList();

        List<WordDTO> shuffled = new ArrayList<>(priorityWords);
        Collections.shuffle(shuffled, random);

        return shuffled.stream()
                .limit(count)
                .toList();
    }

    @Transactional
    public void clearAll() {
        wordRepository.deleteAll();
    }

    @Transactional
    public void updateStatus(String original, Status newStatus) {
        String normalized = original.trim().toLowerCase(Locale.ROOT);
        WordEntity word = wordRepository.findByOriginal(normalized)
                .orElseThrow(() -> new NoSuchElementException("Word not found: " + original));
        word.setStatus(newStatus);
        word.setRepetition(10);
        wordRepository.save(word);
    }

    @Transactional
    public void removeWord(String original) {
        String normalized = original.trim().toLowerCase(Locale.ROOT);
        if (!wordRepository.existsByOriginal(normalized)) {
            throw new NoSuchElementException("Word not found: " + original);
        }
        wordRepository.deleteByOriginal(normalized);
    }
}
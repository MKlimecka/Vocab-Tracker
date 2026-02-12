package org.marta.vocabtracker.service;

import org.marta.vocabtracker.dto.WordDTO;
import org.marta.vocabtracker.model.Status;
import org.marta.vocabtracker.model.Word;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class VocabularyService {

    private final Map<String, Word> words = new HashMap<>();
    private final Random random = new Random();


    public Word addWord(String inputWord, List<String> translationsFromAPI) {
        if (inputWord == null || inputWord.isBlank()) {
            throw new IllegalArgumentException("Word cannot be empty");
        }
        if (translationsFromAPI == null || translationsFromAPI.isEmpty()) {
            throw new NoSuchElementException(" Word not found");
        }
        String normalized = inputWord.trim().toLowerCase(Locale.ROOT);

        if (words.containsKey(normalized)) {
            Word existing = words.get(normalized);
            existing.setStatus(Status.NEW);
            return existing;
        }
        Word word = Word.builder()
                .id(UUID.randomUUID())
                .original(normalized)
                .translations(new ArrayList<>(translationsFromAPI.stream()
                        .map(String :: trim)
                        .filter(t -> ! t.isBlank())
                        .distinct()
                        .collect(Collectors.toList())))
                .status(Status.NEW)
                .build();

            words.put(normalized, word);
            return word;
    }

    public int getStatusPriority(Status status) {
        return  switch (status) {
            case NEW -> 1;
            case REPEAT -> 2;
            case KNOWN -> 3;
            case MASTERED -> 4;
        };
    }

    public List <WordDTO> getAllWords() {
        return words.values().stream().map(word ->
                new WordDTO(word.getOriginal(), word.getTranslations(),
                        word.getStatus())).toList();
    }

    public List<WordDTO> getDailyReview(int count) {
        List<WordDTO> allWords = new ArrayList<>(getAllWords());
        allWords.sort(Comparator.comparingInt(wordDto ->
                getStatusPriority(wordDto.getStatus())));
        Collections.shuffle(allWords, random);
        return allWords.stream()
                        .limit(count)
                        .toList();
    }

    public void clearAll() {
        words.clear();
    }

    public void updateStatus (Word word, Status newStatus) {
        word.setStatus(newStatus);
    }

    public void removeWord (Word word) {
        words.remove(word.getOriginal());
    }
}

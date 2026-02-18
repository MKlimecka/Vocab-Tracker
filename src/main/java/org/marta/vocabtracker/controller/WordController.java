package org.marta.vocabtracker.controller;

import lombok.RequiredArgsConstructor;
import org.marta.vocabtracker.dto.WordDTO;
import org.marta.vocabtracker.model.Status;
import org.marta.vocabtracker.model.WordEntity;
import org.marta.vocabtracker.service.WordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @PostMapping
    public ResponseEntity<WordEntity> addWord(
            @RequestParam String word,
            @RequestParam List<String> translations) {
        WordEntity created = wordService.addWord(word, translations);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/repetition")
    public ResponseEntity<List<WordDTO>> getDailyReview(
            @RequestParam(defaultValue = "5") int count) {
        return ResponseEntity.ok(wordService.getDailyRepetition(count));
    }

    @PatchMapping("/{original}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable String original,
            @RequestParam Status status) {
        wordService.updateStatus(original, status);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{original}")
    public ResponseEntity<Void> deleteWord(@PathVariable String original) {
        wordService.removeWord(original);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearAll() {
        wordService.clearAll();
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WordDTO>> getAllWords() {
        return ResponseEntity.ok(wordService.getAllWords());
    }
}

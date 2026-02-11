package org.marta.vocabtracker.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Word {
    private Long id;
    private String original;
    private List<String> translations;
    private Status status;


    public Word(Long id, String original, List<String> translation, Status status) {
        this.id = id;
        this.original = original;
        this.translations = new ArrayList<>();
        this.status = status;
    }

    public void addTranslation(String translation) {
        if (!translations.contains(translation)) {
            translations.add(translation);
        }
    }
}

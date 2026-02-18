package org.marta.vocabtracker.word.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.marta.vocabtracker.word.model.Status;

import java.util.List;
@AllArgsConstructor
@Getter
@Setter
public class WordDTO {
    private String original;
    private List<String> translations;
    private Status status;
}

package org.marta.vocabtracker.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Word {
    private UUID id;
    private String original;
    private List<String> translations;
    private Status status;
    private LocalDateTime createdAt;



}

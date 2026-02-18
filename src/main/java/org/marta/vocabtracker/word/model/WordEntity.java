package org.marta.vocabtracker.word.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.marta.vocabtracker.user.model.UserEntity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "words")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String original;

    @ElementCollection
    @CollectionTable(name = "word_translations", joinColumns = @JoinColumn(name = "word_id"))
    @Column(name = "translation")
    private List<String> translations;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private int repetition;

    @ManyToMany(mappedBy = "words")
    private Set<UserEntity> users = new HashSet<>();
}
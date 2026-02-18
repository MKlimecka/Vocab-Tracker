package org.marta.vocabtracker.user.model;

import jakarta.persistence.*;
import org.marta.vocabtracker.word.model.WordEntity;

import java.util.*;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany
    @JoinTable(name = "user_words", joinColumns = @JoinColumn(name = "word_id"),
    inverseJoinColumns = @JoinColumn(name = "word_id"))
    private Set<WordEntity> words = new HashSet<>();

}

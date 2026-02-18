package org.marta.vocabtracker.word.repository;

import org.marta.vocabtracker.word.model.Status;
import org.marta.vocabtracker.word.model.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WordRepository extends JpaRepository<WordEntity, UUID>  {

    Optional<WordEntity> findByOriginal(String original);

    boolean existsByOriginal(String original);

    void deleteByOriginal(String original);

    List<WordEntity> findByStatus(Status status);
}

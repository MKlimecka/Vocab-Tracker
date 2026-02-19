package org.marta.vocabtracker.word.repository;

import org.marta.vocabtracker.word.model.Status;
import org.marta.vocabtracker.word.model.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WordRepository extends JpaRepository<WordEntity, UUID>  {

    Optional<WordEntity> findByOriginalAndUser_Id(String original, UUID userId);

    boolean existsByOriginalAndUser_Id(String original, UUID userId);

    void deleteByOriginalAndUser_Id(String original, UUID userId);

    List<WordEntity> findByUser_Id(UUID userId);
}

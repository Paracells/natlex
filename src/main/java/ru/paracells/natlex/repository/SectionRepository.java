package ru.paracells.natlex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.paracells.natlex.models.Section;

import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, String> {

    Optional<Section> findSectionByName(String name);

    void deleteSectionByName(String name);


//    List<Section> findSectionByJobid(Integer jobId);
}

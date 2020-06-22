package ru.paracells.natlex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.paracells.natlex.models.Section;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, String> {

    List<Section> findSectionsByGeologicalСlassesCode(String  code);

    Optional<Section> findSectionByName(String name);

    void deleteSectionByName(String name);


}
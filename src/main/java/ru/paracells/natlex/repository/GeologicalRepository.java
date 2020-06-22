package ru.paracells.natlex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.paracells.natlex.models.GeologicalClass;

public interface GeologicalRepository extends JpaRepository<GeologicalClass, String> {
}

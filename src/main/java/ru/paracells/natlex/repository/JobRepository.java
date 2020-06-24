package ru.paracells.natlex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.paracells.natlex.models.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
}

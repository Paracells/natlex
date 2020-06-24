package ru.paracells.natlex.service;

import org.springframework.web.multipart.MultipartFile;
import ru.paracells.natlex.models.Section;

import java.util.List;

public interface SaveAndReadService {

    void read(MultipartFile file, Long jobId);

    void save(Long id);

    void export(List<Section> list, Long jobId);
}

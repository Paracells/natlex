package ru.paracells.natlex.service;

import ru.paracells.natlex.models.Section;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Future;

public interface SaveAndReadService {

    Future<Void> read(InputStream stream, Long jobId);

    void save(Long id);

    void export(List<Section> list, Long jobId);
}

package ru.paracells.natlex.xlsxutils;

import org.springframework.web.multipart.MultipartFile;
import ru.paracells.natlex.models.Section;

import java.util.List;
import java.util.concurrent.Future;

public interface XLSService {

    Future<List<Section>> loadFile(MultipartFile file) throws Exception;

    void createBook(Iterable<Section> all);

    void saveBook();
}

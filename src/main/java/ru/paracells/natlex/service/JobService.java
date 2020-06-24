package ru.paracells.natlex.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface JobService {

    String getJobState(Long jobId);

    Long startImportJob(MultipartFile file);

    String startJExportJob();

    ResponseEntity<String>  saveFile(Long id);

}



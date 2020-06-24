package ru.paracells.natlex.service;

import org.springframework.web.multipart.MultipartFile;

public interface JobService {

    String getJobState(Integer jobId);

    Integer startImportJob(MultipartFile file);

    Integer startJExportJob();

}



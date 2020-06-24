package ru.paracells.natlex.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.paracells.natlex.models.Section;
import ru.paracells.natlex.repository.SectionRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class JobServiceImpl implements JobService {
    AtomicInteger atomicInteger = new AtomicInteger();


    private Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    private SectionRepository sectionRepository;
    private SaveAndReadService saveAndReadService;

    @Autowired
    public JobServiceImpl(SectionRepository sectionRepository, SaveAndReadService saveAndReadService) {
        this.sectionRepository = sectionRepository;
        this.saveAndReadService = saveAndReadService;
    }

    // получим статус работы
    @Override
    public String getJobState(Integer jobId) {
        List<Section> listJob = sectionRepository.findSectionByJobid(jobId);
        return listJob.get(listJob.size() - 1).getJobstate();
    }

    @Override
    public Integer startImportJob(MultipartFile file) {
        int jobId = atomicInteger.incrementAndGet();
        saveAndReadService.read(file, jobId);
        return jobId;
    }


    // промежуточный слой, далее мы передадим в чтение файла
    // а здесь обработаем результат
    @Override
    public Integer startJExportJob() {
        List<Section> jobList = sectionRepository.findAll();
        if (jobList.size() == 0) {
            logger.info(">>>No data in Database, first you should import<<<");
            return 0;
        } else {
            Integer jobid = jobList.get(0).getJobid();
            saveAndReadService.export(jobList);
            return jobid;
        }
    }


}

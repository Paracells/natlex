package ru.paracells.natlex.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.paracells.natlex.models.Job;
import ru.paracells.natlex.models.Section;
import ru.paracells.natlex.repository.JobRepository;
import ru.paracells.natlex.repository.SectionRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class JobServiceImpl implements JobService {
    AtomicLong atomicInteger = new AtomicLong();


    private Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    private SectionRepository sectionRepository;
    private SaveAndReadService saveAndReadService;

    private JobRepository jobRepository;

    public JobServiceImpl(SectionRepository sectionRepository, SaveAndReadService saveAndReadService, JobRepository jobRepository) {
        this.sectionRepository = sectionRepository;
        this.saveAndReadService = saveAndReadService;
        this.jobRepository = jobRepository;
    }

    // получим статус работы
    @Override
    public String getJobState(Long jobId) {
        Optional<Job> byId = jobRepository.findById(jobId);
        if (byId.isPresent()) {
            return byId.get().getJobstate();
        }
        logger.info("No file with id: " + jobId);
        return "No file with id: " + jobId;


    }

    @Override
    public Long startImportJob(MultipartFile file) {
        Long jobId = atomicInteger.incrementAndGet();
        saveAndReadService.read(file, jobId);
        return jobId;
    }


    @Override
    public String startJExportJob() {

        List<Section> jobList = sectionRepository.findAll();
        if (jobList.size() == 0) {
            return "No data in Database, first you should import";
        } else {
            Long jobId = atomicInteger.incrementAndGet();
            saveAndReadService.export(jobList, jobId);
            return jobId.toString();
        }
    }

    @Override
    public ResponseEntity<String> saveFile(Long id) {

        Optional<Job> byId = jobRepository.findById(id);
        if (byId.isPresent()) {
            if (byId.get().getJobstate().equals("DONE")) {
                saveAndReadService.save(id);
            } else {
                return new ResponseEntity<>("File with id " + id + " is not ready", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("There is no file with id: " + id, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("filename: result.xlsx", HttpStatus.OK);
    }


}

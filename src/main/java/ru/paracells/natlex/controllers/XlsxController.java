package ru.paracells.natlex.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.paracells.natlex.repository.SectionRepository;
import ru.paracells.natlex.service.JobService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class XlsxController {


    ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();
    AtomicInteger workId = new AtomicInteger();

    Logger logger = LoggerFactory.getLogger(MainController.class);

    private final SectionRepository sectionRepository;

    private JobService jobService;


    @Autowired
    public XlsxController(SectionRepository sectionRepository, JobService jobService) {
        this.sectionRepository = sectionRepository;
        this.jobService = jobService;
    }


    // import (file) returns ID of the Async Job and launches importing.

    @PostMapping("/import")
    public Integer importFile(@RequestParam("file") MultipartFile file) {
        Integer integer = jobService.startImportJob(file);
        return integer;

    }

    @GetMapping("/import/{id}")
    public String importFile(@PathVariable("id") Integer id) {
        return jobService.getJobState(id);
    }


    // export returns ID of the Async Job and launches exporting.
    @GetMapping("/export")
    public Integer exportToXLS() {
        Integer integer = jobService.startJExportJob();
        return integer;
    }


    // API GET /export/{id} returns result of parsed file by Job ID ("DONE", "IN PROGRESS", "ERROR") 
    @GetMapping("/export/{id}")
    public String resultParsed(@PathVariable("id") Integer id) {
        return jobService.getJobState(id);
    }

    // export/{id} returns result of parsed file by Job ID ("DONE", "IN PROGRESS", "ERROR") 
    @GetMapping("/export/{id}/file")
    public ResponseEntity<String> exportWithId(@PathVariable("id") Integer id) {

        if (checkFile(id).equals(State.ERROR.getTitle()))
            return new ResponseEntity<>("There is no file with id: " + id, HttpStatus.INTERNAL_SERVER_ERROR);
        if (map.get(id).equals(State.DONE.getTitle())) {
//            XLSService.saveBook();

        } else {
            return new ResponseEntity<>("file is not ready, status: " + map.get(id),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("filename: result.xlsx", HttpStatus.OK);

    }


    private String checkFile(int id) {
        if (!map.containsKey(id)) {
            logger.info("There is no file with id: " + id);
            return State.ERROR.getTitle();
        }
        return map.get(id);
    }

    private enum State {
        DONE("DONE"),
        IN_PROGRESS("IN PROGRESS"),
        ERROR("ERROR");

        private final String title;

        State(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }


}

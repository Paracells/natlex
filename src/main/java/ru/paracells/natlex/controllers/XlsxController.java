package ru.paracells.natlex.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.paracells.natlex.models.Section;
import ru.paracells.natlex.repository.SectionRepository;
import ru.paracells.natlex.xlsxutils.XLSService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class XlsxController {


    ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();
    AtomicInteger workId = new AtomicInteger();

    Logger logger = LoggerFactory.getLogger(MainController.class);

    private final SectionRepository sectionRepository;
    private final XLSService XLSService;

    @Autowired
    public XlsxController(SectionRepository sectionRepository, XLSService XLSService) {
        this.sectionRepository = sectionRepository;
        this.XLSService = XLSService;
    }


    // import (file) returns ID of the Async Job and launches importing.
    @Async("taskExecutor")
    @PostMapping("/import")
    public CompletableFuture<Integer> importFile(@RequestParam("file") MultipartFile file) {
        Integer countId = workId.incrementAndGet();
        map.put(countId, State.IN_PROGRESS.getTitle());

        //TODO переписать?
        // сходим - проверим файл, если null, то присвоим State. ERROR
        CompletableFuture<Future<List<Section>>> futureCompletableFuture = CompletableFuture.supplyAsync(
                () -> {
                    Future<List<Section>> listFuture = null;
                    try {
                        listFuture = XLSService.loadFile(file);

                    } catch (Exception e) {
                        map.put(countId, State.ERROR.getTitle());
                    }
                    return listFuture;
                });
        futureCompletableFuture.thenApplyAsync(listFuture -> {
            List<Section> list = null;
            try {
                if (listFuture == null) {
                    map.put(countId, State.ERROR.getTitle());
                } else {
                    list = listFuture.get();
                    list.forEach(x -> sectionRepository.save(x));
                    map.put(countId, State.DONE.getTitle());
                }

            } catch (InterruptedException | ExecutionException e) {
                map.put(countId, State.ERROR.getTitle());

            }
            return list;
        });

        return CompletableFuture.completedFuture(countId);

    }

    @GetMapping("/import/{id}")
    public String importFile(@PathVariable("id") Integer id) {
        return checkFile(id);
    }


    // export returns ID of the Async Job and launches exporting.
    @Async("taskExecutor")
    @GetMapping("/export")
    public CompletableFuture<Integer> exportToXLS() {

        // all repository
        if (sectionRepository.count() == 0) {
            logger.info("I can't export EMPTY DB");
            return CompletableFuture.completedFuture(0);
        } else {
            Iterable<Section> all = sectionRepository.findAll();
            Integer countId = workId.incrementAndGet();
            map.put(countId, State.IN_PROGRESS.getTitle());
            CompletableFuture.runAsync(() -> {
                XLSService.createBook(all);
            }).thenRun(() -> map.put(countId, State.DONE.getTitle()));
            return CompletableFuture.completedFuture(countId);
        }

    }

    // API GET /export/{id} returns result of parsed file by Job ID ("DONE", "IN PROGRESS", "ERROR") 
    @GetMapping("/export/{id}")
    public String resultParsed(@PathVariable("id") Integer id) {
        return checkFile(id);
    }

    // export/{id} returns result of parsed file by Job ID ("DONE", "IN PROGRESS", "ERROR") 
    @GetMapping("/export/{id}/file")
    public ResponseEntity<String> exportWithId(@PathVariable("id") Integer id) {

        if (checkFile(id).equals(State.ERROR.getTitle()))
            return new ResponseEntity<>("There is no file with id: " + id, HttpStatus.INTERNAL_SERVER_ERROR);
        if (map.get(id).equals(State.DONE.getTitle())) {
            XLSService.saveBook();

        } else {
            return new ResponseEntity<>("file is not ready, status: " + map.get(id),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("filename: result.xlsx", HttpStatus.OK);

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

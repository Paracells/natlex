package ru.paracells.natlex.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.paracells.natlex.models.Section;
import ru.paracells.natlex.repository.SectionRepository;
import ru.paracells.natlex.service.UtilsService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RestController
public class MainController {

    @Value("${generate.section}")
    private int NUMBER_OF_SECTION;

    Logger logger = LoggerFactory.getLogger(MainController.class);

    private  UtilsService utilsService;
    private  SectionRepository sectionRepository;

    public MainController() {
    }

    @Autowired
    public MainController(UtilsService utilsService, SectionRepository sectionRepository) {
        this.utilsService = utilsService;
        this.sectionRepository = sectionRepository;
    }

    // add fake values
    @GetMapping(value = "/base")
    public void baseValues() {

        for (int i = 1; i <= NUMBER_OF_SECTION; i++) {
            Section section = new Section();
            section.setName("Section " + i);
            utilsService.addToList(section, i);
            sectionRepository.save(section);
            logger.info(section.toString());

        }
    }


  /*  // Add API GET /sections/by-code?code=..
    @GetMapping(value = "/sections/by-code", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> findByCode(@RequestParam String code) {
        List<Section> list = sectionRepository.findSectionsByGeologicalСlassesCode(code);
        List<String> collect = list.stream().map(Section::getName).collect(Collectors.toList());
        return collect;
    }*/


    @PostMapping("/create")
    public String addValues(@RequestBody Section section) {
        sectionRepository.save(section);
        return section.toString();

    }

    @GetMapping(value = "/read", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Section> readAll() {
        List<Section> list = sectionRepository.findAll();
        return list;
    }

    @GetMapping(value = "/read/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Section> readById(@PathVariable("name") String name) {
        return sectionRepository.findSectionByName(name);
    }

    @PutMapping("update/{name}")
    public Section updateById(@PathVariable("name") String name, @RequestBody Section section) {
        Optional<Section> byId = sectionRepository.findSectionByName(name);
        if (byId.isPresent()) {
            Section updated = byId.get();
            updated.setName(section.getName());
            updated.setGeoClasses(section.getGeoClasses());
            sectionRepository.save(updated);
            return updated;
        }
        return null;

    }

    @Transactional
    @DeleteMapping("/delete/{name}")
    public void deleteById(@PathVariable("name") String name) {
        sectionRepository.deleteSectionByName(name);
    }

}

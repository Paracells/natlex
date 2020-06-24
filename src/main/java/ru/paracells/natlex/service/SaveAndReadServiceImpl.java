package ru.paracells.natlex.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import ru.paracells.natlex.controllers.MainController;
import ru.paracells.natlex.models.GeologicalClass;
import ru.paracells.natlex.models.Job;
import ru.paracells.natlex.models.Section;
import ru.paracells.natlex.repository.JobRepository;
import ru.paracells.natlex.repository.SectionRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class SaveAndReadServiceImpl implements SaveAndReadService {

    @Value("${app.pause}")
    private Integer pause;

    private volatile XSSFWorkbook book;

    private Logger logger = LoggerFactory.getLogger(MainController.class);


    private volatile Section section;
    private volatile GeologicalClass geologicalClass;

    private SectionRepository sectionRepository;

    private JobRepository jobRepository;

    @Autowired
    public SaveAndReadServiceImpl(SectionRepository sectionRepository, JobRepository jobRepository) {
        this.sectionRepository = sectionRepository;
        this.jobRepository = jobRepository;
    }

    @Async
    @Override
    public Future<Void> read(InputStream stream, Long jobId) {

        Job job = new Job();

        job.setId(jobId);
        job.setJobname("import");
        try {

            book = (XSSFWorkbook) WorkbookFactory.create(stream);
        } catch (IOException e) {
            job.setJobstate(State.ERROR.getState());
            jobRepository.save(job);
            return new AsyncResult<>(null);
        }

        job.setJobstate(State.IN_PROGRESS.getState());
        jobRepository.save(job);

        // Does we have section in title?
        XSSFSheet sheet = book.getSheetAt(0);
        XSSFRow checkCellRowForSection = sheet.getRow(0);
        if (!checkCellRowForSection.getCell(0).toString().toLowerCase().contains("section")) {
            job.setJobstate(State.ERROR.getState());
            jobRepository.save(job);
            return new AsyncResult<>(null);
        }
        sheet.removeRow(sheet.getRow(0));

        for (Row row : sheet) {
            try {
                Thread.sleep(pause);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Iterator<Cell> cellIterator = row.cellIterator();
            geologicalClass = new GeologicalClass();
            section = new Section();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                String value = cell.getStringCellValue();
                if (value.contains("Section")) {
                    section.setName(value);


                } else if (value.contains("Geo")) {
                    geologicalClass.setName(value);
                } else if (value.contains("GC")) {
                    geologicalClass.setCode(value);
                    section.getGeoClasses().add(geologicalClass);
                    geologicalClass = new GeologicalClass();
                } else {
                    job.setJobstate(State.ERROR.getState());
                    jobRepository.save(job);
                    return new AsyncResult<>(null);
                }

            }
            sectionRepository.save(section);
            logger.info(section.toString());

        }
        job.setJobstate(State.DONE.getState());
        jobRepository.save(job);
        sectionRepository.save(section);
        return new AsyncResult<>(null);

    }

    @Override
    public void save(Long id) {

        try {
            try (FileOutputStream loadFile = new FileOutputStream("result.xlsx")) {
                try {
                    book.write(loadFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void export(List<Section> all, Long jobId) {
        logger.info("EXPORT XLSX");

        Job job = new Job();
        job.setId(jobId);
        job.setJobstate(State.IN_PROGRESS.getState());
        jobRepository.save(job);

        book = new XSSFWorkbook();
        XSSFSheet sheet = book.createSheet("Sheet 1");

        List<GeologicalClass> geologicalClasses = new ArrayList<>();
        // заголовок секции
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("Section name");
        sheet.autoSizeColumn(0);

        int rowForClass = 1;
        for (Section section : all) {
            XSSFRow sectionNumber = sheet.createRow(rowForClass);
            XSSFCell leftCell = sectionNumber.createCell(0);
            leftCell.setCellValue(section.getName());
            int logicCell = 1;
            int countSection = 1;
            geologicalClasses.addAll(section.getGeoClasses());
//            List<GeologicalClass> geologicalClasses = new ArrayList<>(section.getGeoClasses());
            logger.info(section.getName());
            try {
                Thread.sleep(pause);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (GeologicalClass geo_cls : geologicalClasses) {

                // Class Name + Geo Class XX
                XSSFCell headerCell = row.createCell(logicCell);
                headerCell.setCellValue("Class " + countSection + " name");
                sheet.autoSizeColumn(logicCell);
                XSSFCell cellClass = sectionNumber.createCell(logicCell);
                cellClass.setCellValue(geo_cls.getName());


                // Class Code + Code
                XSSFCell codeCell = row.createCell(logicCell + 1);
                codeCell.setCellValue("Class " + countSection + " code");
                sheet.autoSizeColumn(logicCell + 1);
                XSSFCell cellCalss1 = sectionNumber.createCell(logicCell + 1);
                cellCalss1.setCellValue(geo_cls.getCode());


                logicCell += 2;
                countSection++;
            }
            rowForClass++;

        }
        job.setJobstate(State.DONE.getState());
        jobRepository.save(job);
        sectionRepository.save(section);
    }

    public enum State {
        DONE("DONE"),
        IN_PROGRESS("IN PROGRESS"),
        ERROR("ERROR");

        State(String state) {
            this.state = state;
        }

        private String state;

        private String getState() {
            return state;
        }
    }


}

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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.paracells.natlex.controllers.MainController;
import ru.paracells.natlex.models.GeologicalClass;
import ru.paracells.natlex.models.Section;
import ru.paracells.natlex.repository.SectionRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class SaveAndReadServiceImpl implements SaveAndReadService {

    @Value("${app.pause}")
    private Integer pause;

    private XSSFWorkbook book;


    private Logger logger = LoggerFactory.getLogger(MainController.class);


    private Section section;
    private GeologicalClass geologicalClass;

    private SectionRepository sectionRepository;

    @Autowired
    public SaveAndReadServiceImpl(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Override
    @Async
    public void read(MultipartFile file, int jobId) {

        try {
            book = (XSSFWorkbook) WorkbookFactory.create(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Does we have section in title?
        XSSFSheet sheet = book.getSheetAt(0);
        XSSFRow checkCellRowForSection = sheet.getRow(0);
        if (!checkCellRowForSection.getCell(0).toString().toLowerCase().contains("section")) {
            // -----
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
                    section.setJobid(jobId);
                    section.setJobstate(State.IN_PROGRESS.getState());
                } else if (value.contains("Geo")) {
                    geologicalClass.setName(value);
                } else if (value.contains("GC")) {
                    geologicalClass.setCode(value);
                    section.getGeoClasses().add(geologicalClass);
                    geologicalClass = new GeologicalClass();
                } else {
                }

            }
            sectionRepository.save(section);
            logger.info(section.toString());

        }
        section.setJobstate(State.DONE.getState());
        sectionRepository.save(section);
    }

    @Override
    @Async
    public void save() {

    }

    @Override
    @Async
    public void export(List<Section> all) {
        logger.info("EXPORT XLSX");
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
        section.setJobstate(State.DONE.getState());
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

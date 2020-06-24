package ru.paracells.natlex.xlsxutils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.paracells.natlex.models.GeologicalClass;
import ru.paracells.natlex.models.Section;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class XLSServiceImpl implements XLSService {
    Logger logger = LoggerFactory.getLogger(XLSServiceImpl.class);


    @Value("${app.pause}")
    private Integer pause;

    private XSSFWorkbook book;

    private List<Section> sections = new ArrayList<>();
    private Section section;
    private GeologicalClass geo_cls;

    //save deserialization file
    @Override
    public Future<List<Section>> loadFile(MultipartFile file) {

        try {
            book = (XSSFWorkbook) WorkbookFactory.create(file.getInputStream());
        } catch (IOException e) {
            return null;
        }

        // Does we have section in title?
        XSSFSheet sheet = book.getSheetAt(0);
        XSSFRow checkCellRowForSection = sheet.getRow(0);
        if (!checkCellRowForSection.getCell(0).toString().toLowerCase().contains("section")) {
            return null;
        }
        sheet.removeRow(sheet.getRow(0));


        for (Row row : sheet) {
            try {
                Thread.sleep(pause);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Iterator<Cell> cellIterator = row.cellIterator();
            geo_cls = new GeologicalClass();
            section = new Section();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                String value = cell.getStringCellValue();
                if (value.contains("Section")) {
                    section.setName(value);
                } else if (value.contains("Geo")) {
                    geo_cls.setName(value);
                } else if (value.contains("GC")) {
                    geo_cls.setCode(value);
                    section.getGeoClasses().add(geo_cls);
                    geo_cls = new GeologicalClass();
                } else {
                    return null;
                }

            }
            sections.add(section);
            logger.info(section.toString());

        }

        return CompletableFuture.completedFuture(sections);

    }

    @Override
    public void createBook(Iterable<Section> all) {


        logger.info("EXPORT XLSX");
        book = new XSSFWorkbook();
        XSSFSheet sheet = book.createSheet("Sheet 1");

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
            List<GeologicalClass> geologicalClasses = new ArrayList<>(section.getGeoClasses());
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
      /*  book.write(fileOut);
        fileOut.close();*/
    }

    @Override
    public void saveBook() {
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
}

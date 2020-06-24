package ru.paracells.natlex.TestCRUD;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.paracells.natlex.models.GeologicalClass;
import ru.paracells.natlex.models.Section;
import ru.paracells.natlex.repository.SectionRepository;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TestCRUD {


    @Autowired
    private SectionRepository sectionRepository;


    public TestCRUD() {
    }

    @Test
    public void TestCreateToDB() {
        helperForAddElements(10);
        assertEquals(10, sectionRepository.count());
    }

    @Test
    public void TestReadFromDB() {
        helperForAddElements(1);
        Section one = sectionRepository.findSectionByName("Section 1").orElse(null);
        System.out.println(one);
        assertNotNull(one);
    }

   /* @Test
    public void TestUpdateInDB() {
        helperForAddElements(1);
        Section getFromDB = sectionRepository.findAll().get(0);
        List<GeologicalClass> gcList = new ArrayList<>();
        gcList.add(new GeologicalClass("Geo Class 444", "GC444"));
        gcList.add(new GeologicalClass("Geo Class 555", "GC555"));
        Section forUpdate = new Section();
        forUpdate.setName(getFromDB.getName());
        forUpdate.setGeologicalСlasses(gcList);
        sectionRepository.save(forUpdate);
        System.out.println(getFromDB.toString());
        System.out.println(forUpdate.toString());
        assertNotEquals(getFromDB, forUpdate);
    }*/

   /* @Test
    public void TestSectionsByCode() {
        helperForAddElements(4);
        List<Section> sectionList = sectionRepository.findSectionsByGeologicalСlassesCode("GC41");
        assertEquals(sectionList.get(0).getName(), "Section 4");
    }*/

    @Test
    public void TestDeleteFromDB() {
        helperForAddElements(1);
        Section getFromDB = sectionRepository.findSectionByName("Section 1").orElse(null);
        assertEquals("Section 1", getFromDB.getName());
        sectionRepository.delete(getFromDB);
        assertEquals(0, sectionRepository.count());
    }


    //генерируем табличку, в параметры передаём кол-во элементов
    private void helperForAddElements(int values) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Section section;
        for (int i = 1; i <= values; i++) {
            section = section = new Section();
            section.setName("Section " + i);
            for (int j = 1; j <= random.nextInt(3); j++) {
                GeologicalClass geologicalClass = new GeologicalClass();
                geologicalClass.setCode("GC" + i + j);
                geologicalClass.setName("Geo Class" + i + j);
                section.getGeoClasses().add(geologicalClass);
            }
            sectionRepository.save(section);

        }


    }
}

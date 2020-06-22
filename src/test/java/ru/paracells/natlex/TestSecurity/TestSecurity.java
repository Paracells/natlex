package ru.paracells.natlex.TestSecurity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class TestSecurity {

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Test
    public void testWrongUser() throws Exception {
        ResponseEntity<String> responseEntity = testRestTemplate.withBasicAuth("", "")
                .getForEntity("/export",String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());

    }


    @Test
    public void testExport() throws Exception {
        ResponseEntity<String> responseEntity = testRestTemplate.withBasicAuth("user", "user")
                .getForEntity("/export",String.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    }

    @Test
    public void testExportWithID() throws Exception {
        ResponseEntity<String> responseEntity = testRestTemplate.withBasicAuth("user", "user")
                .getForEntity("/export/1",String.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    }

    @Test
    public void testExportWithIdAndFile() throws Exception {
        ResponseEntity<String> responseEntity = testRestTemplate.withBasicAuth("user", "user")
                .getForEntity("/export/1",String.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    }


}

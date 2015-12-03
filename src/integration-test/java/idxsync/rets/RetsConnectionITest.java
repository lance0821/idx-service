package idxsync.rets;


import idxsync.Application;
import idxsync.rets.metadata.Metadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.realtors.rets.client.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest("server.port:0")
public class RetsConnectionITest {

    @Autowired
    private RetsConnection retsConnection;

    @Before
    public void setup() {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @After
    public void tearDown() {
        retsConnection.endSession();
    }

    @Test
    public void testGetMetadata() {

        Metadata metadata = retsConnection.getMetadata();

        assertThat(metadata, notNullValue());

        //resources
        assertThat(metadata.getResources(), notNullValue());
        assertThat(metadata.getResources().size(), greaterThan(0));

        //classes
        metadata.getResources().forEach((key, resource) -> {
            assertThat(resource, notNullValue());
            assertThat(resource.getClassifications().size(), greaterThan(0));

            //tables
            resource.getClassifications().forEach((clsKey, cls) -> {
                assertThat(cls.getTableList(), notNullValue());
                assertThat(cls.getTableList().size(), greaterThan(0));

                cls.getTableList().forEach(table -> {
                    System.out.println(table.getLongName());
                });
            });
        });
    }

    @Test
    public void testSearch() {
        LocalDate now = LocalDate.now();

        String monthValue = "" + now.getMonth().getValue();
        if (monthValue.length() < 2) {
            monthValue = "0"+monthValue;
        }

        String dayValue = "" + now.getDayOfMonth();
        if (dayValue.length() < 2) {
            dayValue = "0"+dayValue;
        }

        String dtString = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        SearchResult searchResult =
                retsConnection.search("PROPERTY", "RESI", String.format("(MatrixModifiedDT=%s-)", dtString));

        assertThat(searchResult, notNullValue());
        assertThat(searchResult.getColumns(), notNullValue());
        assertThat(searchResult.iterator(), notNullValue());
    }
}

package idxsync.mapping;

import idxsync.Application;
import idxsync.domain.*;
import idxsync.rets.RetsConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest("server.port:0")
public class MapperITest {

    @Autowired
    private RetsConnection retsConnection;

    @Autowired
    private ListingCommercialMapper listingCommercialMapper;

    @Autowired
    private ListingResidentialMapper listingResidentialMapper;

    @Autowired
    private ListingLandMapper listingLandMapper;

    @Autowired
    private ListingMultMapper listingMultMapper;

    @Autowired
    private OpenHouseMapper openHouseMapper;

    private String dateQueryString;


    @Before
    public void setup() {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }

        dateQueryString = "2015-07-13T00:00:00.000";
    }

    @After
    public void tearDown() {
        retsConnection.endSession();
    }

    @Test
    public void testListingCommercialMapper() {

        ListingCommercialMapper mapper = listingCommercialMapper;

        Set<ListingCommercial> listings = mapper.mapData(
                retsConnection.searchListingCommercial(String.format("(MatrixModifiedDT=%s+)", dateQueryString)));

        assertThat(listings, notNullValue());
        assertThat(listings.size(), greaterThan(0));

        listings.forEach(listing -> {
            assertThat(Long.toString(listing.getMatrixUniqueId()).length(), greaterThan(0));
            assertThat(Double.toString(listing.getListPrice()).length(), greaterThan(0));
        });
    }

    @Test
    public void testListingLandMapper() {

        ListingLandMapper mapper = listingLandMapper;

        Set<ListingLand> listings = mapper.mapData(
                retsConnection.searchListingLand(String.format("(MatrixModifiedDT=%s+)", dateQueryString)));

        assertThat(listings, notNullValue());
        assertThat(listings.size(), greaterThan(0));

        listings.forEach(listing -> {
            assertThat(Long.toString(listing.getMatrixUniqueId()).length(), greaterThan(0));
            assertThat(Double.toString(listing.getListPrice()).length(), greaterThan(0));
        });
    }

    @Test
    public void testListingMultMapper() {

        ListingMultMapper mapper = listingMultMapper;

        Set<ListingMult> listings = mapper.mapData(
                retsConnection.searchListingMult(String.format("(MatrixModifiedDT=%s+)", dateQueryString)));

        assertThat(listings, notNullValue());
        assertThat(listings.size(), greaterThan(0));

        listings.forEach(listing -> {
            assertThat(Long.toString(listing.getMatrixUniqueId()).length(), greaterThan(0));
            assertThat(Double.toString(listing.getListPrice()).length(), greaterThan(0));
        });
    }

    @Test
    public void testListingResidentialMapper() {

        ListingResidentialMapper mapper = listingResidentialMapper;

        Set<ListingResidential> listings = mapper.mapData(
                retsConnection.searchListingResidential(String.format("(MatrixModifiedDT=%s+)", dateQueryString)));

        assertThat(listings, notNullValue());
        assertThat(listings.size(), greaterThan(1));

        listings.forEach(listing -> {
            assertThat(Long.toString(listing.getMatrixUniqueId()).length(), greaterThan(0));
            assertThat(Double.toString(listing.getListPrice()).length(), greaterThan(0));
        });
    }

    @Test
    public void testOpenHouseMapper() {
        OpenHouseMapper mapper = openHouseMapper;

        Set<OpenHouse> openHouseList = mapper.mapData(
                retsConnection.searchOpenHouse(
                        String.format("(MatrixModifiedDT=%s+),(activeYN=1)", dateQueryString)));

        assertThat(openHouseList, notNullValue());
        assertThat(openHouseList.size(), greaterThan(0));

        openHouseList.forEach(openhouse -> {
            assertThat(Long.toString(openhouse.getMatrixUniqueId()).length(), greaterThan(0));
            assertThat(Double.toString(openhouse.getListingMatrixUniqueId()).length(), greaterThan(0));
        });
    }
}

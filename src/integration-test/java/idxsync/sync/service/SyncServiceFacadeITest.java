package idxsync.sync.service;

import idxsync.Application;
import idxsync.domain.SyncFullStat;
import idxsync.persistence.repository.ListingCommercialRepository;
import idxsync.persistence.repository.ListingLandRepository;
import idxsync.persistence.repository.ListingMultRepository;
import idxsync.persistence.repository.ListingResidentialRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Random;

import static idxsync.domain.SyncStatus.COMPLETE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest("server.port:0")
public class SyncServiceFacadeITest {

    @Autowired
    private SyncServiceFacade syncService;

    @Resource
    private ListingResidentialRepository listingResidentialRepository;

    @Resource
    private ListingCommercialRepository listingCommercialRepository;

    @Resource
    private ListingLandRepository listingLandRepository;

    @Resource
    private ListingMultRepository listingMultRepository;

    @After
    public void tearDown() {

        listingResidentialRepository.deleteAll();
        listingCommercialRepository.deleteAll();
        listingLandRepository.deleteAll();
        listingMultRepository.deleteAll();
    }

    @Test
    public void testSyncIdxData() {

        SyncFullStat syncFullStat = syncService.syncIdxData(
                "" + new Random(System.currentTimeMillis()).nextLong(), LocalDateTime.now().minusDays(1));

        assertThat(syncFullStat.getStatus(), is(COMPLETE));
        assertThat(syncFullStat.getErrorMessage(), isEmptyOrNullString());
        assertThat(syncFullStat.getSyncStatList().size(), is(5));
        assertThat(syncFullStat.getSyncDurationMillis(), greaterThan((long) 0));
    }
}


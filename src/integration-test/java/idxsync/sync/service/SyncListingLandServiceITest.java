package idxsync.sync.service;


import idxsync.Application;
import idxsync.domain.ListingLand;
import idxsync.domain.SyncStat;
import idxsync.persistence.repository.ListingLandRepository;
import idxsync.rets.RetsConnection;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest("server.port:0")
public class SyncListingLandServiceITest {

    @Autowired
    private SyncListingLandService syncService;

    @Autowired
    private RetsConnection retsConnection;

    @Resource
    private ListingLandRepository listingLandRepository;

    @After
    public void tearDown() {

        listingLandRepository.deleteAll();

        retsConnection.endSession();
    }

    @Test
    public void testSync() {

        SyncStat syncStat = syncService.sync(retsConnection, LocalDateTime.now().minusMonths(6));

        assertThat(syncStat.getSyncType(), is(ListingLand.class.getName()));
        assertThat(syncStat.getNumNewRecords(), greaterThan(0));
        assertThat(syncStat.getNumRecordsFromRets(), is(syncStat.getNumNewRecords()));
        assertThat(syncStat.getNumUpdatedRecords(), is(0));
        assertThat(syncStat.getNumDeletedRecords(), is(0));
        assertThat(syncStat.getNumRecordsInDbPreSync(), is(0));
        assertThat(syncStat.getNumRecordsInDbPostSync(), is(syncStat.getNumNewRecords()));
    }
}

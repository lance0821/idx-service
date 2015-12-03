package idxsync.sync.service;

import idxsync.Application;
import idxsync.domain.SyncStat;
import idxsync.domain.SyncStatus;
import idxsync.persistence.repository.LookupValueRepository;
import idxsync.rets.RetsConnection;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest("server.port:0")
public class SyncLookupValueServiceITest {

    @Autowired
    private SyncLookupValuesService syncLookupValuesService;

    @Autowired
    private RetsConnection retsConnection;

    @Resource
    private LookupValueRepository repository;

    @After
    public void tearDown() {

        repository.deleteAll();

        retsConnection.endSession();
    }

    @Test
    public void testSyncLookupValues() {
        SyncStat syncStat = syncLookupValuesService.syncLookupValues(retsConnection, false);

        assertThat(syncStat.getStatus(), is(SyncStatus.COMPLETE));
    }
}

package idxsync.sync.service;

import idxsync.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest("server.port:0")
public class SyncManagerServiceITest {

    @Autowired
    private SyncManagerService syncManagerService;

    @Test
    public void testSynxIdxData() {
        String syncToken = syncManagerService.syncIdxData(null, false);
    }
}

package idxsync.sync.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class SyncSchedulerHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SyncSchedulerHandler.class);

    @Value("${sync.idx.sync.stat.report.email.address}")
    private String syncStatReportEmailAddress;

    @Value("${sync.idx.sync.stat.report.email.exclude.complete}")
    private boolean syncStatReportExcludeStatusComplete;

    @Autowired
    private SyncManagerService syncManagerService;

    @Override
    public void run() {

        EmailNotificationConfig emailNotificationConfig = null;

        if (StringUtils.isNotBlank(syncStatReportEmailAddress)) {
            emailNotificationConfig =
                    new EmailNotificationConfig(syncStatReportEmailAddress, syncStatReportExcludeStatusComplete);
        }

        String token = syncManagerService.syncIdxData(emailNotificationConfig, false);
        
        logger.info("Scheduler invoking idx data sync. Sync token: {}", token);
    }
}

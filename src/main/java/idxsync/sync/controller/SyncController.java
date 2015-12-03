package idxsync.sync.controller;

import idxsync.domain.SyncFullStat;
import idxsync.sync.service.EmailNotificationConfig;
import idxsync.sync.service.SyncManagerService;
import idxsync.sync.service.SyncNotPermittedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class SyncController {

    private final Logger logger = LoggerFactory.getLogger(SyncController.class);

    @Autowired
    private SyncManagerService syncManagerService;

    @Value("${sync.idx.refresh.restrict.check}")
    private boolean syncRefreshRestrictCheck;


    @RequestMapping(value="/sync", method=POST)
    @ResponseStatus(HttpStatus.CREATED)
    public SyncStartResponse syncStart(@RequestBody(required = false) SyncConfig syncConfig) {

        logger.info("syncStart invoked.");

        String emailNotificationAddress = null;
        if (syncConfig != null) {
            emailNotificationAddress = syncConfig.getEmailAddress();

            logger.info("Sync config provided. Will be sending email report to {}.", emailNotificationAddress);
        }

        SyncStartResponse resp = new SyncStartResponse();

        EmailNotificationConfig emailNotificationConfig = null;
        if (emailNotificationAddress != null) {
            emailNotificationConfig = new EmailNotificationConfig(emailNotificationAddress, false);
        }

        String syncToken = syncManagerService.syncIdxData(emailNotificationConfig, syncRefreshRestrictCheck);

        resp.setSyncToken(syncToken);

        return resp;
    }

    @RequestMapping(value="/sync/{syncToken}", method= RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public SyncStatusResponse syncStatus(@PathVariable String syncToken) {

        logger.info("syncStatus invoked with token {}.", syncToken);

        SyncStatusResponse resp = new SyncStatusResponse();

        SyncFullStat syncFullStat = syncManagerService.getSyncFullStatRecord(syncToken);

        if (syncFullStat == null) {
            String err = String.format("Could not find sync record with the supplied token: %s", syncToken);
            throw new IllegalArgumentException(err);
        }

        resp.setSyncFullStat(syncFullStat);

        return resp;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private void handleBadRequests(HttpServletResponse response, Exception exception) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler(SyncNotPermittedException.class)
    private void handleSyncNotPermitted(HttpServletResponse response, Exception exception) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }
}

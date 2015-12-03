package idxsync.sync.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import idxsync.domain.SyncFullStat;
import idxsync.domain.SyncStatus;
import idxsync.persistence.repository.SyncFullStatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static idxsync.domain.SyncStatus.COMPLETE;

@Service
public class SyncManagerService {
    private final Logger logger = LoggerFactory.getLogger(SyncManagerService.class);

    @Autowired
    private SyncServiceFacade syncServiceFacade;

    @Resource
    private SyncFullStatRepository syncFullStatRepository;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private SimpleMailMessage syncReportEmailTemplate;

    @Value("${sync.idx.refresh.restrict.duration.minutes}")
    private int syncRefreshRestrictDurationMinutes;

    public String syncIdxData(EmailNotificationConfig emailNotificationConfig, boolean syncRefreshRestrictCheck) {

        if (syncRefreshRestrictCheck) {
            //do not permit sync if the configured duration time hasn't past since the last recorded sync operation
            syncRefreshRestrictCheck();
        }

        String syncToken = UUID.randomUUID().toString();

        //kick off syncServiceFacade thread
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("IdxSync-%d")
                .setDaemon(false)
                .build();

        ExecutorService executorService = Executors.newSingleThreadExecutor(threadFactory);
        executorService.submit(() -> {

            SyncFullStat syncFullStat = syncServiceFacade.syncIdxData(syncToken);

            if (emailNotificationConfig != null) {
                sendSyncStatEmailReport(emailNotificationConfig, syncFullStat);
            }
        });

        return syncToken;
    }

    /**
     * Perform sync refresh restriction check:
     *  do not permit sync if the configured duration time hasn't past since the last recorded sync operation.
     */
    private void syncRefreshRestrictCheck() {
        //find most recent sync timestamp
        SyncFullStat mostRecentSync = null;
        if (syncFullStatRepository.count() > 0) {
            Iterable<SyncFullStat> stats = syncFullStatRepository.findAllByStatusOrderByUpdatedDesc(COMPLETE);
            Iterator<SyncFullStat> iterator = stats.iterator();
            if (iterator.hasNext()) {
                mostRecentSync = iterator.next();
            }
        }

        if (mostRecentSync != null) {
            long syncRefreshDurationMS = syncRefreshRestrictDurationMinutes * 60 * 1000;
            Date restrictionExpiresDate = new Date(mostRecentSync.getSyncStartTime().getTime() + syncRefreshDurationMS);

            //if restriction expiration hasn't passed yet
            if (new Date().before(restrictionExpiresDate)) {

                LocalDateTime dt = LocalDateTime.ofInstant(restrictionExpiresDate.toInstant(), ZoneId.systemDefault());
                Duration duration = Duration.between(LocalDateTime.now(), dt);

                String errMsg = String.format("A re-sync with the RETS server is not permitted until %d minutes has passed. Remaining time: %s", syncRefreshRestrictDurationMinutes, duration);

                logger.info(errMsg);

                throw new SyncNotPermittedException(errMsg);
            }
        }
    }

    public SyncFullStat getSyncFullStatRecord(String syncToken) {
        return syncFullStatRepository.findBySyncToken(syncToken);
    }

    private void sendSyncStatEmailReport(EmailNotificationConfig emailNotificationConfig, SyncFullStat syncFullStat) {

        //excludeStatusComplete will send an email if status is something other than complete / success
        if (emailNotificationConfig.isExcludeStatusComplete() && !SyncStatus.COMPLETE.equals(syncFullStat.getStatus()))
            return;

        SimpleMailMessage msg = new SimpleMailMessage(this.syncReportEmailTemplate);

        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(true);
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ObjectMapper jsonMapper = builder.build();

        try{

            msg.setTo(emailNotificationConfig.getNotificationEmailAddress());
            msg.setText(jsonMapper.writeValueAsString(syncFullStat));

            logger.info("Emailing sync report for token ({}) to {}", syncFullStat.getSyncToken(),
                    emailNotificationConfig.getNotificationEmailAddress());
            this.mailSender.send(msg);
        }
        catch (MailException | JsonProcessingException e) {
            logger.error("An error occurred while attempting to mail sync report.", e);
        }
    }
}

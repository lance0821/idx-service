package idxsync.sync.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SyncSchedulerService implements BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(SyncSchedulerService.class);

    private BeanFactory beanFactory;

    @Value("${sync.idx.refresh.check.period.minutes}")
    private int syncSchedulerPeriodMinutes;

    @Value("${sync.idx.refresh.check.delay.minutes}")
    private int syncSchedulerDelayMinutes;

    @Value("${sync.idx.sync.stat.report.email.address}")
    private String syncStatReportEmailAddress;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void initSyncScheduler() {

        logger.info("Initializing sync scheduler - (delay: {} minutes, period: {} minutes, report email address: {})",
                syncSchedulerDelayMinutes, syncSchedulerPeriodMinutes, syncStatReportEmailAddress);

        scheduler.scheduleAtFixedRate(createSyncSchedulerHandler(),
                syncSchedulerDelayMinutes, syncSchedulerPeriodMinutes, TimeUnit.MINUTES);
    }

    public SyncSchedulerHandler createSyncSchedulerHandler() {
        return (SyncSchedulerHandler) beanFactory.getBean("syncSchedulerHandler");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}

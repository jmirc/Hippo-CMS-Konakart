package org.onehippo.forge.konakart.replication.service;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.Plugin;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.konakart.replication.synchronization.HippoKonakartDaemonModule;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

public class KonakartSynchronizationService extends Plugin {

    private static Logger log = LoggerFactory.getLogger(KonakartSynchronizationService.class);

    public static final String KK_CONTENT_ROOT = "kkContentRoot";
    private static final String MASS_SYNC_JOB = "KonkartMassSyncJob";
    private static final String MASS_SYNC_JOB_TRIGGER = MASS_SYNC_JOB + "Trigger";
    private static final String MASS_SYNC_JOB_TRIGGER_GROUP = MASS_SYNC_JOB_TRIGGER + "Group";
    private static final String MASS_SYNC_JOB_GROUP = MASS_SYNC_JOB + "Group";

    protected Scheduler resourceScheduler;

    public KonakartSynchronizationService(IPluginContext context, IPluginConfig config) {
        super(context, config);

        initScheduler();
        initializeJobs();
    }

    private void initScheduler() {
        this.resourceScheduler = HippoKonakartDaemonModule.getScheduler();
    }

    /**
     * Initialize each job.
     */
    private void initializeJobs() {
        Collection<KKStoreConfig> kkStoreConfigs = HippoModuleConfig.load(HippoKonakartDaemonModule.getSession()).getStoresConfig().values();

        for (KKStoreConfig kkStoreConfig : kkStoreConfigs) {
            try {
                JobDetail jobDetail = new JobDetail(MASS_SYNC_JOB, MASS_SYNC_JOB_GROUP, Class.forName(kkStoreConfig.getJobClass()));

                JobDataMap dataMap = new JobDataMap();
                dataMap.put(KK_CONTENT_ROOT, kkStoreConfig.getContentRoot());
                jobDetail.setJobDataMap(dataMap);

                if (StringUtils.isNotEmpty(kkStoreConfig.getCronExpression())) {
                    CronTrigger trigger = new CronTrigger(MASS_SYNC_JOB_TRIGGER, MASS_SYNC_JOB_TRIGGER_GROUP,
                            MASS_SYNC_JOB, MASS_SYNC_JOB_GROUP, kkStoreConfig.getCronExpression());

                    if (triggerExists(trigger)) {
                        if (triggerChanged(trigger)) {
                            resourceScheduler.rescheduleJob(MASS_SYNC_JOB_TRIGGER, MASS_SYNC_JOB_TRIGGER_GROUP, trigger);
                        }
                    } else {
                        resourceScheduler.scheduleJob(jobDetail, trigger);
                    }
                } else {
                    // Create a trigger that fires immediately, the repeats every 60 seconds, forever
                    Trigger trigger = new SimpleTrigger(MASS_SYNC_JOB_TRIGGER, MASS_SYNC_JOB_TRIGGER_GROUP, new Date(),
                            null, SimpleTrigger.REPEAT_INDEFINITELY, 300L * 1000L);

                    resourceScheduler.scheduleJob(jobDetail, trigger);
                }

            } catch (ClassNotFoundException e) {
                log.error("Failed to find the Job class named - " + kkStoreConfig.getJobClass(), e);
            } catch (ParseException e) {
                log.error("Failed to parse the cron expression - " + kkStoreConfig.getCronExpression(), e);
            } catch (SchedulerException e) {
                log.error("Failed to start the job", e);
            }
        }
    }

    protected boolean triggerExists(Trigger checkTrigger) {
        try {
            Trigger trigger = resourceScheduler.getTrigger(checkTrigger.getName(), checkTrigger.getGroup());
            return (trigger != null);
        } catch (SchedulerException e) {
            log.error("Failed to check if the trigger exists", e);
        }
        return false;
    }

    protected boolean triggerChanged(Trigger checkTrigger) {
        try {
            Trigger trigger = resourceScheduler.getTrigger(checkTrigger.getName(), checkTrigger.getGroup());
            if (trigger != null && trigger instanceof CronTrigger) {
                return !((CronTrigger) trigger).getCronExpression().equals(((CronTrigger) checkTrigger).getCronExpression());
            }
        } catch (SchedulerException e) {
            log.error("Failed to check if the trigger has changed", e);
        }
        return false;
    }


}

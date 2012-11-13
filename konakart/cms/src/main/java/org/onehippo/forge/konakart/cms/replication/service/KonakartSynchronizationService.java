package org.onehippo.forge.konakart.cms.replication.service;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.Plugin;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.konakart.cms.replication.synchronization.HippoKonakartDaemonModule;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

public class KonakartSynchronizationService extends Plugin {

    private static Logger log = LoggerFactory.getLogger(KonakartSynchronizationService.class);

    private static final String MASS_SYNC_JOB_ONCE = "KonkartMassSyncJobOnce";
    private static final String MASS_SYNC_JOB = "KonkartMassSyncJob";
    private static final String MASS_SYNC_JOB_TRIGGER_ONCE = MASS_SYNC_JOB + "TriggerOnce";
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
     *
     */
    private void initializeJobs() {
        Collection<KKStoreConfig> kkStoreConfigs = HippoModuleConfig.load(HippoKonakartDaemonModule.getSession()).getStoresConfig().values();

        if (kkStoreConfigs.size() == 0) {
            log.error("No store config has been found. Please check the konakart:konakart configuration");
            return;
        }

        // Get the first one.
        KKStoreConfig kkStoreConfig = kkStoreConfigs.iterator().next();

        try {
            String triggerName = MASS_SYNC_JOB_TRIGGER;
            String jobName = MASS_SYNC_JOB_ONCE;

            JobDetail jobDetail = new JobDetail(jobName, MASS_SYNC_JOB_GROUP, Class.forName(kkStoreConfig.getJobClass()));

            JobDataMap dataMap = new JobDataMap();
            jobDetail.setJobDataMap(dataMap);


            // Create a trigger that fires immediately, and run only once
            Trigger trigger = new SimpleTrigger(MASS_SYNC_JOB_TRIGGER_ONCE);
            trigger.setStartTime(new Date(System.currentTimeMillis() + 10000));

            if (!triggerExists(trigger)) {
                resourceScheduler.scheduleJob(jobDetail, trigger);
            }

            jobName = MASS_SYNC_JOB;
            jobDetail = new JobDetail(jobName, MASS_SYNC_JOB_GROUP, Class.forName(kkStoreConfig.getJobClass()));

            // Create the cron task. The first launch is scheduled after 30 minutes
            if (StringUtils.isNotEmpty(kkStoreConfig.getCronExpression())) {
                trigger = new CronTrigger(triggerName, MASS_SYNC_JOB_TRIGGER_GROUP,
                        jobName, MASS_SYNC_JOB_GROUP, kkStoreConfig.getCronExpression());

                trigger.setStartTime(new Date(System.currentTimeMillis() + 1000));

                if (triggerExists(trigger)) {
                    if (triggerChanged(trigger)) {
                        resourceScheduler.rescheduleJob(MASS_SYNC_JOB_TRIGGER, MASS_SYNC_JOB_TRIGGER_GROUP, trigger);
                    }
                } else {
                    resourceScheduler.scheduleJob(jobDetail, trigger);
                }
            }

        } catch (ClassNotFoundException e) {
            log.error("Failed to find the Job class named - " + kkStoreConfig.getJobClass(), e);
        } catch (ParseException e) {
            log.error("Failed to parse the cron expression - " + kkStoreConfig.getCronExpression(), e);
        } catch (SchedulerException e) {
            log.error("Failed to start the job", e);
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

package org.onehippo.forge.konakart.cms.replication.service;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.Plugin;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.service.ServiceTracker;
import org.hippoecm.frontend.translation.ILocaleProvider;
import org.onehippo.forge.konakart.cms.replication.synchronization.HippoKonakartDaemonModule;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class KonakartSynchronizationService extends Plugin {

    private static Logger log = LoggerFactory.getLogger(KonakartSynchronizationService.class);

    public static final String LOCALES = "locales";
    private static final String MASS_SYNC_JOB = "KonkartMassSyncJob";
    private static final String MASS_SYNC_JOB_TRIGGER = MASS_SYNC_JOB + "Trigger";
    private static final String MASS_SYNC_JOB_TRIGGER_GROUP = MASS_SYNC_JOB_TRIGGER + "Group";
    private static final String MASS_SYNC_JOB_GROUP = MASS_SYNC_JOB + "Group";

    protected Scheduler resourceScheduler;

    public KonakartSynchronizationService(IPluginContext context, IPluginConfig config) {
        super(context, config);


        // Retrieve the locale provider
        context.registerTracker(new ServiceTracker<ILocaleProvider>(ILocaleProvider.class) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onServiceAdded(ILocaleProvider service, String name) {
                initScheduler();
                initializeJobs(service.getLocales());

                super.onServiceAdded(service, name);
            }

            @Override
            protected void onRemoveService(ILocaleProvider service, String name) {
                super.onRemoveService(service, name);
            }
        }, config.getString("locale.id", ILocaleProvider.class.getName()));


    }

    private void initScheduler() {
        this.resourceScheduler = HippoKonakartDaemonModule.getScheduler();
    }

    /**
     * Initialize each job.
     *
     * @param locales list of availables locales
     */
    private void initializeJobs(List<? extends ILocaleProvider.HippoLocale> locales) {
        Collection<KKStoreConfig> kkStoreConfigs = HippoModuleConfig.load(HippoKonakartDaemonModule.getSession()).getStoresConfig().values();

        if (kkStoreConfigs.size() == 0) {
            log.error("No store config has been found. Please check the konakart:konakart configuration");
            return;
        }

        // Get the first one.
        KKStoreConfig kkStoreConfig = kkStoreConfigs.iterator().next();

        try {
            String triggerName = MASS_SYNC_JOB_TRIGGER;
            String jobName = MASS_SYNC_JOB;

            JobDetail jobDetail = new JobDetail(jobName, MASS_SYNC_JOB_GROUP, Class.forName(kkStoreConfig.getJobClass()));

            JobDataMap dataMap = new JobDataMap();
            dataMap.put(LOCALES, locales);
            jobDetail.setJobDataMap(dataMap);


            if (StringUtils.isNotEmpty(kkStoreConfig.getCronExpression())) {
                CronTrigger trigger = new CronTrigger(triggerName, MASS_SYNC_JOB_TRIGGER_GROUP,
                        jobName, MASS_SYNC_JOB_GROUP, kkStoreConfig.getCronExpression());

                if (triggerExists(trigger)) {
                    if (triggerChanged(trigger)) {
                        resourceScheduler.rescheduleJob(MASS_SYNC_JOB_TRIGGER, MASS_SYNC_JOB_TRIGGER_GROUP, trigger);
                    }
                } else {
                    resourceScheduler.scheduleJob(jobDetail, trigger);
                }
            } else {
                // Create a trigger that fires immediately, the repeats every 60 seconds, forever
                Trigger trigger = new SimpleTrigger(triggerName, MASS_SYNC_JOB_TRIGGER_GROUP, new Date(),
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

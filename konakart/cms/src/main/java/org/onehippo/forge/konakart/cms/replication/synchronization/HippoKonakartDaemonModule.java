package org.onehippo.forge.konakart.cms.replication.synchronization;

import org.hippoecm.repository.ext.DaemonModule;
import org.hippoecm.repository.quartz.JCRSchedulingContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.core.QuartzScheduler;
import org.quartz.core.QuartzSchedulerResources;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Properties;

public class HippoKonakartDaemonModule implements DaemonModule {

    private static Logger log = LoggerFactory.getLogger(HippoKonakartDaemonModule.class);

    static Session session = null;
    static KonakartResourceScheduler resourceScheduler = null;
    static KonakartResourceSchedulerFactory schedFactory = null;


    @Override
    public void initialize(Session session) throws RepositoryException {
        HippoKonakartDaemonModule.session = session;

        log.info("***Initialized HippoKonakartDaemonModule for quartz schedualar***");

        Properties properties = new Properties();
        try {
            properties.put("org.quartz.scheduler.instanceName", "Hippo Konakart Resource Quartz Job Scheduler");
            properties.put("org.quartz.scheduler.instanceName", "EXRES1");
            properties.put("org.quartz.scheduler.instanceId", "AUTO");
            properties.put("org.quartz.scheduler.skipUpdateCheck", "true");
            properties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
            properties.put("org.quartz.threadPool.threadCount", "1");
            properties.put("org.quartz.threadPool.threadPriority", "5");
            schedFactory = new KonakartResourceSchedulerFactory(session);
            schedFactory.initialize(properties);
            resourceScheduler = (KonakartResourceScheduler) schedFactory.getScheduler();
            resourceScheduler.start();
        } catch (SchedulerException ex) {
            log.error("Failed to start the quartz scheduler", ex);
        }


    }

    public void shutdown() {
        if (resourceScheduler != null) {
            resourceScheduler.shutdown(true);
        }
        session.logout();
    }

    public static KonakartResourceScheduler getScheduler() {
        return new KonakartResourceScheduler(resourceScheduler, session);
    }

    public static Session getSession() {
        return session;
    }


    public static class KonakartResourceSchedulerFactory extends StdSchedulerFactory {
        private Properties props;
        private Session session;

        public KonakartResourceSchedulerFactory(Session session) throws SchedulerException {
            this.session = session;
        }

        @Override
        public void initialize(Properties props) throws SchedulerException {
            this.props = new Properties(props);
            super.initialize(props);
        }

        @Override
        protected Scheduler instantiate(QuartzSchedulerResources rsrcs, QuartzScheduler qs) {
            JCRSchedulingContext schedCtxt = new JCRSchedulingContext(session);
            schedCtxt.setInstanceId(rsrcs.getInstanceId());
            return new KonakartResourceScheduler(qs, schedCtxt);
        }
    }
}

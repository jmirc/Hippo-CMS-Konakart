package org.onehippo.forge.konakart.cms.replication.synchronization;

import org.hippoecm.repository.quartz.JCRSchedulingContext;
import org.quartz.Scheduler;
import org.quartz.core.QuartzScheduler;
import org.quartz.core.SchedulingContext;
import org.quartz.impl.StdScheduler;

import javax.jcr.Session;

public class KonakartResourceScheduler extends StdScheduler implements Scheduler {

  private QuartzScheduler qs;
  private SchedulingContext ctx;


  public KonakartResourceScheduler(QuartzScheduler sched, SchedulingContext schedCtxt) {
    super(sched, schedCtxt);

    this.qs = sched;
    this.ctx = schedCtxt;
  }

  public KonakartResourceScheduler(KonakartResourceScheduler sched, Session session) {
    super(sched.qs, new JCRSchedulingContext(sched.ctx, session));
  }

  public SchedulingContext getCtx() {
    return ctx;
  }

}
